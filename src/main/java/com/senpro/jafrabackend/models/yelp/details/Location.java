package com.senpro.jafrabackend.models.yelp.details;

import com.fasterxml.jackson.annotation.JsonAlias;
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
  @JsonAlias(value = "zip_code")
  private String zipCode;
  private String country;
  private String state;
  @JsonAlias(value = "display_address")
  private List<String> displayAddress;
}
