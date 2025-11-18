package me.alex.polarbookshop.catalogservice;

import dasniko.testcontainers.keycloak.KeycloakContainer;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;

public interface KeycloakIT {

    @Container
    KeycloakContainer keycloakContainer =
            new KeycloakContainer("quay.io/keycloak/keycloak:25.0")
                    .withRealmImportFile("test-realm-config.json");

    @DynamicPropertySource
    static void dynamicProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.security.oauth2.resourceserver.jwt.issuer-uri",
                () -> keycloakContainer.getAuthServerUrl() + "realms/PolarBookshop");
    }
}
