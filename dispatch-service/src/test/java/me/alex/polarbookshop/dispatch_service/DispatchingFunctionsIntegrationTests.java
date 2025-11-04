package me.alex.polarbookshop.dispatch_service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.function.context.FunctionCatalog;
import org.springframework.cloud.function.context.test.FunctionalSpringBootTest;

import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;

@FunctionalSpringBootTest
public class DispatchingFunctionsIntegrationTests {

    @Autowired
    private FunctionCatalog functionCatalog;

    @Test
    void packAndLabelOrder() {
        Function<OrderAcceptedMessage, OrderDispatchedMessage> packAndLabel = functionCatalog.lookup(
                Function.class,
                "pack|label"
        );

        long orderId = 121;
        assertEquals(new OrderDispatchedMessage(orderId),
                packAndLabel.apply(new OrderAcceptedMessage(orderId)));
    }
}
