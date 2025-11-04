package me.alex.polarbookshop.dispatch_service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Slf4j
@Component
public class DispatchingFunctions {

    @Bean
    public Function<OrderAcceptedMessage, Long> pack() {
        return orderAcceptedMessage -> {
            log.info("The order with id: {} is packet", orderAcceptedMessage.orderId());
            return orderAcceptedMessage.orderId();
        };
    }

    @Bean
    public Function<Long, OrderDispatchedMessage> label() {
        return orderId -> {
            log.info("The order with id: {} is labeled", orderId);
            return new OrderDispatchedMessage(orderId);
        };
    }
}
