package com.senpro.jafrabackend.services;

import com.senpro.jafrabackend.exceptions.EntityExistsException;
import com.senpro.jafrabackend.exceptions.EntityNotFoundException;
import com.senpro.jafrabackend.exceptions.InvalidNameException;
import com.senpro.jafrabackend.models.user.VisitedRestaurant;
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

  @Autowired
  public VisitedService(VisitedRepository visitedRepository, RestaurantService restaurantService) {
    this.visitedRepository = visitedRepository;
    this.restaurantService = restaurantService;
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
    // Update the user's recommended restaurants whenever preferences change
    restaurantService.updateUserRestaurants(username);
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
    // Update the user's recommended restaurants whenever preferences change
    restaurantService.updateUserRestaurants(username);
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
