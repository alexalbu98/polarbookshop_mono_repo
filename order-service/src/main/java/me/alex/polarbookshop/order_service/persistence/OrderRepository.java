package me.alex.polarbookshop.order_service.persistence;

import me.alex.polarbookshop.order_service.domain.Order;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface OrderRepository extends ReactiveCrudRepository<Order, Long> {
    Flux<Order> findAllByCreatedBy(String userId);
}
