package com.senpro.jafrabackend.repositories;

import com.senpro.jafrabackend.models.user.VisitedRestaurant;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VisitedRepository extends MongoRepository<VisitedRestaurant, String> {
  boolean existsByRestaurantIDAndUsername(String restaurantID, String username);

  List<VisitedRestaurant> getAllByUsername(String username);

  VisitedRestaurant getByUsernameAndAndRestaurantID(String restaurantID, String username);
}
