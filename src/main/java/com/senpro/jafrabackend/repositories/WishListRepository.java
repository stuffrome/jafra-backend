package com.senpro.jafrabackend.repositories;

import com.senpro.jafrabackend.models.user.VisitedRestaurant;
import com.senpro.jafrabackend.models.user.WishListEntry;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WishListRepository extends MongoRepository<WishListEntry, String> {
    boolean existsById_UsernameAndId_RestaurantId(String username, String restaurantId);
    List<WishListEntry> getAllById_Username(String username);
    WishListEntry getById_UsernameAndId_RestaurantId(String username, String restaurantId);
}
