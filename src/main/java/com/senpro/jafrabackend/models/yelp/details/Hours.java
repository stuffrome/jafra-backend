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
public class Hours {
  private List<Open> open;
  @JsonAlias(value = "hours_type")
  private String hoursType;
  @JsonAlias(value = "is_open_now")
  private Boolean isOpenNow;
}
