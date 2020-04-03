package com.senpro.jafrabackend.repositories;

import com.senpro.jafrabackend.models.yelp.Restaurant;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RestaurantRepository extends MongoRepository<Restaurant, String> {
}
