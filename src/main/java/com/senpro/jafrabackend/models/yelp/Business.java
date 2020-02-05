package com.senpro.jafrabackend.models.yelp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.senpro.jafrabackend.enums.Price;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/*
 *  This object contains the same fields the Yelp API Business object contains
 *  https://api.yelp.com/v3/businesses/search
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Business {
    String id;
    String alias;
    String name;
    String image_url;
    Boolean isClosed;
    long reviewCount;
    List<Category> categories;
    Double rating;
    Price price;
    double distance;
}
