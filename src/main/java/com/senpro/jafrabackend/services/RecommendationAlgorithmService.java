package com.senpro.jafrabackend.services;

import com.senpro.jafrabackend.enums.Price;
import com.senpro.jafrabackend.models.user.preferences.CuisinePreference;
import com.senpro.jafrabackend.models.user.preferences.DistancePreference;
import com.senpro.jafrabackend.models.user.preferences.PricePreference;
import com.senpro.jafrabackend.models.user.preferences.RatingPreference;
import com.senpro.jafrabackend.models.yelp.Restaurant;
import com.senpro.jafrabackend.models.yelp.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class RecommendationAlgorithmService {
  private final double DISTANCE_WEIGHT = 10;
  private final double PRICE_WEIGHT = 10;
  private final double RATING_WEIGHT = 8;

  @Autowired
  public RecommendationAlgorithmService() {}

  public List<Restaurant> sortRestaurants(
      List<Restaurant> restaurants,
      List<CuisinePreference> userCuisinePreferences,
      // DistancePreference userDistancePreference,
      PricePreference userPricePreference,
      RatingPreference userRatingPreference) {

    Map<Double, Restaurant> scores = new TreeMap<Double, Restaurant>(Collections.reverseOrder());
    for (Restaurant restaurant : restaurants) {
      //System.out.println("testing: " + restaurant.getAlias());
      double score = 0;
      // check every cuisine preference
      for (CuisinePreference cuisinePreference : userCuisinePreferences) {
        // check every category associated with the restaurant
        for (Category category : restaurant.getCategories()) {
          // if restaurant's category is the same as cuisine preference, add score
          if (cuisinePreference.getCuisineAlias().equals(category.getAlias())) {
            score += cuisinePreference.getPreferenceWeight();
          }
        }
        // TODO: check parent category
      }
      // add distance factors to score
      // only adds a score if less than avg dist
      // higher score if closer, proportionally

      double proportion = 1.0 - restaurant.getDistance() / 10000;
      double distanceScore = (proportion * proportion) * DISTANCE_WEIGHT;
      score += distanceScore;

      // adds price factors to the score
      // higher if prices are similar
      if (restaurant.getPrice() != null) {
        double priceDist =
            priceDistance(restaurant.getPrice(), userPricePreference.getPreferenceWeight());
        double priceProp = 1.0 - priceDist / 3.0;
        double priceScore = (priceProp * priceProp) * PRICE_WEIGHT;
        score += priceScore;
      }

      // adds rating factors to score
      // if rating lower than your preferred rating, proportionally add a lower score (closer to
      // preferred rating is higher)
      // if equal, just add weight,
      // if higher, proportionally add twice the weight
      if (restaurant.getRating() != null) {
        if (restaurant.getRating() < userRatingPreference.getPreferenceWeight()) {
          double ratingProp = restaurant.getRating() / userRatingPreference.getPreferenceWeight();
          double ratingScore = (ratingProp * ratingProp) * RATING_WEIGHT;
          score += ratingScore;

        } else if (restaurant.getRating() == userRatingPreference.getPreferenceWeight()) {
          score += RATING_WEIGHT;
        } else {
          double ratingProp =
              1.0
                  - ((5.01 - restaurant.getRating())
                      / (5.01 - userRatingPreference.getPreferenceWeight()));
          double ratingScore = (ratingProp * ratingProp) * (RATING_WEIGHT * 2);
          score += ratingScore;
        }
      }
      //System.out.println("Score :" + score);
      scores.put(score, restaurant);
    }

    for (double s : scores.keySet()) {
        System.out.println("" + s + ": " + scores.get(s).getName());
    }
    List<Restaurant> scoredRests = new ArrayList<Restaurant>(scores.values());
    return scoredRests;
  }

  private double priceDistance(Price p1, double p2) {
    double price1 = (double) p1.ordinal();
    return Math.abs(price1 - p2);
  }
}
