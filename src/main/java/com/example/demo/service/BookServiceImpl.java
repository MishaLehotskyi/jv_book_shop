package com.example.demo.service;

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
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;
    private final BookMapper bookMapper;
    private final SpecificationBuilder<Book> bookSpecificationBuilder;

    @Override
    @Transactional(readOnly = true)
    public Page<BookDto> findAll(Pageable pageable) {
        return bookRepository.findAll(pageable)
                .map(bookMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public BookDto findById(Long id) {
        return bookMapper.toDto(findBookById(id));
    }

    @Override
    @Transactional
    public BookDto save(CreateBookRequestDto bookDto) {
        return bookMapper.toDto(bookRepository.save(bookMapper.toEntity(bookDto)));
    }

    @Override
    @Transactional
    public BookDto update(Long id, UpdateBookRequestDto bookDto) {
        Book book = findBookById(id);
        bookMapper.updateBookFromDto(bookDto, book);
        return bookMapper.toDto(bookRepository.save(book));
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        bookRepository.delete(findBookById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookDto> search(BookSearchParametersDto searchParameters) {
        return bookRepository.findAll(bookSpecificationBuilder.build(searchParameters))
                .stream()
                .map(bookMapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BookDtoWithoutCategoryIds> findAllByCategoryId(Long categoryId, Pageable pageable) {
        return bookRepository.findAllByCategoryId(categoryId, pageable)
                .map(bookMapper::toDtoWithoutCategories);
    }

    private Book findBookById(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Book not found with id: " + id));
    }
}
