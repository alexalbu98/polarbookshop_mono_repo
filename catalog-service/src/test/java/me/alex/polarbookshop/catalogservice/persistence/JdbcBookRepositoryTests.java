package me.alex.polarbookshop.catalogservice.persistence;

import me.alex.polarbookshop.catalogservice.PostgresIT;
import me.alex.polarbookshop.catalogservice.config.DataConfig;
import me.alex.polarbookshop.catalogservice.domain.Book;
import me.alex.polarbookshop.catalogservice.domain.BookRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJdbcTest
@Import({DataConfig.class})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class JdbcBookRepositoryTests implements PostgresIT {

    @Autowired
    private BookRepository bookRepository;

    @Test
    void whenCreateBookNotAuthenticatedThenNoAuditMetadata() {
        var bookToCreate = Book.of("1232343456", "Title", "Author", 12.90, "Polarsophia");
        var createdBook = bookRepository.save(bookToCreate);
        assertThat(createdBook.createdBy()).isNull();
        assertThat(createdBook.lastModifiedBy()).isNull();
    }

    @Test
    @WithMockUser("john")
    void whenCreateBookAuthenticatedThenAuditMetadata() {
        //given
        var bookIsbn = "1234561237";
        var book = Book.of(bookIsbn, "Title", "Author", 12.90, "test");

        //when
        var created = bookRepository.save(book);

        //then
        assertThat(created.isbn()).isEqualTo(book.isbn());
        assertThat(created.createdBy())
                .isEqualTo(created.lastModifiedBy())
                .isEqualTo("john");
    }
}
