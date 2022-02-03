package com.dsgaur.spring.embedded.demo.service;

import com.dsgaur.spring.embedded.demo.data.Person;
import com.dsgaur.spring.embedded.demo.data.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@RestController
@RequestMapping(path = "/")
@Transactional
public class PersonsService {

    private final PersonRepository personRepository;

    @Autowired
    public PersonsService(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    /**
     * Create a new user with given details if another user with same email does not exist.
     *
     * @param personToCreate The person object to create
     * @return Response code 409 if other person with same email already exists, or 201 if person was successfully
     * created.
     */
    @PostMapping("/persons/")
    public ResponseEntity<Void> createPersons(@RequestBody @NotNull @Valid Person personToCreate) {
        if (personRepository.existsById(personToCreate.getEmail())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        personRepository.save(personToCreate);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * Deletes the user with the given email if the user with given email exists.
     *
     * @param email The email of the person who is to be deleted
     * @return Response code 204 if a person with given email exists, else 404.
     */
    @DeleteMapping("/person/{email}")
    public ResponseEntity<Void> deletePerson(@NotNull @PathVariable String email) {
        if (!personRepository.existsById(email)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        personRepository.deleteById(email);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
