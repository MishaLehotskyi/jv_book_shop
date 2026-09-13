package com.example.demo.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.demo.dto.BookDto;
import com.example.demo.dto.BookDtoWithoutCategoryIds;
import com.example.demo.dto.BookSearchParametersDto;
import com.example.demo.dto.CreateBookRequestDto;
import com.example.demo.dto.UpdateBookRequestDto;
import com.example.demo.exception.EntityNotFoundException;
import com.example.demo.mapper.BookMapper;
import com.example.demo.model.Book;
import com.example.demo.repository.BookRepository;
import com.example.demo.repository.spec.SpecificationBuilder;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

@ExtendWith(MockitoExtension.class)
class BookServiceImplTest {
    private static final Long BOOK_ID = 1L;
    private static final Long NOT_EXISTING_ID = 100L;
    private static final Long CATEGORY_ID = 5L;

    @Mock
    private BookRepository bookRepository;
    @Mock
    private BookMapper bookMapper;
    @Mock
    private SpecificationBuilder<Book> bookSpecificationBuilder;
    @InjectMocks
    private BookServiceImpl bookService;

    @Test
    @DisplayName("findAll() returns a page of mapped books")
    void findAll_validPageable_returnsPageOfDtos() {
        Book book = createBook();
        BookDto expected = createBookDto();
        Pageable pageable = PageRequest.of(0, 10);
        when(bookRepository.findAll(pageable))
                .thenReturn(new PageImpl<>(List.of(book), pageable, 1));
        when(bookMapper.toDto(book)).thenReturn(expected);

        Page<BookDto> actual = bookService.findAll(pageable);

        assertThat(actual.getContent()).containsExactly(expected);
        verify(bookRepository, times(1)).findAll(pageable);
    }

    @Test
    @DisplayName("findById() returns the book with the given id")
    void findById_existingId_returnsBookDto() {
        Book book = createBook();
        BookDto expected = createBookDto();
        when(bookRepository.findById(BOOK_ID)).thenReturn(Optional.of(book));
        when(bookMapper.toDto(book)).thenReturn(expected);

        BookDto actual = bookService.findById(BOOK_ID);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("findById() throws an exception when the book does not exist")
    void findById_notExistingId_throwsEntityNotFoundException() {
        when(bookRepository.findById(NOT_EXISTING_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookService.findById(NOT_EXISTING_ID))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Book not found with id: " + NOT_EXISTING_ID);
        verify(bookMapper, never()).toDto(any());
    }

    @Test
    @DisplayName("save() persists the book and returns its dto")
    void save_validRequest_returnsSavedBookDto() {
        CreateBookRequestDto requestDto = new CreateBookRequestDto("Dune", "Frank Herbert",
                "9780441013593", BigDecimal.valueOf(19.99), "Sci-fi classic", "dune.jpg",
                List.of(CATEGORY_ID));
        Book book = createBook();
        BookDto expected = createBookDto();
        when(bookMapper.toEntity(requestDto)).thenReturn(book);
        when(bookRepository.save(book)).thenReturn(book);
        when(bookMapper.toDto(book)).thenReturn(expected);

        BookDto actual = bookService.save(requestDto);

        assertThat(actual).isEqualTo(expected);
        verify(bookRepository, times(1)).save(book);
    }

    @Test
    @DisplayName("update() applies the changes to an existing book")
    void update_existingId_returnsUpdatedBookDto() {
        UpdateBookRequestDto requestDto = new UpdateBookRequestDto("Dune Messiah",
                "Frank Herbert", "9780441013593", BigDecimal.valueOf(21.99), "Sequel",
                "dune2.jpg", List.of(CATEGORY_ID));
        Book book = createBook();
        BookDto expected = createBookDto();
        when(bookRepository.findById(BOOK_ID)).thenReturn(Optional.of(book));
        when(bookRepository.save(book)).thenReturn(book);
        when(bookMapper.toDto(book)).thenReturn(expected);

        BookDto actual = bookService.update(BOOK_ID, requestDto);

        assertThat(actual).isEqualTo(expected);
        verify(bookMapper, times(1)).updateBookFromDto(requestDto, book);
    }

    @Test
    @DisplayName("deleteById() soft deletes an existing book")
    void deleteById_existingId_callsRepositoryDelete() {
        Book book = createBook();
        when(bookRepository.findById(BOOK_ID)).thenReturn(Optional.of(book));

        bookService.deleteById(BOOK_ID);

        verify(bookRepository, times(1)).delete(book);
    }

    @Test
    @DisplayName("deleteById() throws an exception when the book does not exist")
    void deleteById_notExistingId_throwsEntityNotFoundException() {
        when(bookRepository.findById(NOT_EXISTING_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookService.deleteById(NOT_EXISTING_ID))
                .isInstanceOf(EntityNotFoundException.class);
        verify(bookRepository, never()).delete(any(Book.class));
    }

    @Test
    @DisplayName("search() returns the books matching the built specification")
    void search_validParameters_returnsMatchingBooks() {
        BookSearchParametersDto params = new BookSearchParametersDto("Dune", null, null);
        Specification<Book> specification = (root, query, cb) -> null;
        Book book = createBook();
        BookDto expected = createBookDto();
        when(bookSpecificationBuilder.build(params)).thenReturn(specification);
        when(bookRepository.findAll(specification)).thenReturn(List.of(book));
        when(bookMapper.toDto(book)).thenReturn(expected);

        List<BookDto> actual = bookService.search(params);

        assertThat(actual).containsExactly(expected);
    }

    @Test
    @DisplayName("findAllByCategoryId() returns books of the category without category ids")
    void findAllByCategoryId_existingCategory_returnsBooks() {
        Book book = createBook();
        BookDtoWithoutCategoryIds expected = new BookDtoWithoutCategoryIds(BOOK_ID, "Dune",
                "Frank Herbert", "9780441013593", BigDecimal.valueOf(19.99), "Sci-fi classic",
                "dune.jpg");
        Pageable pageable = PageRequest.of(0, 10);
        when(bookRepository.findAllByCategoryId(CATEGORY_ID, pageable))
                .thenReturn(new PageImpl<>(List.of(book), pageable, 1));
        when(bookMapper.toDtoWithoutCategories(book)).thenReturn(expected);

        Page<BookDtoWithoutCategoryIds> actual =
                bookService.findAllByCategoryId(CATEGORY_ID, pageable);

        assertThat(actual.getContent()).containsExactly(expected);
    }

    private Book createBook() {
        Book book = new Book();
        book.setId(BOOK_ID);
        book.setTitle("Dune");
        book.setAuthor("Frank Herbert");
        book.setIsbn("9780441013593");
        book.setPrice(BigDecimal.valueOf(19.99));
        book.setDescription("Sci-fi classic");
        book.setCoverImage("dune.jpg");
        return book;
    }

    private BookDto createBookDto() {
        return new BookDto(BOOK_ID, "Dune", "Frank Herbert", "9780441013593",
                BigDecimal.valueOf(19.99), "Sci-fi classic", "dune.jpg", Set.of(CATEGORY_ID));
    }
}
