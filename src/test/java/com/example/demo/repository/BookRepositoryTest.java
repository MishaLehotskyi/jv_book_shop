package com.example.demo.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.demo.model.Book;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql(scripts = "classpath:database/books/add-books-and-categories.sql",
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "classpath:database/books/remove-books-and-categories.sql",
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class BookRepositoryTest {
    private static final Long FICTION_CATEGORY_ID = 100L;
    private static final Long EMPTY_CATEGORY_ID = 999L;

    @Autowired
    private BookRepository bookRepository;

    @Test
    @DisplayName("findAllByCategoryId() returns only the books of the given category")
    void findAllByCategoryId_existingCategory_returnsItsBooks() {
        Pageable pageable = PageRequest.of(0, 10);

        Page<Book> actual = bookRepository.findAllByCategoryId(FICTION_CATEGORY_ID, pageable);

        assertThat(actual.getTotalElements()).isEqualTo(1);
        assertThat(actual.getContent().get(0).getTitle()).isEqualTo("Dune");
    }

    @Test
    @DisplayName("findAllByCategoryId() returns an empty page for a category without books")
    void findAllByCategoryId_categoryWithoutBooks_returnsEmptyPage() {
        Pageable pageable = PageRequest.of(0, 10);

        Page<Book> actual = bookRepository.findAllByCategoryId(EMPTY_CATEGORY_ID, pageable);

        assertThat(actual.getContent()).isEmpty();
    }

    @Test
    @DisplayName("findAll() skips soft deleted books")
    void findAll_withSoftDeletedBook_doesNotReturnIt() {
        Page<Book> actual = bookRepository.findAll(PageRequest.of(0, 10));

        assertThat(actual.getContent())
                .extracting(Book::getTitle)
                .containsExactlyInAnyOrder("Dune", "Cosmos")
                .doesNotContain("Deleted Book");
    }

    @Test
    @DisplayName("delete() performs a soft delete instead of removing the row")
    void delete_existingBook_softDeletesIt() {
        Book book = bookRepository.findById(100L).orElseThrow();

        bookRepository.delete(book);
        bookRepository.flush();

        assertThat(bookRepository.findById(100L)).isEmpty();
    }
}
