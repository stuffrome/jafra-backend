package com.senpro.jafrabackend.services;

import com.senpro.jafrabackend.exceptions.EntityNotFoundException;
import com.senpro.jafrabackend.models.RecommendedRestaurantResponse;
import com.senpro.jafrabackend.models.UserRestaurant;
import com.senpro.jafrabackend.models.user.User;
import com.senpro.jafrabackend.models.user.VisitedRestaurant;
import com.senpro.jafrabackend.models.user.WishListEntry;
import com.senpro.jafrabackend.models.yelp.Restaurant;
import com.senpro.jafrabackend.models.yelp.details.RestaurantDetails;
import com.senpro.jafrabackend.repositories.RestaurantRepository;
import com.senpro.jafrabackend.repositories.UserRestaurantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

@Service
public class RestaurantService {

  private final double LAT_LON_ERROR_TOLERANCE = 0.0045;
  private final int DEFAULT_PAGE_SIZE = 10;
  private final int NUM_YELP_CALLS = 3;

  private YelpService apiService;
  private UserService userService;
  private VisitedService visitedService;
  private WishListService wishListService;
  private RecommendationAlgorithmService algorithmService;
  private CacheService cacheService;
  private UserRestaurantRepository userRestaurantRepository;
  private RestaurantRepository restaurantRepository;

  @Autowired
  public RestaurantService(
      YelpService yelpService,
      UserService userService,
      @Lazy VisitedService visitedService,
      @Lazy WishListService wishListService,
      RecommendationAlgorithmService algorithmService,
      CacheService cacheService,
      UserRestaurantRepository userRestaurantRepository,
      RestaurantRepository restaurantRepository) {
    this.apiService = yelpService;
    this.userService = userService;
    this.visitedService = visitedService;
    this.wishListService = wishListService;
    this.algorithmService = algorithmService;
    this.cacheService = cacheService;
    this.userRestaurantRepository = userRestaurantRepository;
    this.restaurantRepository = restaurantRepository;
  }

  // Returns the list of recommended restaurants
  public RecommendedRestaurantResponse getRecommendedRestaurants(
      String category,
      String username,
      double latitude,
      double longitude,
      int pageNumber,
      int pageSize)
      throws EntityNotFoundException {
    Optional<UserRestaurant> userRestaurant = getUserRestaurantList(username);

    if (userRestaurant.isPresent()) {
      if (validateCache(
          userRestaurant.get().getLatitude(),
          userRestaurant.get().getLongitude(),
          latitude,
          longitude)) {
        return getSortedRestaurantsFromCache(
            userRestaurant.get().getRestaurantIds(), username, pageNumber, pageSize);
      } else clearCache(username, userRestaurant.get().getRestaurantIds());
    }
    List<Restaurant> restaurants = updateUserRestaurants(category, username, latitude, longitude);
    return paginateRestaurants(
        filterOutVisited(formatRestaurants(restaurants, username)), pageSize, pageNumber);
  }

  private RecommendedRestaurantResponse getSortedRestaurantsFromCache(
      List<String> restaurantIds, String username, int pageNumber, int pageSize)
      throws EntityNotFoundException {

    List<Restaurant> restaurants =
        filterOutVisited(formatRestaurants(getUserRestaurants(restaurantIds, username), username));
    List<Restaurant> sortedRestaurants =
        sortRestaurants(restaurants, userService.findById(username));
    return paginateRestaurants(sortedRestaurants, pageSize, pageNumber);
  }

  private RecommendedRestaurantResponse paginateRestaurants(
      List<Restaurant> restaurants, int pSize, int pNumber) {
    List<Restaurant> pagedRestaurants = new ArrayList<>();

    int pageSize = pSize;
    int pageNumber = pNumber;

    if (!validatePageParameters(pageSize, pageNumber, restaurants.size())) {
      pageSize = DEFAULT_PAGE_SIZE;
      pageNumber = 1;
    }
    for (int i = 0; i < restaurants.size(); ++i) {
      if (i >= (pageNumber - 1) * pageSize && i < pageNumber * pageSize)
        pagedRestaurants.add(restaurants.get(i));
    }
    return new RecommendedRestaurantResponse(
        pagedRestaurants, pageSize, pageNumber, restaurants.size());
  }

