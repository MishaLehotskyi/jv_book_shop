package com.example.demo.repository;

import com.example.demo.model.Book;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;

public interface BookRepository extends JpaRepository<Book, Long> {
    @Transactional
    @Modifying
    @Query("UPDATE Book b SET b.isDeleted = true WHERE b.id = :id")
    void deleteById(@NonNull Long id);
}
