package com.example.demo.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.demo.dto.CategoryDto;
import com.example.demo.dto.CreateCategoryDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import tools.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@Sql(scripts = "classpath:database/books/add-books-and-categories.sql",
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "classpath:database/books/remove-books-and-categories.sql",
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class CategoryControllerTest {
    private static final Long CATEGORY_ID = 100L;
    private static final Long NOT_EXISTING_ID = 999L;

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(username = "user", roles = "USER")
    @DisplayName("GET /categories returns a page with all categories")
    void getAll_asUser_returnsPageOfCategories() throws Exception {
        mockMvc.perform(get("/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(2));
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    @DisplayName("GET /categories/{id} returns the requested category")
    void getCategoryById_existingId_returnsCategory() throws Exception {
        MvcResult result = mockMvc.perform(get("/categories/" + CATEGORY_ID))
                .andExpect(status().isOk())
                .andReturn();

        CategoryDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), CategoryDto.class);
        assertThat(actual.id()).isEqualTo(CATEGORY_ID);
        assertThat(actual.name()).isEqualTo("Fiction");
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    @DisplayName("GET /categories/{id} returns 404 for a not existing category")
    void getCategoryById_notExistingId_returnsNotFound() throws Exception {
        mockMvc.perform(get("/categories/" + NOT_EXISTING_ID))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    @DisplayName("GET /categories/{id}/books returns the books of the category")
    void getBooksByCategoryId_existingId_returnsBooks() throws Exception {
        mockMvc.perform(get("/categories/" + CATEGORY_ID + "/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.content[0].title").value("Dune"));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    @DisplayName("POST /categories creates a new category")
    void createCategory_validRequest_returnsCreatedCategory() throws Exception {
        CreateCategoryDto requestDto = new CreateCategoryDto("Fantasy", "Fantasy books");

        MvcResult result = mockMvc.perform(post("/categories")
                        .content(objectMapper.writeValueAsString(requestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();

        CategoryDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), CategoryDto.class);
        assertThat(actual.id()).isNotNull();
        assertThat(actual.name()).isEqualTo(requestDto.name());
        assertThat(actual.description()).isEqualTo(requestDto.description());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    @DisplayName("POST /categories returns 400 when the name is blank")
    void createCategory_invalidRequest_returnsBadRequest() throws Exception {
        CreateCategoryDto requestDto = new CreateCategoryDto("", "No name");

        mockMvc.perform(post("/categories")
                        .content(objectMapper.writeValueAsString(requestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    @DisplayName("POST /categories is forbidden for a user without the ADMIN role")
    void createCategory_asUser_returnsForbidden() throws Exception {
        CreateCategoryDto requestDto = new CreateCategoryDto("Fantasy", "Fantasy books");

        mockMvc.perform(post("/categories")
                        .content(objectMapper.writeValueAsString(requestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    @DisplayName("PUT /categories/{id} updates an existing category")
    void updateCategory_validRequest_returnsUpdatedCategory() throws Exception {
        CreateCategoryDto requestDto = new CreateCategoryDto("Sci-fi", "Sci-fi books");

        MvcResult result = mockMvc.perform(put("/categories/" + CATEGORY_ID)
                        .content(objectMapper.writeValueAsString(requestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        CategoryDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), CategoryDto.class);
        assertThat(actual.id()).isEqualTo(CATEGORY_ID);
        assertThat(actual.name()).isEqualTo("Sci-fi");
        assertThat(actual.description()).isEqualTo("Sci-fi books");
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN", "USER"})
    @DisplayName("DELETE /categories/{id} soft deletes the category and returns 204")
    void deleteCategory_existingId_returnsNoContent() throws Exception {
        mockMvc.perform(delete("/categories/" + CATEGORY_ID))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/categories/" + CATEGORY_ID))
                .andExpect(status().isNotFound());
    }
}
