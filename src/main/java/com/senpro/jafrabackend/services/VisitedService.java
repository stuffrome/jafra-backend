package com.senpro.jafrabackend.services;

import com.senpro.jafrabackend.exceptions.EntityExistsException;
import com.senpro.jafrabackend.exceptions.EntityNotFoundException;
import com.senpro.jafrabackend.exceptions.InvalidNameException;
import com.senpro.jafrabackend.models.user.User;
import com.senpro.jafrabackend.models.user.VisitedRestaurant;
import com.senpro.jafrabackend.models.user.preferences.CuisinePreference;
import com.senpro.jafrabackend.models.yelp.Category;
import com.senpro.jafrabackend.models.yelp.Restaurant;
import com.senpro.jafrabackend.repositories.VisitedRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class VisitedService {

  private VisitedRepository visitedRepository;
  private RestaurantService restaurantService;
  private UserService userService;

  @Autowired
  public VisitedService(
      VisitedRepository visitedRepository,
      RestaurantService restaurantService,
      UserService userService) {
    this.visitedRepository = visitedRepository;
    this.restaurantService = restaurantService;
    this.userService = userService;
  }

  // Adds a visited to the database
  public void addVisitedRestaurant(String username, String restaurantId, float userRating)
      throws InvalidNameException, EntityExistsException, EntityNotFoundException {
    VisitedRestaurant visited = new VisitedRestaurant();
    VisitedRestaurant.VisitedKey id = new VisitedRestaurant.VisitedKey();
    id.setUsername(username);
    id.setRestaurantId(restaurantId);
    visited.setId(id);
    visited.setUserRating(userRating);
    visited.setReviewDate(new Date());
    validateVisited(visited);
    visitedRepository.save(visited);
    updatePreferences(username, restaurantId, userRating);
  }

  private void updatePreferences(String username, String restaurantId, double userRating)
      throws EntityNotFoundException {
    User user = userService.findById(username);
    Restaurant restaurant = restaurantService.getRestaurantDetails(restaurantId);
    int numVisited = findByUsername(username).size();
    /*user.getDistancePreference()
        .setPreferenceWeight(getNewWeight(user.getDistancePreference().getPreferenceWeight(), restaurant.getDistance(), numVisited, userRating ));
    System.out.println("New dist: " + user.getDistancePreference().getPreferenceWeight());*/
    user.getPricePreference()
        .setPreferenceWeight(
            getNewWeight(
                user.getPricePreference().getPreferenceWeight(),
                restaurant.getPrice().ordinal() + 1,
                numVisited,
                userRating));
    System.out.println("New price: " + user.getPricePreference().getPreferenceWeight());
    user.getRatingPreference()
        .setPreferenceWeight(
            getNewWeight(
                user.getRatingPreference().getPreferenceWeight(),
                restaurant.getRating(),
                numVisited,
                userRating));
    System.out.println("New rating: " + user.getRatingPreference().getPreferenceWeight());
    List<CuisinePreference> cps = user.getCuisinePreferences();
    for (Category category : restaurant.getCategories()) {
      String alias = category.getAlias();
      boolean found = false;
      for (CuisinePreference cp : cps) {
        if (cp.getCuisineAlias().equals(alias)) {
          cp.setPreferenceWeight(cp.getPreferenceWeight() + Math.pow(userRating, 1.0/3) * 3);
          found = true;
        }
      }
      if (!found) {
        CuisinePreference newCP = new CuisinePreference();
        newCP.setCuisineAlias(alias);
        newCP.setPreferenceWeight(Math.sqrt(userRating));
        cps.add(newCP);
      }
    }
    cps = user.getCuisinePreferences();
    for (CuisinePreference cp : cps) {
      System.out.println(cp.getCuisineAlias() + ": " + cp.getPreferenceWeight());
    }
    userService.updateUser(user);
  }

  public double getNewWeight(double oldWeight, double newWeight, int numRs, double userRating) {
    /*System.out.println(
        "Old weight: "
            + oldWeight
            + "\nNew Weight: "
            + newWeight
            + "\nNumRs: "
            + numRs
            + "\nUser Rating: "
            + userRating);*/
    return (oldWeight - (Math.pow(userRating, 2) / (25 * numRs)) * (oldWeight - newWeight));
  }

  public void updateVisitedRestaurant(String username, String restaurantId, float userRating)
      throws EntityNotFoundException, EntityNotFoundException {
    VisitedRestaurant visited = new VisitedRestaurant();
    VisitedRestaurant.VisitedKey id = new VisitedRestaurant.VisitedKey();
    id.setUsername(username);
    id.setRestaurantId(restaurantId);
    visited.setId(id);
    visited.setUserRating(userRating);
    visited.setReviewDate(new Date());
    validateUpdate(visited);
    visitedRepository.deleteById(visited.getId().toString());
    visitedRepository.save(visited);
    updatePreferences(username, restaurantId, userRating);
  }
  // Returns all users in the database
  public List<VisitedRestaurant> getVisitedRestaurants() throws EntityNotFoundException {
    List<VisitedRestaurant> visited = visitedRepository.findAll();
    if (visited == null) throw new EntityNotFoundException("Visited");
    return visited;
  }

  // Finds a visited by userID
  public List<VisitedRestaurant> findByUsername(String username) throws EntityNotFoundException {
    List<VisitedRestaurant> visited = visitedRepository.getAllById_Username(username);
    if (visited == null) throw new EntityNotFoundException("Visited");
    return visited;
  }

  // Finds a visited by ID
  public VisitedRestaurant findById(String username, String restaurantId)
      throws EntityNotFoundException {
    VisitedRestaurant.VisitedKey id = new VisitedRestaurant.VisitedKey();
    id.setUsername(username);
    id.setRestaurantId(restaurantId);
    Optional<VisitedRestaurant> visited = visitedRepository.findById(id.toString());
    return visited.orElseThrow(
        () -> new EntityNotFoundException("Visited with id " + username + restaurantId));
  }

  private void validateVisited(VisitedRestaurant visited)
      throws InvalidNameException, EntityExistsException {
    if (visitedRepository.existsById_UsernameAndId_RestaurantId(
        visited.getId().getUsername(), visited.getId().getRestaurantId())) {
      throw new EntityExistsException(
          "VisitedRestaurant with username "
              + visited.getId().getUsername()
              + " and restaurantId "
              + visited.getId().getRestaurantId());
    }
  }

  private void validateUpdate(VisitedRestaurant visited) throws EntityNotFoundException {
    if (!visitedRepository.existsById_UsernameAndId_RestaurantId(
        visited.getId().getUsername(), visited.getId().getRestaurantId())) {
      throw new EntityNotFoundException(
          "VisitedRestaurant with username "
              + visited.getId().getUsername()
              + " and restaurantId "
              + visited.getId().getRestaurantId());
    }
  }
}
