package me.alex.polarbookshop.catalogservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = CatalogServiceApplication.class)
@ActiveProfiles("integration")
public class CatalogServiceApplicationTests {
    @Test
    void contextLoads() {
    }
}
