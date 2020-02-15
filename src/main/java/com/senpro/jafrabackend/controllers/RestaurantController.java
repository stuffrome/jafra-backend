package com.senpro.jafrabackend.controllers;

import com.senpro.jafrabackend.exceptions.EntityNotFoundException;
import com.senpro.jafrabackend.models.yelp.Restaurant;
import com.senpro.jafrabackend.models.yelp.details.RestaurantDetails;
import com.senpro.jafrabackend.services.RestaurantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/restaurants")
public class RestaurantController {

  private RestaurantService restaurantService;

  @Autowired
  public RestaurantController(RestaurantService restaurantService) {
    this.restaurantService = restaurantService;
  }

  @GetMapping
  public ResponseEntity<List<Restaurant>> getRestaurants(
      @RequestParam(required = false, defaultValue = "restaurants") String categories,
      @RequestParam String latitude,
      @RequestParam String longitude,
      @RequestParam String radius)
      throws EntityNotFoundException {
    return ResponseEntity.status(HttpStatus.OK)
        .body(
            restaurantService.getRestaurants(
                categories,
                Long.parseLong(latitude),
                Long.parseLong(longitude),
                Long.parseLong(radius)));
  }

  @GetMapping("/details")
  public ResponseEntity<RestaurantDetails> getRestaurantDetails(@RequestParam String id)
      throws EntityNotFoundException {
    return ResponseEntity.status(HttpStatus.OK).body(restaurantService.getRestaurantDetails(id));
  }
}
