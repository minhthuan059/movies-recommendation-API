package com.example.movies_recommendation_API.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
interface UserRepository  extends  JpaRepository<User, Long>{
    User findOneByUsername(String username);
    User findOneByGoogleId(String googleId);
    User findOneByUsernameAndGoogleId(String username, String googleId);
    @Query("SELECT u FROM User u WHERE u.username = :username AND u.googleId IS NULL")
    User findOneByUsernameAndGoogleIdIsNull(@Param("username") String username);
}

