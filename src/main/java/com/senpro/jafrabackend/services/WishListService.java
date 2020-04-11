package com.senpro.jafrabackend.services;

import com.senpro.jafrabackend.exceptions.EntityExistsException;
import com.senpro.jafrabackend.exceptions.EntityNotFoundException;
import com.senpro.jafrabackend.exceptions.InvalidNameException;
import com.senpro.jafrabackend.models.user.VisitedRestaurant;
import com.senpro.jafrabackend.models.user.WishListEntry;
import com.senpro.jafrabackend.models.yelp.Restaurant;
import com.senpro.jafrabackend.repositories.UserRepository;
import com.senpro.jafrabackend.repositories.WishListRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class WishListService {

    private WishListRepository wishListRepository;
    private RestaurantService restaurantService;

    @Autowired
    public WishListService(WishListRepository wishListRepository, RestaurantService restaurantService) {
        this.wishListRepository = wishListRepository;
        this.restaurantService = restaurantService;
    }

    public List<WishListEntry> getWishListEntries(String username) throws EntityNotFoundException {
        List<WishListEntry> wishListEntries = wishListRepository.getAllById_Username(username);
        if (wishListEntries == null) throw new EntityNotFoundException("WishList");
        return wishListEntries;
    }

    public List<Restaurant> getRestaurantsFromList(List<WishListEntry> entries, String username) throws EntityNotFoundException {
        List<Restaurant> restaurants = new ArrayList<>();
        for (WishListEntry entry : entries) {
            restaurants.add(restaurantService.findById(entry.getId().getRestaurantId(), username));
        }
        return restaurants;
    }

    public void addWishListEntry(String username, String restaurantId) throws InvalidNameException, EntityExistsException {
        WishListEntry wishListEntry = new WishListEntry();
        WishListEntry.WishListKey id = new WishListEntry.WishListKey();
        id.setUsername(username);
        id.setRestaurantId(restaurantId);
        wishListEntry.setId(id);
        wishListEntry.setListAddDate(new Date());
        validateWishListEntry(wishListEntry);
        wishListRepository.save(wishListEntry);
    }

    public void removeWishListEntry(String username, String restaurantId) throws EntityNotFoundException {
        validateRemoveWishListEntry(username, restaurantId);
        wishListRepository.deleteById_UsernameAndId_RestaurantId(username, restaurantId);
    }

    private void validateWishListEntry(WishListEntry wishListEntry)
            throws InvalidNameException, EntityExistsException {
        if (wishListRepository.existsById_UsernameAndId_RestaurantId(
                wishListEntry.getId().getUsername(), wishListEntry.getId().getRestaurantId())) {
            throw new EntityExistsException(
                    "WishListEntry with username "
                            + wishListEntry.getId().getUsername()
                            + " and restaurantId "
                            + wishListEntry.getId().getRestaurantId());
        }
    }

    private void validateRemoveWishListEntry(String username, String restaurantId)
            throws EntityNotFoundException {
        if (!wishListRepository.existsById_UsernameAndId_RestaurantId(
               username, restaurantId)) {
            throw new EntityNotFoundException(
                    "No WishListEntry with username "
                            + username
                            + " and restaurantId "
                            + restaurantId);
        }
    }
}
