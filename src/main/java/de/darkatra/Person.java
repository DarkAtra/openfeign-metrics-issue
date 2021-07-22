package de.darkatra;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class Person {

    private final String firstName;
    private final String lastName;
    private final int age;
}
