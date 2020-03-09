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
  private final double CUISINE_WEIGHT = 1.5;
  private final double DISTANCE_WEIGHT = 5;
  private final double PRICE_WEIGHT = 2;
  private final double RATING_WEIGHT = 20;

  @Autowired
  public RecommendationAlgorithmService() {}

  public List<Restaurant> sortRestaurants(
      List<Restaurant> restaurants,
      List<CuisinePreference> userCuisinePreferences,
      // DistancePreference userDistancePreference,
      PricePreference userPricePreference,
      RatingPreference userRatingPreference) {

    Map<Double, Restaurant> scores = new TreeMap<Double, Restaurant>(Collections.reverseOrder());
    Map<Restaurant, List<Double>> weightsDebug = new HashMap<Restaurant, List<Double>>();

    System.out.println("User Preferences:\n\tPrice Preference: " + userPricePreference.getPreferenceWeight() + "\n\tRating Preference: " + userRatingPreference.getPreferenceWeight() + "\n\tCuisine Preferences:");
    for (CuisinePreference cp: userCuisinePreferences) {
        System.out.println("\t\t" + cp.getCuisineAlias() + ": " + cp.getPreferenceWeight());
    }
    System.out.println("Ranking " + restaurants.size() + " restaurants:");
    for (Restaurant restaurant : restaurants) {
      List<Double> weights = new ArrayList<Double>();
      //System.out.println("testing: " + restaurant.getAlias());
      double score = 0;
      // check every cuisine preference
      double cuisineScore = 0;
      for (CuisinePreference cuisinePreference : userCuisinePreferences) {
        // check every category associated with the restaurant
        for (Category category : restaurant.getCategories()) {
          // if restaurant's category is the same as cuisine preference, add score
          if (cuisinePreference.getCuisineAlias().equals(category.getAlias())) {
            score += cuisinePreference.getPreferenceWeight() * CUISINE_WEIGHT;
            cuisineScore += cuisinePreference.getPreferenceWeight() * CUISINE_WEIGHT;
          }
        }
        // TODO: check parent category
      }

      weights.add(weights.size(), cuisineScore);
      // add distance factors to score
      // only adds a score if less than avg dist
      // higher score if closer, proportionally

      double proportion = 1.0 - restaurant.getDistance() / 10000;
      double distanceScore = (proportion * proportion) * DISTANCE_WEIGHT;
      score += distanceScore;
      weights.add(weights.size(), distanceScore);

      // adds price factors to the score
      // higher if prices are similar
      if (restaurant.getPrice() != null) {
        double priceDist =
            priceDistance(restaurant.getPrice(), userPricePreference.getPreferenceWeight());
        double priceProp = 1.0 - priceDist / 3.0;
        double priceScore = (priceProp * priceProp) * PRICE_WEIGHT;
        score += priceScore;
        weights.add(weights.size(), priceScore);
      }
      else{
        weights.add(weights.size(), 0.0);
      }

      //rating weight is affected by the number of reviews
      double numReviewsAdjustedWeight = RATING_WEIGHT * (Math.pow(restaurant.getReviewCount(), 1/4)/5);

      // adds rating factors to score
      // if rating lower than your preferred rating, proportionally add a lower score (closer to
      // preferred rating is higher)
      // if equal, just add weight,
      // if higher, proportionally add twice the weight
      if (restaurant.getRating() != null) {
        if (restaurant.getRating() < userRatingPreference.getPreferenceWeight()) {
          double ratingProp = restaurant.getRating() / userRatingPreference.getPreferenceWeight();
          double ratingScore = (ratingProp * ratingProp) * numReviewsAdjustedWeight;
          score += ratingScore;
          weights.add(weights.size(), ratingScore);
        } else if (restaurant.getRating() == userRatingPreference.getPreferenceWeight()) {
          score += numReviewsAdjustedWeight;
          weights.add(weights.size(), numReviewsAdjustedWeight);
        } else {
          double ratingProp =
              1.0
                  - ((5.01 - restaurant.getRating())
                      / (5.01 - userRatingPreference.getPreferenceWeight()));
          double ratingScore = (ratingProp * ratingProp) * (numReviewsAdjustedWeight * 2);
          score += ratingScore;
          weights.add(weights.size(), ratingScore);
        }
      }
      //System.out.println("Score :" + score);
        while(scores.containsKey(score)){
            score -= 0.000001;
        }
      scores.put(score, restaurant);
        weightsDebug.put(restaurant, weights);
    }

    int i = 1;
    for (double s : scores.keySet()) {
        System.out.println("" + i + ": " + s + " = " + scores.get(s).getName());
        List<Double> weights = weightsDebug.get(scores.get(s));
        System.out.println("\tCuisine Weight: " + weights.get(0) + ", Distance Weight: " + weights.get(1) + ", Price Weight: " + weights.get(2) + ", Rating Weight: " + weights.get(3));
        i++;
    }
    List<Restaurant> scoredRests = new ArrayList<Restaurant>(scores.values());
    return scoredRests;
  }

  private double priceDistance(Price p1, double p2) {
    double price1 = (double) p1.ordinal();
    return Math.abs(price1 - p2);
  }
}
