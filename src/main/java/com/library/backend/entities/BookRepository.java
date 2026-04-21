package com.library.backend.entities;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    boolean existsByOpenLibraryKey(String openLibraryKey);
    void deleteByOpenLibraryKey(String openLibraryKey);
}

