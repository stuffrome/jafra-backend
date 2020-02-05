package com.senpro.jafrabackend.controllers;

import com.senpro.jafrabackend.exceptions.EntityNotFoundException;
import com.senpro.jafrabackend.models.yelp.Business;
import com.senpro.jafrabackend.models.yelp.details.BusinessDetails;
import com.senpro.jafrabackend.services.YelpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/yelp")
public class YelpController {

    private YelpService yelpService;

    @Autowired
    public YelpController(YelpService yelpService) {
        this.yelpService = yelpService;
    }

    @GetMapping
    public ResponseEntity<List<Business>> getRestaurants(@RequestParam(required = false) String categories,
                                                         @RequestParam String latitude,
                                                         @RequestParam String longitude,
                                                         @RequestParam String radius) throws EntityNotFoundException {
        if (categories == null) categories = "restaurants";
        return ResponseEntity.status(HttpStatus.OK).body(yelpService.getRestaurants(categories, Long.parseLong(latitude), Long.parseLong(longitude), Long.parseLong(radius)));
    }

    @GetMapping("/details")
    public ResponseEntity<BusinessDetails> getRestaurantDetails(@RequestParam String id) throws EntityNotFoundException {
        return ResponseEntity.status(HttpStatus.OK).body(yelpService.getBusinessDetails(id));
    }
}