  private boolean validatePageParameters(int pageSize, int pageNumber, int resultSize) {
    return pageNumber > 0 && pageSize > 0 && pageNumber <= (resultSize / pageSize);
  }

  private Optional<UserRestaurant> getUserRestaurantList(String username) {
    return userRestaurantRepository.findById(username);
  }

  // Validates the cache of user restaurants
  private boolean validateCache(
      double oldLatitude, double oldLongitude, double newLatitude, double newLongitude) {
    return Math.abs(oldLatitude - newLatitude) <= LAT_LON_ERROR_TOLERANCE
        && Math.abs(oldLongitude - newLongitude) <= LAT_LON_ERROR_TOLERANCE;
  }

  @CacheEvict("restaurants")
  public void clearCache(String username, List<String> restaurantIds) {
    cacheService.clearCache(username);
    userRestaurantRepository.deleteById(username);
    restaurantIds.forEach(rId -> restaurantRepository.deleteById(rId));
  }

  // Return the list of restaurants in the database for a user
  @Cacheable(value = "restaurants", key = "#username")
  public List<Restaurant> getUserRestaurants(List<String> restaurantIds, String username) {
    return cacheService.getRestaurants(restaurantIds, username);
  }

  private List<Restaurant> sortRestaurants(List<Restaurant> restaurants, User user) {
    // Sorts the sorted restaurants
    return algorithmService.sortRestaurants(
        restaurants,
        user.getCuisinePreferences(),
        // user.getDistancePreference(),
        user.getPricePreference(),
        user.getRatingPreference());
  }

  @Async
  public void saveRestaurants(
      List<Restaurant> restaurants, String username, double latitude, double longitude) {
    // Saves the sorted restaurants in the DB
    saveRestaurants(restaurants);
    saveUserRestaurants(restaurants, username, latitude, longitude);
  }

  // Update's the user's recommended restaurants
  private List<Restaurant> updateUserRestaurants(
      String category, String username, double latitude, double longitude)
      throws EntityNotFoundException {

    // Throws exception if not found
    User user = userService.findById(username);
    List<Restaurant> allRawRestaurants = new ArrayList<>();

    List<Future<List<Restaurant>>> rawRestaurants = new ArrayList<>();

    for (int i = 0; i < NUM_YELP_CALLS; i++) {
      rawRestaurants.add(getRestaurants(category, latitude, longitude, 10000, 50 * i));
    }
    /*
    Future<List<Restaurant>> rawRestaurants1 =
        getRestaurants(category, latitude, longitude, 10000, 0);
    Future<List<Restaurant>> rawRestaurants2 =
        getRestaurants(category, latitude, longitude, 10000, 50);
    Future<List<Restaurant>> rawRestaurants3 =
        getRestaurants(category, latitude, longitude, 10000, 100); */

    while (true) {
      boolean done = true;
      for (int i = 0; i < NUM_YELP_CALLS; i++) {
        if (!rawRestaurants.get(i).isDone()) {
          done = false;
        }
      }
      if (done) {
        try {
          for (int i = 0; i < NUM_YELP_CALLS; i++) {
            allRawRestaurants.addAll(rawRestaurants.get(i).get());
          }
          break;
        } catch (Exception e) {
          System.out.println("error" + e);
        }
      }
    }

    // Sorts the restaurants
    List<Restaurant> sortedRestaurants = sortRestaurants(allRawRestaurants, user);

    // Saves the sorted restaurants in the DB
    saveRestaurants(sortedRestaurants, username, latitude, longitude);
    return sortedRestaurants;
  }

