package com.pikel.balancetracker.entity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Find a user by their Google ID (the "sub" claim from Google's JWT).
     * This is how we identify if a user has logged in before.
     */
    Optional<User> findByGoogleId(String googleId);

    Optional<User> findByEmail(String email);
}