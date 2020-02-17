package com.senpro.jafrabackend.services;

import com.senpro.jafrabackend.exceptions.EntityExistsException;
import com.senpro.jafrabackend.exceptions.EntityNotFoundException;
import com.senpro.jafrabackend.exceptions.InvalidNameException;
import com.senpro.jafrabackend.models.user.User;
import com.senpro.jafrabackend.models.user.preferences.CuisinePreference;
import com.senpro.jafrabackend.models.user.preferences.DistancePreference;
import com.senpro.jafrabackend.models.user.preferences.PricePreference;
import com.senpro.jafrabackend.models.user.preferences.RatingPreference;
import com.senpro.jafrabackend.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class UserService {

  private UserRepository userRepository;

  @Autowired
  public UserService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  // Returns all users in the database
  public List<User> getUsers() throws EntityNotFoundException {
    List<User> users = userRepository.findAll();
    if (users == null) throw new EntityNotFoundException("Users");
    return users;
  }

  // Adds a user to the database
  public void addUser(String name, String email, String username)
      throws InvalidNameException, EntityExistsException {
    User user = new User(name, email);
    user.setUsername(username);
    user.setHiddenRestaurants(new ArrayList<String>());
    user.setCuisinePreferences(new ArrayList<CuisinePreference>());
    user.setPricePreference(new PricePreference());
    user.setRatingPreference(new RatingPreference());
    user.setDistancePreference(new DistancePreference());
    validateUser(user);
    userRepository.save(user);
  }

  // Finds a user by ID
  public User findById(String id) throws EntityNotFoundException {
    Optional<User> optionalUser = userRepository.findById(id);
    return optionalUser.orElseThrow(() -> new EntityNotFoundException("User with id"));
  }

  // Validates user fields
  private void validateUser(User user) throws InvalidNameException, EntityExistsException {
    if (containsSpecialCharacters(user.getName())) {
      throw new InvalidNameException("Name " + user.getName() + " is invalid.");
    }
    if (!validEmail(user.getEmail())) {
      throw new InvalidNameException("Email " + user.getEmail() + " is invalid.");
    }
    if (userRepository.existsByEmail(user.getEmail())) {
      throw new EntityExistsException("User with email " + user.getEmail());
    }
    if (userRepository.existsByUsername(user.getUsername())) {
      throw new EntityExistsException("User with username " + user.getUsername());
    }
  }

  // Checks for special characters
  private boolean containsSpecialCharacters(String value) {
    Pattern pattern = Pattern.compile("[a-zA-Z0-9 ]*");
    Matcher matcher = pattern.matcher(value);
    return !matcher.matches();
  }

  // Validates email format
  /*
   *  Taken directly from:
   *  https://www.geeksforgeeks.org/check-email-address-valid-not-java/
   */
  private boolean validEmail(String email) {
    String emailRegex =
        "^[a-zA-Z0-9_+&*-]+(?:\\."
            + "[a-zA-Z0-9_+&*-]+)*@"
            + "(?:[a-zA-Z0-9-]+\\.)+[a-z"
            + "A-Z]{2,7}$";

    Pattern pat = Pattern.compile(emailRegex);
    if (email == null) return false;
    return pat.matcher(email).matches();
  }
}
