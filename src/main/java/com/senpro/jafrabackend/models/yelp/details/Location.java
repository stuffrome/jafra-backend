package com.senpro.jafrabackend.models.yelp.details;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Location {
  private String address1;
  private String address2;
  private String address3;
  private String city;
  private String zip_code;
  private String country;
  private String state;
  private List<String> displayAddress;
}
