package com.dsgaur.spring.embedded.demo.data;

import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Mongodb repository to create, update, read and delete person data from mongo database.
 */
public interface PersonRepository extends MongoRepository<Person, String> {

}
