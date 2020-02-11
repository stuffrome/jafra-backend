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
import org.springframework.web.bind.annotation.RequestBody;
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

  @PostMapping
  public ResponseEntity<String> addUser(@RequestBody User user)
      throws InvalidNameException, EntityExistsException {
    userService.addUser(user);
    return ResponseEntity.status(HttpStatus.CREATED).body("Success!");
  }

  @GetMapping("/id")
  public ResponseEntity<User> findUserById(@RequestParam String id) throws EntityNotFoundException {
    return ResponseEntity.status(HttpStatus.OK).body(userService.findById(id));
  }
}
