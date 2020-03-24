package com.senpro.jafrabackend.repositories;

import com.senpro.jafrabackend.models.user.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
  boolean existsByUsername(String username);

  boolean existsByEmail(String email);
}
