package com.senpro.jafrabackend.services;

import com.senpro.jafrabackend.models.yelp.Restaurant;
import com.senpro.jafrabackend.repositories.RestaurantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CacheService {

  private RestaurantRepository restaurantRepository;

  @Autowired
  public CacheService(RestaurantRepository restaurantRepository) {
    this.restaurantRepository = restaurantRepository;
  }

  @CacheEvict(value = "restaurants", key = "#username")
  public void clearCache(String username) {}

  // Return the list of restaurants in the database for a user
  @Cacheable(value = "restaurants", key = "#username")
  public List<Restaurant> getRestaurants(List<String> restaurantIds, String username) {
    List<Restaurant> restaurants = new ArrayList<>();
    restaurantIds.forEach(id -> restaurantRepository.findById(id).ifPresent(restaurants::add));
    return restaurants;
  }
}
