package com.example.demo.mapper;

import com.example.demo.dto.BookDto;
import com.example.demo.dto.BookDtoWithoutCategoryIds;
import com.example.demo.dto.CreateBookRequestDto;
import com.example.demo.dto.UpdateBookRequestDto;
import com.example.demo.model.Book;
import com.example.demo.model.Category;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.AfterMapping;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface BookMapper {
    @Mapping(target = "categoryIds", source = "categories")
    BookDto toDto(Book book);

    BookDtoWithoutCategoryIds toDtoWithoutCategories(Book book);

    @Mapping(target = "categories", ignore = true)
    Book toEntity(CreateBookRequestDto bookDto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "categories", ignore = true)
    void updateBookFromDto(UpdateBookRequestDto dto, @MappingTarget Book book);

    default Set<Long> toCategoryIds(Set<Category> categories) {
        if (categories == null) {
            return Set.of();
        }
        return categories.stream()
                .map(Category::getId)
                .collect(Collectors.toSet());
    }

    @AfterMapping
    default void setCategories(@MappingTarget Book book, CreateBookRequestDto bookDto) {
        replaceCategories(book, bookDto.categoryIds());
    }

    @AfterMapping
    default void setCategories(@MappingTarget Book book, UpdateBookRequestDto bookDto) {
        if (bookDto.categoryIds() != null) {
            replaceCategories(book, bookDto.categoryIds());
        }
    }

    private void replaceCategories(Book book, List<Long> categoryIds) {
        book.getCategories().clear();
        if (categoryIds == null) {
            return;
        }
        categoryIds.stream()
                .distinct()
                .map(this::toCategoryReference)
                .forEach(book.getCategories()::add);
    }

    private Category toCategoryReference(Long categoryId) {
        Category category = new Category();
        category.setId(categoryId);
        return category;
    }
}
