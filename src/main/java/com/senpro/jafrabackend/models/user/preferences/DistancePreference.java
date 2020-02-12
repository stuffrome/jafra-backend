package com.senpro.jafrabackend.models.user.preferences;

import lombok.Data;

@Data
public class DistancePreference {
    //stores the average distance that the user enjoys traveling
    //will increase if user visits a restaurant further than avg and gives it a positive review
    private float preferenceWeight;
}
