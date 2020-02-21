package com.senpro.jafrabackend.controllers;

import com.senpro.jafrabackend.exceptions.EntityExistsException;
import com.senpro.jafrabackend.exceptions.EntityNotFoundException;
import com.senpro.jafrabackend.exceptions.InvalidNameException;
import com.senpro.jafrabackend.models.user.User;
import com.senpro.jafrabackend.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/users")
@CrossOrigin(origins = "https://jafra.herokuapp.com")
// @CrossOrigin(origins = "http://localhost:4200")
public class UserController {

  private UserService userService;

  @Autowired
  public UserController(UserService userService) {
    this.userService = userService;
  }

  @GetMapping
  public ResponseEntity<List<User>> getUsers() throws EntityNotFoundException {
    return ResponseEntity.status(HttpStatus.OK).body(userService.getUsers());
  }

  // Adds a user
  @PostMapping
  public ResponseEntity<String> addUser(
      @RequestParam String name,
      @RequestParam String email,
      @RequestParam String username,
      @RequestParam(required = false, defaultValue = "-1") String latitude,
      @RequestParam(required = false, defaultValue = "-1") String longitude)
      throws InvalidNameException, EntityExistsException, EntityNotFoundException {
    userService.addUser(name, email, username);
    if (latitude.equals("-1") || longitude.equals("-1"))
      userService.updateLatLon(
          username, Double.parseDouble(latitude), Double.parseDouble(longitude));
    return ResponseEntity.status(HttpStatus.CREATED).body("Success!");
  }

  @GetMapping("/id")
  public ResponseEntity<User> findUserById(@RequestParam String username)
      throws EntityNotFoundException {
    return ResponseEntity.status(HttpStatus.OK).body(userService.findById(username));
  }
}
