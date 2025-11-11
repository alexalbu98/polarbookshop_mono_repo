package me.alex.polarbookshop.order_service;

import me.alex.polarbookshop.order_service.domain.Order;
import me.alex.polarbookshop.order_service.domain.OrderStatus;
import me.alex.polarbookshop.order_service.order.event.OrderDispatchMessage;
import me.alex.polarbookshop.order_service.persistence.OrderRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.binder.test.InputDestination;
import org.springframework.cloud.stream.binder.test.TestChannelBinderConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;


@SpringBootTest
@Import({TestChannelBinderConfiguration.class})
public class StreamingIntegrationTests implements PostgresIT {

    @Autowired
    private InputDestination input;

    @Autowired
    private OrderRepository orderRepository;


    @Test
    void whenOrderDispatched_statusIsUpdated() {
        var order = Order.of("123", "test", 123.12, 1, OrderStatus.ACCEPTED);
        var savedOrder = orderRepository.save(order).block();

        Message<OrderDispatchMessage> inputMessage = MessageBuilder
                .withPayload(new OrderDispatchMessage(savedOrder.id())).build();

        this.input.send(inputMessage);
    }
}
