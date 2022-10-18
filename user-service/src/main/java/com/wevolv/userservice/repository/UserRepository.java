package com.wevolv.userservice.repository;

import com.wevolv.userservice.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
    @Query("{ 'email' : ?0 }")
    Optional<User> findByEmail(String email);

    @Query("{'keycloakId' : ?0 }")
    Optional<User> findByKeycloakId(String keycloakId);

}
