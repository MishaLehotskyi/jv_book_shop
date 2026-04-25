package com.example.demo.service;

import com.example.demo.dto.BookDto;
import com.example.demo.dto.BookSearchParametersDto;
import com.example.demo.dto.CreateBookRequestDto;
import com.example.demo.dto.UpdateBookRequestDto;
import java.util.List;

public interface BookService {
    List<BookDto> findAll();

    BookDto findById(Long id);

    BookDto save(CreateBookRequestDto bookDto);

    BookDto update(Long id, UpdateBookRequestDto bookDto);

    void deleteById(Long id);

    List<BookDto> search(BookSearchParametersDto searchParameters);
}
