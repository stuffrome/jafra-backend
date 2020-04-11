package com.senpro.jafrabackend.controllers;

import com.senpro.jafrabackend.exceptions.EntityExistsException;
import com.senpro.jafrabackend.exceptions.EntityNotFoundException;
import com.senpro.jafrabackend.exceptions.InvalidNameException;
import com.senpro.jafrabackend.models.user.VisitedRestaurant;
import com.senpro.jafrabackend.models.user.WishListEntry;
import com.senpro.jafrabackend.models.yelp.Restaurant;
import com.senpro.jafrabackend.services.RestaurantService;
import com.senpro.jafrabackend.services.UserService;
import com.senpro.jafrabackend.services.VisitedService;
import com.senpro.jafrabackend.services.WishListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/wishlist")
@CrossOrigin(origins = "https://jafra.herokuapp.com")
public class WishListController {
  private WishListService wishListService;
  private UserService userService;
  private RestaurantService restaurantService;

  @Autowired
  public WishListController(
      WishListService wishListService,
      UserService userService,
      RestaurantService restaurantService) {
    this.wishListService = wishListService;
    this.userService = userService;
    this.restaurantService = restaurantService;
  }

  @PostMapping
  public ResponseEntity<String> addWishListEntry(
      @RequestParam String username, @RequestParam String restaurantId)
      throws InvalidNameException, EntityExistsException, EntityNotFoundException {
    userService.findById(username);
    restaurantService.getRestaurantDetails(restaurantId);
    wishListService.addWishListEntry(username, restaurantId);
    return ResponseEntity.status(HttpStatus.CREATED).body("Success!");
  }

  @PostMapping("/remove")
  public ResponseEntity<String> removeWishListEntry(
      @RequestParam String username, @RequestParam String restaurantId)
      throws EntityNotFoundException {
    wishListService.removeWishListEntry(username, restaurantId);
    return ResponseEntity.status(HttpStatus.CREATED).body("Success!");
  }

  @GetMapping("/username")
  public ResponseEntity<List<Restaurant>> getWishListEntries(@RequestParam String username)
      throws EntityNotFoundException {
    return ResponseEntity.status(HttpStatus.OK)
        .body(
            restaurantService.formatRestaurants(
                wishListService.getRestaurantsFromList(
                    wishListService.getWishListEntries(username), username),
                username));
  }
}
