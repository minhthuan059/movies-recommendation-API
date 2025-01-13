package com.example.movies_recommendation_API.people;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PeopleRepository extends MongoRepository<People, Long>{
    @Query("{ 'id': ?0 }")
    People findOneById(@Param("id") Integer id);

    Page<People> findAll(Pageable pageable);
}
