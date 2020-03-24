package com.senpro.jafrabackend.repositories;

import com.senpro.jafrabackend.models.user.VisitedRestaurant;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VisitedRepository extends MongoRepository<VisitedRestaurant, String> {
  boolean existsById_UsernameAndId_RestaurantId(String username, String restaurantId);
  List<VisitedRestaurant> getAllById_Username(String username);
}
