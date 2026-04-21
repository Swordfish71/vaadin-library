package com.library.backend.service;

import com.library.backend.dto.OpenLibraryBook;
import com.library.backend.dto.OpenLibraryResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Collections;
import java.util.List;

@Service
public class OpenLibraryService {
    private final RestClient restClient;
    private final int MAX_RESULTS = 10;
    private Runnable onResponseError;

    public OpenLibraryService() {
        this.restClient = RestClient.builder()
                .baseUrl("https://openlibrary.org")
                .build();
    }

    public OpenLibraryService(Runnable onResponseError) {
        this();
        this.onResponseError = onResponseError;
    }

    public List<OpenLibraryBook> searchBooks(String searchType, String query) {
        if(query == null || query.isBlank()) return Collections.emptyList();

        try {
            OpenLibraryResponse response = restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/search.json")
                            .queryParam(searchType, query)
                            .queryParam("limit", MAX_RESULTS)
                            .build()
                    )
                    .retrieve()
                    .body(OpenLibraryResponse.class);
            return response != null && response.docs() != null ? response.docs() : Collections.emptyList();
        } catch (Exception e) {
            System.err.println("Failed to fetch books from Open Library");
            if(onResponseError != null) onResponseError.run(); // notify the caller
            return Collections.emptyList();
        }
    }

}
