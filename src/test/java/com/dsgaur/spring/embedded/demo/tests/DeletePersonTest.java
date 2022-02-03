package com.dsgaur.spring.embedded.demo.tests;

import com.dsgaur.spring.embedded.demo.data.Person;
import com.dsgaur.spring.embedded.demo.data.PersonRepository;
import com.dsgaur.spring.embedded.demo.service.PersonsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest
public class DeletePersonTest {

    @Resource
    private PersonRepository personRepository;

    @Resource
    private PersonsService personsService;

    private Person person;

    @BeforeEach
    public void before() {
        Person person = new Person("rahul", "rahul@example.com");
        this.person = personRepository.save(person);
    }

    /**
     * Tests that when person is deleted, the person is actually deleted from the database.
     */
    @Test
    public void onDeleteCall_personShouldBeDeletedFromDb() {
        personsService.deletePerson(person.getEmail());

        assertFalse(personRepository.existsById(person.getEmail()));
    }
}
