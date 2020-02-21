package com.senpro.jafrabackend.services;

import com.senpro.jafrabackend.exceptions.EntityNotFoundException;
import com.senpro.jafrabackend.models.UserRestaurant;
import com.senpro.jafrabackend.models.user.User;
import com.senpro.jafrabackend.models.yelp.Restaurant;
import com.senpro.jafrabackend.models.yelp.details.RestaurantDetails;
import com.senpro.jafrabackend.repositories.RestaurantRepository;
import com.senpro.jafrabackend.repositories.UserRestaurantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RestaurantService {

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

  // Updates the users recommended restaurants
  public void updateUserRestaurants(String username) throws EntityNotFoundException {
    // Throws exception if not found
    User user = userService.findById(username);
    updateUserRestaurants("restaurants", username, user.getLatitude(), user.getLongitude());
  }

  // Updates the user's recommended restaurants
  public void updateUserRestaurants(String category, String username)
      throws EntityNotFoundException {
    // Throws exception if not found
    User user = userService.findById(username);
    updateUserRestaurants(category, username, user.getLatitude(), user.getLongitude());
  }

  // Update's the user's recommended restaurants
  @Async
  public void updateUserRestaurants(
      String category, String username, double latitude, double longitude)
      throws EntityNotFoundException {

    // Throws exception if not found
    User user = userService.findById(username);
    List<Restaurant> rawRestaurants = new ArrayList<>();

    // Gets the first 150 restaurants
    rawRestaurants.addAll(getRestaurants(category, latitude, longitude, 10000, 0));
    rawRestaurants.addAll(getRestaurants(category, latitude, longitude, 10000, 50));
    rawRestaurants.addAll(getRestaurants(category, latitude, longitude, 10000, 100));

    // Sorts the restaurants
    List<Restaurant> sortedRestaurants =
        algorithmService.sortRestaurants(
            rawRestaurants,
            user.getCuisinePreferences(),
            user.getDistancePreference(),
            user.getPricePreference(),
            user.getRatingPreference());

    // Saves the sorted restaurants in the DB
    saveRestaurants(sortedRestaurants);
    saveUserRestaurants(sortedRestaurants, username);
  }

  // Saves all restaurant details in the DB
  private void saveRestaurants(List<Restaurant> restaurants) {
    // TODO ensure this saves unique restaurants and doesn't save duplicates
    this.restaurantRepository.saveAll(restaurants);
  }

  // Saves the users recommended restaurants in the DB
  private void saveUserRestaurants(List<Restaurant> restaurants, String username) {
    UserRestaurant userRestaurant = new UserRestaurant(username);
    List<String> restaurantIds = new ArrayList<>();
    for (Restaurant restaurant : restaurants) {
      restaurantIds.add(restaurant.getId());
    }
    // If the user already has saved restaurants in the DB, it will (should) get overwritten because
    // it is using the same ID
    userRestaurantRepository.save(userRestaurant);
  }

  // Returns a list of restaurants sorted by distance
  public List<Restaurant> getRestaurants(
      String categories, double latitude, double longitude, long radius, int offset)
      throws EntityNotFoundException {
    return apiService.getRestaurants(categories, latitude, longitude, radius, offset);
  }

  // Returns more details about a restaurant
  public RestaurantDetails getRestaurantDetails(String id) throws EntityNotFoundException {
    return apiService.getRestaurantDetails(id);
  }

  // Finds restaurants by name
  public List<Restaurant> findByName(String restaurantName, String username) throws EntityNotFoundException {
    User user = userService.findById(username);
    return apiService.findRestaurantByName(restaurantName, user.getLatitude(), user.getLongitude());
  }
}
