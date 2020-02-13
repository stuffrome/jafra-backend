package com.senpro.jafrabackend.models.yelp.details;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Open {
  @JsonAlias(value = "is_overnight")
  private Boolean isOvernight;
  private String start;
  private String end;
  private long day;
}
