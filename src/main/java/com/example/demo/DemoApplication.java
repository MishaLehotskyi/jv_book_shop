package com.example.demo;

import com.example.demo.model.Book;
import com.example.demo.service.BookService;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner(BookService bookService) {
        return args -> {
            Book book = new Book();
            book.setTitle("The Clean Coder");
            book.setAuthor("Robert C. Martin");
            book.setIsbn("978-0137081073");
            book.setPrice(BigDecimal.valueOf(29.99));
            book.setDescription("A Code of Conduct for Professional Programmers");
            bookService.save(book);

            List<Book> books = bookService.findAll();
            System.out.println("Books in the database: " + books.size());
        };
    }
}
