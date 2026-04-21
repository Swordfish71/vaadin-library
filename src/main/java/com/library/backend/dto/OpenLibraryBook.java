package com.library.backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record OpenLibraryBook(
        String key,
        String title,
        @JsonProperty("author_name") List<String> authorNames
) {
    public String getFormattedAuthors() {
        if(authorNames == null || authorNames.isEmpty()) return "Unknown Author";
        return String.join(", ", authorNames);
    }
}
