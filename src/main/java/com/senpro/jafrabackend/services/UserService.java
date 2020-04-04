package com.senpro.jafrabackend.services;

import com.senpro.jafrabackend.exceptions.EntityExistsException;
import com.senpro.jafrabackend.exceptions.EntityNotFoundException;
import com.senpro.jafrabackend.exceptions.InvalidNameException;
import com.senpro.jafrabackend.models.user.User;
import com.senpro.jafrabackend.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class UserService implements UserDetailsService {

  private UserRepository userRepository;
//  private PasswordEncoder passwordEncoder;

  @Value("${jwt.secret}")
  private String jwtSecret;

  @Autowired
  public UserService(UserRepository userRepository) {
    this.userRepository = userRepository;
//    this.passwordEncoder = passwordEncoder;
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

    User user;
    try {
      user = findById(username);
    } catch (EntityNotFoundException e) {
      throw new UsernameNotFoundException(
          "User account with username: " + username + " not found.");
    }

    return new org.springframework.security.core.userdetails.User(
        user.getUsername(), user.getPassword(), new ArrayList<>());
  }

  // Returns all users in the database
  public List<User> getUsers() throws EntityNotFoundException {
    List<User> users = userRepository.findAll();
    if (users == null) throw new EntityNotFoundException("Users");
    return users;
  }

  // Adds a user to the database
  public void addUser(String username, String email, String password)
      throws InvalidNameException, EntityExistsException {
    BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    User user = new User(username, email, encoder.encode(password));
    validateUser(user);
    userRepository.save(user);
  }

  public void updateUser(User newUser) {
    userRepository.deleteById(newUser.getUsername());
    userRepository.save(newUser);
  }

  // Finds a user by ID
  public User findById(String username) throws EntityNotFoundException {
    Optional<User> optionalUser = userRepository.findById(username);
    return optionalUser.orElseThrow(
        () -> new EntityNotFoundException("User with username" + username));
  }

  // Validates user fields
  private void validateUser(User user) throws InvalidNameException, EntityExistsException {
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
