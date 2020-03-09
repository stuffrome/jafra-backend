package com.senpro.jafrabackend.services;

import com.senpro.jafrabackend.exceptions.EntityNotFoundException;
import com.senpro.jafrabackend.models.UserRestaurant;
import com.senpro.jafrabackend.models.user.User;
import com.senpro.jafrabackend.models.yelp.Restaurant;
import com.senpro.jafrabackend.models.yelp.details.RestaurantDetails;
import com.senpro.jafrabackend.repositories.RestaurantRepository;
import com.senpro.jafrabackend.repositories.UserRestaurantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class RestaurantService {

  private final double LAT_LON_ERROR_TOLERANCE = 0.0045;

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
  public List<Restaurant> getRecommendedRestaurants(
      String category, String username, double latitude, double longitude)
      throws EntityNotFoundException {
    Optional<UserRestaurant> userRestaurant = getUserRestaurantList(username);

    if (userRestaurant.isPresent()) {
      if (validateCache(
          userRestaurant.get().getLatitude(),
          userRestaurant.get().getLongitude(),
          latitude,
          longitude)) return getUserRestaurants(userRestaurant.get().getRestaurantIds());
      else clearCache(username, userRestaurant.get().getRestaurantIds());
    }
    return updateUserRestaurants(category, username, latitude, longitude);
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

  // TODO clear restaurants from DB as well?
  private void clearCache(String username, List<String> restaurantIds) {
    userRestaurantRepository.deleteById(username);
  }

  // Return the list of restaurants in the database for a user
  private List<Restaurant> getUserRestaurants(List<String> restaurantIds) {
    List<Restaurant> restaurants = new ArrayList<>();
    restaurantIds.forEach(id -> restaurantRepository.findById(id).ifPresent(restaurants::add));
    return restaurants;
  }

  // Update's the user's recommended restaurants
  private List<Restaurant> updateUserRestaurants(
      String category, String username, double latitude, double longitude)
      throws EntityNotFoundException {

    // Throws exception if not found
    User user = userService.findById(username);
    List<Restaurant> rawRestaurants = new ArrayList<Restaurant>();

    // Gets the first 150 restaurants
    rawRestaurants.addAll(getRestaurants(category, latitude, longitude, 10000, 0));
    rawRestaurants.addAll(getRestaurants(category, latitude, longitude, 10000, 50));
    rawRestaurants.addAll(getRestaurants(category, latitude, longitude, 10000, 100));

    // Sorts the restaurants
    List<Restaurant> sortedRestaurants =
        algorithmService.sortRestaurants(
            rawRestaurants,
            user.getCuisinePreferences(),
            //user.getDistancePreference(),
            user.getPricePreference(),
            user.getRatingPreference());

    // Saves the sorted restaurants in the DB
    saveRestaurants(sortedRestaurants);
    saveUserRestaurants(sortedRestaurants, username, latitude, longitude);
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
  public List<Restaurant> getRestaurants(
      String categories, double latitude, double longitude, long radius, int offset)
      throws EntityNotFoundException {
    return getRestaurants(categories, "", latitude, longitude, radius, offset);
  }

  // Returns more details about a restaurant
  public RestaurantDetails getRestaurantDetails(String id) throws EntityNotFoundException {
    return apiService.getRestaurantDetails(id);
  }
}
