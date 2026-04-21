package com.library.backend.entities;

import com.vaadin.copilot.shaded.checkerframework.common.aliasing.qual.Unique;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
public class Branch {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    @OneToMany( mappedBy = "branch", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Book> books = new HashSet<>();


    public Branch() {};

    public Branch(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Book> getBooks() {
        return books;
    }

    public void addBook(Book book) {
        this.books.add(book);
        book.setBranch(this);
    }

    public void removeBook(Book book) {
        this.books.remove(book);
        book.setBranch(null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Branch branch = (Branch) o;
        return id != null && id.equals(branch.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
