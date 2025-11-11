package me.alex.polarbookshop.order_service.order.event;

import lombok.extern.slf4j.Slf4j;
import me.alex.polarbookshop.order_service.domain.OrderService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Flux;

import java.util.function.Consumer;

@Configuration
@Slf4j
public class OrderFunctions {

    @Bean
    public Consumer<Flux<OrderDispatchMessage>> dispatchOrder(OrderService orderService) {
        return flux ->
                orderService.consumeOrderDispatchedEvent(flux)
                        .doOnNext(order -> log.info("The order with id {} is dispatched", order.id()))
                        .subscribe();
    }
}
