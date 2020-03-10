package com.senpro.jafrabackend.services;

import com.senpro.jafrabackend.exceptions.EntityNotFoundException;
import com.senpro.jafrabackend.models.RecommendedRestaurantResponse;
import com.senpro.jafrabackend.models.UserRestaurant;
import com.senpro.jafrabackend.models.user.User;
import com.senpro.jafrabackend.models.yelp.Restaurant;
import com.senpro.jafrabackend.models.yelp.details.RestaurantDetails;
import com.senpro.jafrabackend.repositories.RestaurantRepository;
import com.senpro.jafrabackend.repositories.UserRestaurantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Future;

@Service
public class RestaurantService {

  private final double LAT_LON_ERROR_TOLERANCE = 0.0045;
  private final int DEFAULT_PAGE_SIZE = 10;

  private YelpService apiService;
  private UserService userService;
  private RecommendationAlgorithmService algorithmService;
  private UserRestaurantRepository userRestaurantRepository;
  private RestaurantRepository restaurantRepository;

  @Autowired
  public RestaurantService(
      YelpService yelpService,
      UserService userService,
      RecommendationAlgorithmService algorithmService,
      UserRestaurantRepository userRestaurantRepository,
      RestaurantRepository restaurantRepository) {
    this.apiService = yelpService;
    this.userService = userService;
    this.algorithmService = algorithmService;
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
    return paginateRestaurants(restaurants, pageSize, pageNumber);
  }

  private RecommendedRestaurantResponse getSortedRestaurantsFromCache(
      List<String> restaurantIds, String username, int pageNumber, int pageSize)
      throws EntityNotFoundException {

    List<Restaurant> restaurants = getUserRestaurants(restaurantIds);
    List<Restaurant> sortedRestaurants =
        sortRestaurants(restaurants, userService.findById(username));
    return paginateRestaurants(sortedRestaurants, pageSize, pageNumber);
  }

  private RecommendedRestaurantResponse paginateRestaurants(
      List<Restaurant> restaurants, int pageSize, int pageNumber) {
    List<Restaurant> pagedRestaurants = new ArrayList<>();
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
    return pageNumber > 0 && pageSize > 0 && pageNumber <= (pageSize / resultSize);
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

  private void clearCache(String username, List<String> restaurantIds) {
    userRestaurantRepository.deleteById(username);
    restaurantIds.forEach(rId -> restaurantRepository.deleteById(rId));
  }

  // Return the list of restaurants in the database for a user
  private List<Restaurant> getUserRestaurants(List<String> restaurantIds) {
    List<Restaurant> restaurants = new ArrayList<>();
    restaurantIds.forEach(id -> restaurantRepository.findById(id).ifPresent(restaurants::add));
    return restaurants;
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

    Future<List<Restaurant>> rawRestaurants1 =
        getRestaurants(category, latitude, longitude, 10000, 0);
    Future<List<Restaurant>> rawRestaurants2 =
        getRestaurants(category, latitude, longitude, 10000, 50);
    Future<List<Restaurant>> rawRestaurants3 =
        getRestaurants(category, latitude, longitude, 10000, 100);

    while (true) {
      if (rawRestaurants1.isDone() && rawRestaurants2.isDone() && rawRestaurants3.isDone()) {
        try {
          allRawRestaurants.addAll(rawRestaurants1.get());
          allRawRestaurants.addAll(rawRestaurants2.get());
          allRawRestaurants.addAll(rawRestaurants3.get());
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
}
