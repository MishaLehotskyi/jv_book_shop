package com.example.demo.repository.spec.book.providers;

import com.example.demo.model.Book;
import com.example.demo.repository.spec.SpecificationProvider;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class TitleSpecificationProvider implements SpecificationProvider<Book> {
    @Override
    public String getKey() {
        return "title";
    }

    @Override
    public Specification<Book> getSpecification(String[] params) {
        return (root, query, cb) -> cb.like(
                cb.lower(root.get("title")), "%" + params[0].toLowerCase() + "%");
    }
}
