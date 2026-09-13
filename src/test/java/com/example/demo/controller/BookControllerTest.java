package com.example.demo.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.demo.dto.BookDto;
import com.example.demo.dto.CreateBookRequestDto;
import com.example.demo.dto.UpdateBookRequestDto;
import java.math.BigDecimal;
import java.util.List;
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
class BookControllerTest {
    private static final Long BOOK_ID = 100L;
    private static final Long CATEGORY_ID = 100L;
    private static final Long NOT_EXISTING_ID = 999L;

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(username = "user", roles = "USER")
    @DisplayName("GET /books returns a page with all non deleted books")
    void getAll_asUser_returnsPageOfBooks() throws Exception {
        mockMvc.perform(get("/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.content[*].title")
                        .value(containsInAnyOrder("Dune", "Cosmos")));
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    @DisplayName("GET /books/{id} returns the requested book")
    void getBookById_existingId_returnsBook() throws Exception {
        MvcResult result = mockMvc.perform(get("/books/" + BOOK_ID))
                .andExpect(status().isOk())
                .andReturn();

        BookDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), BookDto.class);
        assertThat(actual.id()).isEqualTo(BOOK_ID);
        assertThat(actual.title()).isEqualTo("Dune");
        assertThat(actual.categoryIds()).containsExactly(CATEGORY_ID);
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    @DisplayName("GET /books/{id} returns 404 for a not existing book")
    void getBookById_notExistingId_returnsNotFound() throws Exception {
        mockMvc.perform(get("/books/" + NOT_EXISTING_ID))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    @DisplayName("GET /books/search returns the books matching the parameters")
    void searchBooks_byTitle_returnsMatchingBooks() throws Exception {
        mockMvc.perform(get("/books/search").param("title", "dun"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].title").value("Dune"));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    @DisplayName("POST /books creates a new book")
    void createBook_validRequest_returnsCreatedBook() throws Exception {
        CreateBookRequestDto requestDto = new CreateBookRequestDto("Neuromancer",
                "William Gibson", "9780441569595", BigDecimal.valueOf(15.50),
                "Cyberpunk classic", "neuromancer.jpg", List.of(CATEGORY_ID));

        MvcResult result = mockMvc.perform(post("/books")
                        .content(objectMapper.writeValueAsString(requestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();

        BookDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), BookDto.class);
        assertThat(actual.id()).isNotNull();
        assertThat(actual.title()).isEqualTo(requestDto.title());
        assertThat(actual.categoryIds()).containsExactly(CATEGORY_ID);
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    @DisplayName("POST /books returns 400 when the request body is invalid")
    void createBook_invalidRequest_returnsBadRequest() throws Exception {
        CreateBookRequestDto requestDto = new CreateBookRequestDto("", "", "not-an-isbn",
                BigDecimal.valueOf(-1), null, null, List.of());

        mockMvc.perform(post("/books")
                        .content(objectMapper.writeValueAsString(requestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    @DisplayName("POST /books is forbidden for a user without the ADMIN role")
    void createBook_asUser_returnsForbidden() throws Exception {
        CreateBookRequestDto requestDto = new CreateBookRequestDto("Neuromancer",
                "William Gibson", "9780441569595", BigDecimal.valueOf(15.50),
                "Cyberpunk classic", "neuromancer.jpg", List.of(CATEGORY_ID));

        mockMvc.perform(post("/books")
                        .content(objectMapper.writeValueAsString(requestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    @DisplayName("PUT /books/{id} updates an existing book")
    void updateBook_validRequest_returnsUpdatedBook() throws Exception {
        UpdateBookRequestDto requestDto = new UpdateBookRequestDto("Dune (revised)",
                "Frank Herbert", "9780441013593", BigDecimal.valueOf(29.99),
                "Revised edition", "dune-revised.jpg", List.of(CATEGORY_ID));

        MvcResult result = mockMvc.perform(put("/books/" + BOOK_ID)
                        .content(objectMapper.writeValueAsString(requestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        BookDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), BookDto.class);
        assertThat(actual.id()).isEqualTo(BOOK_ID);
        assertThat(actual.title()).isEqualTo("Dune (revised)");
        assertThat(actual.price()).isEqualByComparingTo(BigDecimal.valueOf(29.99));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN", "USER"})
    @DisplayName("DELETE /books/{id} soft deletes the book and returns 204")
    void deleteBook_existingId_returnsNoContent() throws Exception {
        mockMvc.perform(delete("/books/" + BOOK_ID))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/books/" + BOOK_ID))
                .andExpect(status().isNotFound());
    }
}
