package de.darkatra;

import java.util.List;

import feign.Headers;
import feign.RequestLine;

public interface PersonClient {

    @RequestLine("GET /persons")
    @Headers("Content-Type: application/json")
    List<Person> getPersons();
}
