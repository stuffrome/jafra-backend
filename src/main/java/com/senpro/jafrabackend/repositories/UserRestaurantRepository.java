package com.senpro.jafrabackend.repositories;

import com.senpro.jafrabackend.models.UserRestaurant;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRestaurantRepository extends MongoRepository<UserRestaurant, String> {
    boolean existsByUsername(String username);
}