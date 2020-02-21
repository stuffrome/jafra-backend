package com.senpro.jafrabackend.services;

import com.senpro.jafrabackend.exceptions.EntityNotFoundException;
import com.senpro.jafrabackend.models.yelp.Restaurant;
import com.senpro.jafrabackend.models.yelp.RestaurantResponseWrapper;
import com.senpro.jafrabackend.models.yelp.details.RestaurantDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
@PropertySource("classpath:yelp.properties")
public class YelpService {

  @Value("${yelp.token}")
  private String authToken;

  @Value("${yelp.url}")
  private String baseUrl;

  private RestTemplate restTemplate;
  private HttpHeaders headers;

  @Autowired
  public YelpService(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  // Gets more details about a particular business
  public RestaurantDetails getRestaurantDetails(String id) throws EntityNotFoundException {

    String url = baseUrl + "/businesses/" + id;

    setHeaders();

    ResponseEntity<RestaurantDetails> response =
        restTemplate.exchange(
            url, HttpMethod.GET, new HttpEntity<>("parameters", headers), RestaurantDetails.class);

    RestaurantDetails businessDetails = response.getBody();
    if (businessDetails == null) throw new EntityNotFoundException("Restaurant");
    return businessDetails;
  }

  // Searches for restaurants using Yelp's API
  public List<Restaurant> getRestaurants(
      String categories, double latitude, double longitude, long radius, int offset)
      throws EntityNotFoundException {

    String url =
        baseUrl
            + "/businesses/search?latitude="
            + latitude
            + "&longitude="
            + longitude
            + "&radius="
            + radius
            + "&sort_by=distance&limit=50&categories="
            + categories
            + "$offset="
            + offset;

    setHeaders();
    ResponseEntity<RestaurantResponseWrapper> response =
        restTemplate.exchange(
            url,
            HttpMethod.GET,
            new HttpEntity<>("parameters", headers),
            RestaurantResponseWrapper.class);

    RestaurantResponseWrapper wrapper = response.getBody();
    validateResponse(wrapper);
    return wrapper.getRestaurants();
  }

  // Finds a restaurant by name. Radius is set to the max value, offset it set to 25 to keep result
  // object small
  public List<Restaurant> findRestaurantByName(String name, double latitude, double longitude)
      throws EntityNotFoundException {

    String url =
        baseUrl
            + "/businesses/search?latitude="
            + latitude
            + "&longitude="
            + longitude
            + "&radius="
            + 40000
            + "&sort_by=distance&limit=50&categories="
            + "restaurants"
            + "$offset="
            + 25
            + "&term="
            + name;

    setHeaders();
    ResponseEntity<RestaurantResponseWrapper> response =
        restTemplate.exchange(
            url,
            HttpMethod.GET,
            new HttpEntity<>("parameters", headers),
            RestaurantResponseWrapper.class);

    RestaurantResponseWrapper wrapper = response.getBody();
    validateResponse(wrapper);
    return wrapper.getRestaurants();
  }

  // Set's the headers
  private void setHeaders() {
    headers = new HttpHeaders();
    headers.add("Authorization", "Bearer " + authToken);
  }

  // Validates the response for the business search call
  private void validateResponse(RestaurantResponseWrapper wrapper) throws EntityNotFoundException {
    if (wrapper == null) throw new EntityNotFoundException("Restaurants");
    if (wrapper.getRestaurants() == null) throw new EntityNotFoundException("Restaurants");
    if (wrapper.getRestaurants().size() == 0) throw new EntityNotFoundException("Restaurants");
  }
}
