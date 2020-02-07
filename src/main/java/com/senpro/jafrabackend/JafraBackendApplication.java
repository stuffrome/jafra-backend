package com.senpro.jafrabackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class JafraBackendApplication {

  public static void main(String[] args) {
    SpringApplication.run(JafraBackendApplication.class, args);
  }

  // Creates the RestTemplate to make API calls
  @Bean
  public RestTemplate yelpRestTemplate(RestTemplateBuilder builder) {
    return builder.build();
  }
}
