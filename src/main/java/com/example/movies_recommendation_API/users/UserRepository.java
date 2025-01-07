package com.example.movies_recommendation_API.users;


import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
interface UserRepository extends MongoRepository<User, Long> {
    User findOneByUsername(String username);
    User findOneByGoogleId(String googleId);
    User findOneByEmail(@Param("email") String email);
    User findOneByUsernameAndGoogleId(String username, String googleId);

    @Query("{ 'email': ?0, '$or': [ { 'googleId': null }, { 'googleId': '' } ] }")
    User findOneByEmailAndGoogleIdIsEmpty(@Param("email") String email);

    @Query("{ 'username': ?0, '$or': [ { 'googleId': null }, { 'googleId': '' } ] }")
    User findOneByUsernameAndGoogleIdIsEmpty(@Param("username") String username);

    @Query("{ 'email': :#{#email}, 'googleId': { $exists: true, $ne: '' } }")
    User findOneByEmailAndGoogleIdIsNotEmpty(@Param("email") String email);

}

