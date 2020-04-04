package com.senpro.jafrabackend.controllers;

import com.senpro.jafrabackend.config.JWTTokenUtil;
import com.senpro.jafrabackend.exceptions.EntityExistsException;
import com.senpro.jafrabackend.exceptions.EntityNotFoundException;
import com.senpro.jafrabackend.exceptions.InvalidNameException;
import com.senpro.jafrabackend.models.authentication.JWTResponse;
import com.senpro.jafrabackend.models.user.User;
import com.senpro.jafrabackend.models.user.UserAccountRequest;
import com.senpro.jafrabackend.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/users")
@CrossOrigin
public class UserController {

  private UserService userService;
  private JWTTokenUtil jwtTokenUtil;

  @Autowired
  public UserController(UserService userService, JWTTokenUtil jwtTokenUtil) {
    this.userService = userService;
    this.jwtTokenUtil = jwtTokenUtil;
  }

  @GetMapping
  public ResponseEntity<List<User>> getUsers() throws EntityNotFoundException {
    return ResponseEntity.status(HttpStatus.OK).body(userService.getUsers());
  }

  // Adds a user
  @PostMapping("/create")
  public ResponseEntity<JWTResponse> addUser(@RequestBody UserAccountRequest newUserAccount)
      throws InvalidNameException, EntityExistsException, EntityNotFoundException {
    userService.addUser(
        newUserAccount.getUsername(), newUserAccount.getEmail(), newUserAccount.getPassword());

    final UserDetails userDetails =
            userService.loadUserByUsername(newUserAccount.getUsername());

    final String token = jwtTokenUtil.generateToken(userDetails);

    return ResponseEntity.ok(new JWTResponse(token));
  }

  @GetMapping("/id")
  public ResponseEntity<User> findUserById(@RequestParam String username)
      throws EntityNotFoundException {
    return ResponseEntity.status(HttpStatus.OK).body(userService.findById(username));
  }
}
