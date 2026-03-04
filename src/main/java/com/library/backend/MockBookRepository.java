package com.library.backend;

import com.library.security.Roles;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class MockBookRepository {
    private List<Book> books = new ArrayList<>();

    public MockBookRepository() {
        save(new Book("The Fellowship of the Ring", "J.R.R. Tolkein", "1234567890"));
        save(new Book("Harry Potter and the Order of the Pheonix", "J.K. Rowling", "0987654321"));
        save(new Book("Pride and Prejudice", "Jane Austen", "1357924680"));
    }

    public Optional<Book> findById(Long id) {
        return books.stream().filter(book -> book.getId().equals(id)).findFirst();
    }

    // return a copy
    public List<Book> findAll() {
        return new ArrayList<>(books);
    }

    // save or update
    public void save(Book book) {
        int index = books.indexOf(book);
        if(index == -1) books.add(book);
        else books.set(index, book);
    }

    public void delete(Book book) {
        books.remove(book);
    }
}
