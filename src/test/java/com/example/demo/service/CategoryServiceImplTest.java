package com.example.demo.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.demo.dto.CategoryDto;
import com.example.demo.dto.CreateCategoryDto;
import com.example.demo.exception.EntityNotFoundException;
import com.example.demo.mapper.CategoryMapper;
import com.example.demo.model.Category;
import com.example.demo.repository.CategoryRepository;
import java.util.List;
import java.util.Optional;
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

@ExtendWith(MockitoExtension.class)
class CategoryServiceImplTest {
    private static final Long CATEGORY_ID = 1L;
    private static final Long NOT_EXISTING_ID = 100L;

    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private CategoryMapper categoryMapper;
    @InjectMocks
    private CategoryServiceImpl categoryService;

    @Test
    @DisplayName("findAll() returns a page of mapped categories")
    void findAll_validPageable_returnsPageOfDtos() {
        Category category = createCategory();
        CategoryDto expected = createCategoryDto();
        Pageable pageable = PageRequest.of(0, 10);
        when(categoryRepository.findAll(pageable))
                .thenReturn(new PageImpl<>(List.of(category), pageable, 1));
        when(categoryMapper.toDto(category)).thenReturn(expected);

        Page<CategoryDto> actual = categoryService.findAll(pageable);

        assertThat(actual.getContent()).containsExactly(expected);
        verify(categoryRepository, times(1)).findAll(pageable);
    }

    @Test
    @DisplayName("getById() returns the category with the given id")
    void getById_existingId_returnsCategoryDto() {
        Category category = createCategory();
        CategoryDto expected = createCategoryDto();
        when(categoryRepository.findById(CATEGORY_ID)).thenReturn(Optional.of(category));
        when(categoryMapper.toDto(category)).thenReturn(expected);

        CategoryDto actual = categoryService.getById(CATEGORY_ID);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("getById() throws an exception when the category does not exist")
    void getById_notExistingId_throwsEntityNotFoundException() {
        when(categoryRepository.findById(NOT_EXISTING_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> categoryService.getById(NOT_EXISTING_ID))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Category not found with id: " + NOT_EXISTING_ID);
        verify(categoryMapper, never()).toDto(any());
    }

    @Test
    @DisplayName("save() persists the category and returns its dto")
    void save_validRequest_returnsSavedCategoryDto() {
        CreateCategoryDto requestDto = new CreateCategoryDto("Fiction", "Fiction books");
        Category category = createCategory();
        CategoryDto expected = createCategoryDto();
        when(categoryMapper.toEntity(requestDto)).thenReturn(category);
        when(categoryRepository.save(category)).thenReturn(category);
        when(categoryMapper.toDto(category)).thenReturn(expected);

        CategoryDto actual = categoryService.save(requestDto);

        assertThat(actual).isEqualTo(expected);
        verify(categoryRepository, times(1)).save(category);
    }

    @Test
    @DisplayName("update() applies the changes to an existing category")
    void update_existingId_returnsUpdatedCategoryDto() {
        CreateCategoryDto requestDto = new CreateCategoryDto("Sci-fi", "Sci-fi books");
        Category category = createCategory();
        CategoryDto expected = createCategoryDto();
        when(categoryRepository.findById(CATEGORY_ID)).thenReturn(Optional.of(category));
        when(categoryRepository.save(category)).thenReturn(category);
        when(categoryMapper.toDto(category)).thenReturn(expected);

        CategoryDto actual = categoryService.update(CATEGORY_ID, requestDto);

        assertThat(actual).isEqualTo(expected);
        verify(categoryMapper, times(1)).updateCategoryFromDto(requestDto, category);
    }

    @Test
    @DisplayName("update() throws an exception when the category does not exist")
    void update_notExistingId_throwsEntityNotFoundException() {
        CreateCategoryDto requestDto = new CreateCategoryDto("Sci-fi", "Sci-fi books");
        when(categoryRepository.findById(NOT_EXISTING_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> categoryService.update(NOT_EXISTING_ID, requestDto))
                .isInstanceOf(EntityNotFoundException.class);
        verify(categoryRepository, never()).save(any());
    }

    @Test
    @DisplayName("deleteById() soft deletes an existing category")
    void deleteById_existingId_callsRepositoryDelete() {
        Category category = createCategory();
        when(categoryRepository.findById(CATEGORY_ID)).thenReturn(Optional.of(category));

        categoryService.deleteById(CATEGORY_ID);

        verify(categoryRepository, times(1)).delete(category);
    }

    @Test
    @DisplayName("deleteById() throws an exception when the category does not exist")
    void deleteById_notExistingId_throwsEntityNotFoundException() {
        when(categoryRepository.findById(NOT_EXISTING_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> categoryService.deleteById(NOT_EXISTING_ID))
                .isInstanceOf(EntityNotFoundException.class);
        verify(categoryRepository, never()).delete(any());
    }

    private Category createCategory() {
        Category category = new Category();
        category.setId(CATEGORY_ID);
        category.setName("Fiction");
        category.setDescription("Fiction books");
        return category;
    }

    private CategoryDto createCategoryDto() {
        return new CategoryDto(CATEGORY_ID, "Fiction", "Fiction books");
    }
}
