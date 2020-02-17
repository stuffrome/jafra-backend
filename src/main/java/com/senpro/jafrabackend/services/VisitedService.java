package com.senpro.jafrabackend.services;

import com.senpro.jafrabackend.exceptions.EntityExistsException;
import com.senpro.jafrabackend.exceptions.EntityNotFoundException;
import com.senpro.jafrabackend.exceptions.InvalidNameException;
import com.senpro.jafrabackend.models.user.User;
import com.senpro.jafrabackend.models.user.VisitedRestaurant;
import com.senpro.jafrabackend.repositories.VisitedRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class VisitedService {
  private VisitedRepository visitedRepository;

  @Autowired
  public VisitedService(VisitedRepository visitedRepository) {
    this.visitedRepository = visitedRepository;
  }

  // Adds a visited to the database
  public void addVisitedRestaurant(String username, String restaurantID, float userRating)
      throws InvalidNameException, EntityExistsException {
    VisitedRestaurant visited = new VisitedRestaurant();
    visited.setRestaurantID(restaurantID);
    visited.setUsername(username);
    visited.setUserRating(userRating);
    validateVisited(visited);
    visitedRepository.save(visited);
  }

  // Returns all users in the database
  public List<VisitedRestaurant> getVisitedRestaurants() throws EntityNotFoundException {
    List<VisitedRestaurant> visited = visitedRepository.findAll();
    if (visited == null) throw new EntityNotFoundException("Visited");
    return visited;
  }

  // Finds a visited by userID
  public List<VisitedRestaurant> findByUsername(String username) throws EntityNotFoundException {
    List<VisitedRestaurant> visited = visitedRepository.getAllByUsername(username);
    if (visited == null) throw new EntityNotFoundException("Visited");
    return visited;
  }

  // Finds a visited by ID
  public VisitedRestaurant findById(String username, String restaurantID)
      throws EntityNotFoundException {
    VisitedRestaurant visited =
        visitedRepository.getByUsernameAndAndRestaurantID(username, restaurantID);
    if (visited == null) throw new EntityNotFoundException("Visited");
    return visited;
  }

  private void validateVisited(VisitedRestaurant visited)
      throws InvalidNameException, EntityExistsException {
    if (visitedRepository.existsByRestaurantIDAndUsername(
        visited.getRestaurantID(), visited.getUsername())) {
      throw new EntityExistsException(
          "VisitedRestaurant with restaurantId "
              + visited.getRestaurantID()
              + " and userID "
              + visited.getUsername());
    }
  }
}
