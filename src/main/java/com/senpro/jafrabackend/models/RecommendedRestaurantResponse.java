package com.senpro.jafrabackend.models;

import com.senpro.jafrabackend.models.yelp.Restaurant;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class RecommendedRestaurantResponse {
    private List<Restaurant> restaurants;
    private int pageSize;
    private int pageNumber;
    private int numResults;
}
