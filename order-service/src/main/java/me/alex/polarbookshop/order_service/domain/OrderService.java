package me.alex.polarbookshop.order_service.domain;

import lombok.extern.slf4j.Slf4j;
import me.alex.polarbookshop.order_service.book.Book;
import me.alex.polarbookshop.order_service.book.BookClient;
import me.alex.polarbookshop.order_service.order.event.OrderAcceptedMessage;
import me.alex.polarbookshop.order_service.order.event.OrderDispatchMessage;
import me.alex.polarbookshop.order_service.persistence.OrderRepository;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@Slf4j
@SuppressWarnings("PMD.GuardLogStatement")
public class OrderService {
    private final OrderRepository orderRepository;
    private final BookClient bookClient;
    private final StreamBridge streamBridge;

    public OrderService(OrderRepository orderRepository, BookClient bookClient, StreamBridge streamBridge) {
        this.orderRepository = orderRepository;
        this.bookClient = bookClient;
        this.streamBridge = streamBridge;
    }

    public Flux<Order> getAllOrders(String userId) {
        return orderRepository.findAllByCreatedBy(userId);
    }

    @Transactional
    public Mono<Order> submitOrder(String isbn, int quantity) {
        return bookClient
                .getBookByIsbn(isbn)
                .map(book -> buildAcceptedOrder(book, quantity))
                .defaultIfEmpty(buildRejectedOrder(isbn, quantity))
                .flatMap(orderRepository::save)
                .doOnNext(this::publishOrderAcceptedEvent);
    }

    public Flux<Order> consumeOrderDispatchedEvent(
            Flux<OrderDispatchMessage> flux
    ) {
        return flux
                .flatMap(message ->
                        orderRepository.findById(message.orderId()))
                .map(this::buildDispatchedOrder)
                .flatMap(orderRepository::save);
    }

    private void publishOrderAcceptedEvent(Order order) {
        if (!order.status().equals(OrderStatus.ACCEPTED)) {
            return;
        }
        var orderAcceptedMessage = new OrderAcceptedMessage(order.id());
        log.info("Sending order accepted event with id: {}", order.id());
        var result = streamBridge.send("acceptedOrder-out-0",
                orderAcceptedMessage);
        log.info("Result of sending data for order with id {}: {}",
                order.id(), result);
    }

    private Order buildDispatchedOrder(Order existingOrder) {
        return new Order(
                existingOrder.id(),
                existingOrder.bookIsbn(),
                existingOrder.bookName(),
                existingOrder.bookPrice(),
                existingOrder.quantity(),
                OrderStatus.DISPATCHED,
                existingOrder.createdDate(),
                existingOrder.lastModifiedDate(),
                existingOrder.createdBy(),
                existingOrder.lastModifiedBy(),
                existingOrder.version()
        );
    }

    public static Order buildAcceptedOrder(Book book, int quantity) {
        return Order.of(book.isbn(), book.title() + " - " + book.author(),
                book.price(), quantity, OrderStatus.ACCEPTED);
    }

    public static Order buildRejectedOrder(
            String bookIsbn, int quantity
    ) {
        return Order.of(bookIsbn, null, null, quantity, OrderStatus.REJECTED);
    }
}