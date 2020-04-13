package com.senpro.jafrabackend.controllers;

import com.senpro.jafrabackend.config.JWTTokenUtil;
import com.senpro.jafrabackend.models.authentication.JWTRequest;
import com.senpro.jafrabackend.models.authentication.JWTResponse;
import com.senpro.jafrabackend.services.UserService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@CrossOrigin
public class AuthenticationController {

  private AuthenticationManager authenticationManager;
  private JWTTokenUtil jwtTokenUtil;
  private UserService userService;

  @Autowired
  public AuthenticationController(
      AuthenticationManager authenticationManager,
      JWTTokenUtil jwtTokenUtil,
      UserService userService) {
    this.authenticationManager = authenticationManager;
    this.jwtTokenUtil = jwtTokenUtil;
    this.userService = userService;
  }

  // JWT implemented with the help of
  // https://www.javainuse.com/spring/boot-jwt
  @PostMapping("/authenticate")
  public ResponseEntity<JWTResponse> createAuthenticationToken(
      @RequestBody JWTRequest authenticationRequest) throws Exception {

    authenticate(authenticationRequest.getUsername(), authenticationRequest.getPassword());

    final UserDetails userDetails =
        userService.loadUserByUsername(authenticationRequest.getUsername());

    final String token = jwtTokenUtil.generateToken(userDetails);

    return ResponseEntity.ok(new JWTResponse(token));
  }

  private void authenticate(String username, String password) throws Exception {
    try {
      authenticationManager.authenticate(
          new UsernamePasswordAuthenticationToken(username, password));
    } catch (DisabledException e) {
      throw new Exception("USER_DISABLED", e);
    } catch (BadCredentialsException e) {
      throw new Exception("INVALID_CREDENTIALS", e);
    }
  }
}
