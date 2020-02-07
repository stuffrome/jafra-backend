package com.senpro.jafrabackend.models.yelp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BusinessResponseWrapper {
  private List<Business> businesses;
}
