package com.senpro.jafrabackend.models.yelp;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.senpro.jafrabackend.enums.Price;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

/*
 *  This object contains the same fields the Yelp API Business object contains
 *  https://api.yelp.com/v3/businesses/search
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Restaurant {
  private String id;
  private String alias;
  private String name;
  @JsonAlias(value = "image_url")
  private String imageUrl;
  @JsonAlias(value = "is_closed")
  private Boolean isClosed;
  @JsonAlias(value = "review_count")
  private long reviewCount;
  private List<Category> categories;
  private Double rating;
  private Price price;
  private double distance;
}
