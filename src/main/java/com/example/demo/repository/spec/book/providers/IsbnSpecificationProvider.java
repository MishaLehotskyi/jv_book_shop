package com.example.demo.repository.spec.book.providers;

import com.example.demo.model.Book;
import com.example.demo.repository.spec.SpecificationProvider;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class IsbnSpecificationProvider implements SpecificationProvider<Book> {
    @Override
    public String getKey() {
        return "isbn";
    }

    @Override
    public Specification<Book> getSpecification(String[] params) {
        return (root, query, cb) -> root.get("isbn").in((Object[]) params);
    }
}
