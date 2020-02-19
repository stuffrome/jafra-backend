package com.senpro.jafrabackend.repositories;

import com.senpro.jafrabackend.models.user.RecommendedRestaurantList;
import com.senpro.jafrabackend.models.user.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecommendedRepository extends MongoRepository<RecommendedRestaurantList, String> {

}
