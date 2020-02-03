package com.senpro.jafrabackend.services;

import com.senpro.jafrabackend.exceptions.EntityNotFoundException;
import com.senpro.jafrabackend.models.yelp.Business;
import com.senpro.jafrabackend.models.yelp.BusinessResponseWrapper;
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
@PropertySource("classpath:credentials.properties")
public class YelpService {

    @Value("${yelp.token}")
    private String authToken;
    private String baseUrl;
    private RestTemplate restTemplate;
    private HttpHeaders headers;

    @Autowired
    public YelpService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        baseUrl = "https://api.yelp.com/v3";
    }

    public List<Business> getRestaurants(String type, long latitude, long longitude) throws EntityNotFoundException {

        String url =
                baseUrl +
                        "/businesses/search?term=food&latitude=" +
                        latitude +
                        "&longitude=" +
                        longitude +
                        "&radius=4000&sort_by=distance&limit=10&categories=" +
                        type;

        setHeaders();
        ResponseEntity<BusinessResponseWrapper> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                new HttpEntity<>("parameters", headers),
                BusinessResponseWrapper.class
        );

        BusinessResponseWrapper businessWrapper = response.getBody();
        validateResponse(businessWrapper);
        return businessWrapper.getBusinesses();
    }

    private void setHeaders() {
        headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + authToken);
    }

    private void validateResponse(BusinessResponseWrapper wrapper) throws EntityNotFoundException {
        if (wrapper == null) throw new EntityNotFoundException("Restaurants");
        if (wrapper.getBusinesses() == null) throw new EntityNotFoundException("Restaurants");
        if (wrapper.getBusinesses().size() == 0) throw new EntityNotFoundException("Restaurants");
    }
}
