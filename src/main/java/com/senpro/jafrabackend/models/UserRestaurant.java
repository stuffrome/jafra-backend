package com.senpro.jafrabackend.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "UserRestaurant")
public class UserRestaurant {
    @Id private String username;
    private double latitude;
    private double longitude;
    private List<String> restaurantIds;

    public UserRestaurant(String username, double latitude, double longitude) {
        this.username = username;
        this.latitude = latitude;
        this.longitude = longitude;
        this.restaurantIds = new ArrayList<>();
    }
}
