package me.alex.polarbookshop.catalogservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = CatalogServiceApplication.class)
public class CatalogServiceApplicationTests implements PostgresIT {
    @Test
    void contextLoads() {
    }
}
