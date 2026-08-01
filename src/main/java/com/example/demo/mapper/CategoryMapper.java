package com.example.demo.mapper;

import com.example.demo.dto.CategoryDto;
import com.example.demo.dto.CreateCategoryDto;
import com.example.demo.model.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    CategoryDto toDto(Category category);

    @Mapping(target = "id", ignore = true)
    Category toEntity(CreateCategoryDto categoryDto);

    @Mapping(target = "id", ignore = true)
    void updateCategoryFromDto(CreateCategoryDto categoryDto, @MappingTarget Category category);
}
