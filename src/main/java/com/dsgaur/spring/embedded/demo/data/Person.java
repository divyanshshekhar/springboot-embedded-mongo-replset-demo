package com.dsgaur.spring.embedded.demo.data;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import javax.validation.constraints.NotNull;

/**
 * A simple class that holds details of a person.
 */
@Document(collection = "persons")
public class Person {
    @Id
    @NotNull
    private final String email;

    private final String name;

    public Person(@NonNull String email, @Nullable String name) {
        this.email = email;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }
}
