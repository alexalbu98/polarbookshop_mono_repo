package me.alex.polarbookshop.order_service;

import me.alex.polarbookshop.order_service.domain.Order;
import me.alex.polarbookshop.order_service.domain.OrderService;
import me.alex.polarbookshop.order_service.domain.OrderStatus;
import me.alex.polarbookshop.order_service.web.OrderController;
import me.alex.polarbookshop.order_service.web.OrderRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.BDDMockito.given;

@WebFluxTest(OrderController.class)
public class OrderControllerWebFluxTests {
    @Autowired
    private WebTestClient webClient;

    @MockitoBean
    private OrderService orderService;

    @Test
    void whenBookNotAvailableThenRejectOrder() {
        var orderRequest = new OrderRequest("1234567890", 3);
        var expectedOrder = OrderService.buildRejectedOrder(
                orderRequest.isbn(), orderRequest.quantity());

        given(orderService.submitOrder(
                orderRequest.isbn(), orderRequest.quantity())
        ).willReturn(Mono.just(expectedOrder));

        webClient
                .post()
                .uri("/orders")
                .bodyValue(orderRequest)
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody(Order.class).value(actualOrder -> {
                    assertThat(actualOrder).isNotNull();
                    assertThat(actualOrder.status()).isEqualTo(OrderStatus.REJECTED);
                });
    }
}
