package com.example.demo.repository.spec.book;

import com.example.demo.dto.BookSearchParametersDto;
import com.example.demo.model.Book;
import com.example.demo.repository.spec.SpecificationBuilder;
import com.example.demo.repository.spec.SpecificationProviderManager;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BookSpecificationBuilder implements SpecificationBuilder<Book> {
    private final SpecificationProviderManager<Book> specificationProviderManager;

    @Override
    public Specification<Book> build(BookSearchParametersDto params) {
        Specification<Book> spec = Specification.where((Specification<Book>) null);
        if (params.title() != null && !params.title().isEmpty()) {
            spec = spec.and(specificationProviderManager
                    .getSpecificationProvider("title")
                    .getSpecification(new String[]{params.title()}));
        }
        if (params.author() != null && !params.author().isEmpty()) {
            spec = spec.and(specificationProviderManager
                    .getSpecificationProvider("author")
                    .getSpecification(new String[]{params.author()}));
        }
        if (params.isbn() != null && !params.isbn().isEmpty()) {
            spec = spec.and(specificationProviderManager
                    .getSpecificationProvider("isbn")
                    .getSpecification(new String[]{params.isbn()}));
        }
        return spec;
    }
}
