package com.senpro.jafrabackend.models.user;

import lombok.Data;

import java.util.Date;

@Data
public class VisitedRestaurant {
    private String restaurant;
    private float userRating;
    private Date reviewDate;
}
