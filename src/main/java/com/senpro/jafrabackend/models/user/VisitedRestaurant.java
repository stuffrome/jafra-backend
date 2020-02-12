package com.senpro.jafrabackend.models.user;

import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@Document(collection = "visited")
public class VisitedRestaurant {
    private ObjectId userID;
    private String restaurantID;
    private float userRating;
    private Date reviewDate;
}
