package com.library.backend.entities;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String openLibraryKey;

    private String title;
    private String author;
    private String isbn;

    // Book owns the bridge table, meaning saving a book with a change in its genres will update the bridge table
    // Referencing the set of books for a given genre is like a readonly action -
    // if you update that set and save the genre, nothing happens in the bridge table
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "book_genre", // name of bridge table
        joinColumns = @JoinColumn(name = "book_id"), // column pointing to THIS entity
        inverseJoinColumns = @JoinColumn(name = "genre_id") // column poiting to OTHER entity in the relationship
    )
    private Set<Genre> genres = new HashSet<>();

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "branch_id")
    private Branch branch;

    public Book() {}

    public Book(String title, String author, String isbn) {
        this.title = title;
        this.author = author;
        this.isbn = isbn;
    }

    public Book(String key, String title, String author, String isbn) {
        this.openLibraryKey = key;
        this.title = title;
        this.author = author;
        this.isbn = isbn;
    }

    public Set<Genre> getGenres() {
        return genres;
    }

    public void setGenres(Set<Genre> genres) {
        this.genres = genres;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public Branch getBranch() {
        return branch;
    }

    public void setBranch(Branch branch) {
        this.branch = branch;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Book book = (Book) o;
        return id != null && id.equals(book.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

}