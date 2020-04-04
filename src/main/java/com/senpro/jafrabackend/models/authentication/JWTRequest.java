package com.senpro.jafrabackend.models.authentication;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class JWTRequest implements Serializable {

  private static final long serialVersionUID = 5926468583005150707L;
  private String username;
  private String password;
}
