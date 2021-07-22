package de.darkatra;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import feign.Feign;
import feign.FeignException;
import feign.gson.GsonDecoder;
import feign.micrometer.MicrometerCapability;

class PersonClientTest {

    private static final WireMockServer server = new WireMockServer(new WireMockConfiguration().dynamicPort());

    @BeforeAll
    static void beforeAll() {
        server.start();
    }

    @AfterAll
    static void afterAll() {
        server.stop();
    }

    @Test
    void shouldGetPersons() {

        final PersonClient personClient = getPersonClient(true);

        server.stubFor(get(urlPathEqualTo("/persons"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("[{ \"firstName\": \"Testi\", \"lastName\": \"Tester\", \"age\": \"20\" }]")
                        .withStatus(200)));

        final List<Person> persons = personClient.getPersons();

        assertThat(persons).hasSize(1);
        assertThat(persons.get(0).getFirstName()).isEqualTo("Testi");
        assertThat(persons.get(0).getLastName()).isEqualTo("Tester");
        assertThat(persons.get(0).getAge()).isEqualTo(20);
    }

    @Test
    void shouldThrowExceptionWithoutCustomErrorDecoder() {

        final PersonClient personClient = getPersonClient(false);

        server.stubFor(get(urlPathEqualTo("/persons"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withStatus(500)));

        assertThrows(FeignException.class, personClient::getPersons);
    }

    @Test
    void shouldThrowExceptionWithCustomErrorDecoder() {

        final PersonClient personClient = getPersonClient(true);

        server.stubFor(get(urlPathEqualTo("/persons"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withStatus(500)));

        assertThrows(ClientException.class, personClient::getPersons);
    }

    private PersonClient getPersonClient(final boolean withCustomErrorDecoder) {

        final Feign.Builder builder = new Feign.Builder()
                .decoder(new GsonDecoder())
                .addCapability(new MicrometerCapability());

        if (withCustomErrorDecoder) {
            builder.errorDecoder(new PersonClientErrorDecoder());
        }

        return builder.target(PersonClient.class, "http://localhost:" + server.port());
    }
}
