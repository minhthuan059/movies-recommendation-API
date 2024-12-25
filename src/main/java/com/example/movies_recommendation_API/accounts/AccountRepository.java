package com.example.movies_recommendation_API.accounts;


import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
interface AccountRepository  extends MongoRepository<Account, Long> {
    Account findOneByUsername(String username);
    Account findOneByGoogleId(String googleId);
    Account findOneByEmail(@Param("email") String email);
    Account findOneByUsernameAndGoogleId(String username, String googleId);

    @Query("{ 'email': ?0, '$or': [ { 'googleId': null }, { 'googleId': '' } ] }")
    Account findOneByEmailAndGoogleIdIsEmpty(@Param("email") String email);

    @Query("{ 'username': ?0, '$or': [ { 'googleId': null }, { 'googleId': '' } ] }")
    Account findOneByUsernameAndGoogleIdIsEmpty(@Param("username") String username);

    @Query("{ 'email': :#{#email}, 'googleId': { $exists: true, $ne: '' } }")
    Account findOneByEmailAndGoogleIdIsNotEmpty(@Param("email") String email);

}

