package com.example.demo.mapper;

import com.example.demo.dto.BookDto;
import com.example.demo.dto.CreateBookRequestDto;
import com.example.demo.dto.UpdateBookRequestDto;
import com.example.demo.model.Book;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface BookMapper {
    BookDto toDto(Book book);

    Book toModel(CreateBookRequestDto bookDto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateBookFromDto(UpdateBookRequestDto dto, @MappingTarget Book book);
}