  // Saves all restaurant details in the DB
  private void saveRestaurants(List<Restaurant> restaurants) {
    // TODO ensure this saves unique restaurants and doesn't save duplicates
    this.restaurantRepository.saveAll(restaurants);
  }

  // Saves the users recommended restaurants in the DB
  private void saveUserRestaurants(
      List<Restaurant> restaurants, String username, double latitude, double longitude) {
    UserRestaurant userRestaurant = new UserRestaurant(username, latitude, longitude);
    List<String> restaurantIds = new ArrayList<>();
    restaurants.forEach(restaurant -> restaurantIds.add(restaurant.getId()));
    userRestaurant.setRestaurantIds(restaurantIds);
    // If the user already has saved restaurants in the DB, it will (should) get overwritten because
    // it is using the same ID
    userRestaurantRepository.save(userRestaurant);
  }

  // Returns a list of restaurants sorted by distance
  public List<Restaurant> getRestaurants(
      String categories,
      String restaurantName,
      double latitude,
      double longitude,
      long radius,
      int offset)
      throws EntityNotFoundException {
    return apiService.getRestaurants(
        categories, restaurantName, latitude, longitude, radius, offset);
  }

  // Returns a list of restaurants sorted by distance
  @Async
  public Future<List<Restaurant>> getRestaurants(
      String categories, double latitude, double longitude, long radius, int offset)
      throws EntityNotFoundException {
    return new AsyncResult<>(getRestaurants(categories, "", latitude, longitude, radius, offset));
  }

  // Returns more details about a restaurant
  public RestaurantDetails getRestaurantDetails(String id) throws EntityNotFoundException {
    return apiService.getRestaurantDetails(id);
  }

  public Restaurant findById(String restaurantId, String username) throws EntityNotFoundException {
    if ("".equals(username)) return getRestaurantDetails(restaurantId);
    UserRestaurant userRestaurant =
        userRestaurantRepository
            .findById(username)
            .orElseThrow(() -> new EntityNotFoundException("User"));
    if (userRestaurant.getRestaurantIds().contains(restaurantId))
      return restaurantRepository
          .findById(restaurantId)
          .orElseThrow(() -> new EntityNotFoundException("Restaurant"));
    return getRestaurantDetails(restaurantId);
  }

  public Restaurant findById(String restaurantId) throws EntityNotFoundException {
    return findById(restaurantId, "");
  }

  // Filters out visited restaurants
  public List<Restaurant> filterOutVisited(List<Restaurant> restaurants) {
    return restaurants.stream()
        .filter(restaurant -> !restaurant.isVisited())
        .collect(Collectors.toList());
  }

  // Adds user specific fields to restaurants before returning them to the user
  public List<Restaurant> formatRestaurants(List<Restaurant> restaurants, String username)
      throws EntityNotFoundException {
    List<VisitedRestaurant> visitedRestaurants = visitedService.findByUsername(username);
    List<WishListEntry> entries = wishListService.getWishListEntries(username);

    for (Restaurant restaurant : restaurants) {
      // Set both ton false initially
      restaurant.setVisited(false);
      restaurant.setWishList(false);
      // Check to see if the restaurant has been visited
      for (VisitedRestaurant vR : visitedRestaurants) {
        if (restaurant.getId().equals(vR.getId().getRestaurantId())) {
          restaurant.setVisited(true);
          restaurant.setUserRating(vR.getUserRating());
          restaurant.setUserReviewDate(vR.getReviewDate());
          // Remove from the list (small speed op)
          visitedRestaurants.remove(vR);
          break;
        }
      }
      // Check to see if the restaurant is on the wish list
      for (WishListEntry entry : entries) {
        if (restaurant.getId().equals(entry.getId().getRestaurantId())) {
          restaurant.setWishList(true);
          // Remove from the list (small speed op)
          entries.remove(entry);
          break;
        }
      }
    }
    return restaurants;
  }
}
