package me.alex.polarbookshop.order_service.config;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

import java.net.URI;


@Getter
@ConfigurationProperties(prefix = "polar")
public class ClientProperties {
    @NotNull
    private final URI catalogServiceUri;

    @ConstructorBinding
    public ClientProperties(URI catalogServiceUri) {
        this.catalogServiceUri = catalogServiceUri;
    }
}
