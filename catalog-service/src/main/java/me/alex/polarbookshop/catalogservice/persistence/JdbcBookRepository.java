package me.alex.polarbookshop.catalogservice.persistence;

import me.alex.polarbookshop.catalogservice.domain.Book;
import me.alex.polarbookshop.catalogservice.domain.BookRepository;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@Transactional(readOnly = true)
public interface JdbcBookRepository extends CrudRepository<Book, Long>, BookRepository {
    Optional<Book> findByIsbn(String isbn);

    boolean existsByIsbn(String isbn);

    @Modifying
    @Transactional
    @Query("delete from Book where isbn = :isbn")
    void deleteByIsbn(String isbn);
}
