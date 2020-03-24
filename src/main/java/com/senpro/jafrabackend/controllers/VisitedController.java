package com.senpro.jafrabackend.controllers;

import com.senpro.jafrabackend.exceptions.EntityExistsException;
import com.senpro.jafrabackend.exceptions.EntityNotFoundException;
import com.senpro.jafrabackend.exceptions.InvalidNameException;
import com.senpro.jafrabackend.models.user.VisitedRestaurant;
import com.senpro.jafrabackend.services.RestaurantService;
import com.senpro.jafrabackend.services.UserService;
import com.senpro.jafrabackend.services.VisitedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/visited")
@CrossOrigin(origins = "https://jafra.herokuapp.com")
public class VisitedController {

  private VisitedService visitedService;
  private UserService userService;
  private RestaurantService restaurantService;

  @Autowired
  public VisitedController(VisitedService visitedService, UserService userService, RestaurantService restaurantService) {
    this.visitedService = visitedService;
    this.userService = userService;
    this.restaurantService = restaurantService;
  }

  @GetMapping
  public ResponseEntity<List<VisitedRestaurant>> getVisitedRestaurants()
      throws EntityNotFoundException {
    return ResponseEntity.status(HttpStatus.OK).body(visitedService.getVisitedRestaurants());
  }

  @PostMapping
  public ResponseEntity<String> addVisitedRestaurant(
      @RequestParam String username,
      @RequestParam String restaurantId,
      @RequestParam float userRating)
      throws InvalidNameException, EntityExistsException, EntityNotFoundException {
    userService.findById(username);
    restaurantService.getRestaurantDetails(restaurantId);
    visitedService.addVisitedRestaurant(username, restaurantId, userRating);
    return ResponseEntity.status(HttpStatus.CREATED).body("Success!");
  }

  @PostMapping("/id")
  public ResponseEntity<String> updateVisitedRestaurant(
      @RequestParam String username,
      @RequestParam String restaurantId,
      @RequestParam float userRating)
      throws EntityNotFoundException {
    visitedService.updateVisitedRestaurant(username, restaurantId, userRating);
    return ResponseEntity.status(HttpStatus.CREATED).body("Success!");
  }

  @GetMapping("/username")
  public ResponseEntity<List<VisitedRestaurant>> findById(@RequestParam String username)
      throws EntityNotFoundException {
    return ResponseEntity.status(HttpStatus.OK).body(visitedService.findByUsername(username));
  }

  @GetMapping("/id")
  public ResponseEntity<VisitedRestaurant> findById(
      @RequestParam String username, @RequestParam String restaurantID)
      throws EntityNotFoundException {
    return ResponseEntity.status(HttpStatus.OK)
        .body(visitedService.findById(username, restaurantID));
  }
}
