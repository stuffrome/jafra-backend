package com.senpro.jafrabackend.models;

import com.senpro.jafrabackend.models.yelp.Restaurant;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class RecommendedRestaurantResponse {
    List<Restaurant> restaurants;
    int pageSize;
    int pageNumber;
    int numResults;
}
