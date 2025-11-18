package me.alex.polarbookshop.catalogservice;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import me.alex.polarbookshop.catalogservice.domain.Book;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.servlet.client.MockMvcWebTestClient;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CatalogServiceApplicationTests implements PostgresIT, KeycloakIT {
    private static KeycloakToken bjornTokens;
    private static KeycloakToken isabelleTokens;

    @Autowired
    WebApplicationContext applicationContext;

    WebTestClient webTestClient;

    @BeforeAll
    static void generateAccessTokens() {
        WebClient webClient = WebClient.builder()
                .baseUrl(keycloakContainer.getAuthServerUrl()
                        + "/realms/PolarBookshop/protocol/openid-connect/token")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .build();
        isabelleTokens = authenticateWith(
                "isabelle", "password", webClient);
        bjornTokens = authenticateWith(
                "bjorn", "password", webClient);
    }

    @BeforeEach
    void setup() {
        webTestClient = MockMvcWebTestClient.bindToApplicationContext(applicationContext).build();
    }

    private static KeycloakToken authenticateWith(
            String username, String password, WebClient webClient
    ) {
        return webClient
                .post()
                .body(
                        BodyInserters.fromFormData("grant_type", "password")
                                .with("client_id", "polar-test")
                                .with("username", username)
                                .with("password", password)
                )
                .retrieve()
                .bodyToMono(KeycloakToken.class)
                .block();
    }

    @Test
    void contextLoads() {
    }

    @Test
    void whenPostRequestThenBookCreated() {
        var expectedBook = Book.of("1231231231", "Title", "Author", 9.90, "Polarsophia");
        webTestClient.post().uri("/books")
                .headers(headers ->
                        headers.setBearerAuth(isabelleTokens.accessToken()))
                .bodyValue(expectedBook)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(Book.class).value(actualBook -> {
                    assertThat(actualBook).isNotNull();
                    assertThat(actualBook.isbn()).isEqualTo(expectedBook.isbn());
                });
    }

    @Test
    void whenPostRequestUnauthorizedThen403() {
        var expectedBook = Book.of("1231231231", "Title", "Author", 9.90, "Polarsophia");
        webTestClient.post().uri("/books")
                .headers(headers ->
                        headers.setBearerAuth(bjornTokens.accessToken()))
                .bodyValue(expectedBook)
                .exchange()
                .expectStatus().isForbidden();
    }

    @Test
    void whenPostRequestUnauthenticatedThen401() {
        var expectedBook = Book.of("1231231231", "Title", "Author", 9.90, "Polarsophia");
        webTestClient.post().uri("/books")
                .bodyValue(expectedBook)
                .exchange()
                .expectStatus().isUnauthorized();
    }

    private record KeycloakToken(String accessToken) {
        @JsonCreator
        private KeycloakToken(
                @JsonProperty("access_token") final String accessToken
        ) {
            this.accessToken = accessToken;
        }
    }
}
