package com.example.demo.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.demo.model.Category;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql(scripts = "classpath:database/categories/add-categories.sql",
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "classpath:database/categories/remove-categories.sql",
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class CategoryRepositoryTest {
    private static final Long HISTORY_ID = 200L;
    private static final Long DELETED_ID = 202L;

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    @DisplayName("findById() returns an existing category")
    void findById_existingCategory_returnsCategory() {
        Optional<Category> actual = categoryRepository.findById(HISTORY_ID);

        assertThat(actual).isPresent();
        assertThat(actual.get().getName()).isEqualTo("History");
    }

    @Test
    @DisplayName("findById() returns empty for a soft deleted category")
    void findById_softDeletedCategory_returnsEmpty() {
        assertThat(categoryRepository.findById(DELETED_ID)).isEmpty();
    }

    @Test
    @DisplayName("findAll() returns only the categories that are not deleted")
    void findAll_withSoftDeletedCategory_returnsOnlyActiveOnes() {
        Page<Category> actual = categoryRepository.findAll(PageRequest.of(0, 10));

        assertThat(actual.getContent())
                .extracting(Category::getName)
                .containsExactlyInAnyOrder("History", "Poetry");
    }

    @Test
    @DisplayName("delete() performs a soft delete instead of removing the row")
    void delete_existingCategory_softDeletesIt() {
        Category category = categoryRepository.findById(HISTORY_ID).orElseThrow();

        categoryRepository.delete(category);
        categoryRepository.flush();

        assertThat(categoryRepository.findById(HISTORY_ID)).isEmpty();
    }
}
