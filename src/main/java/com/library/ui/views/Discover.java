package com.library.ui.views;

import com.library.backend.dto.OpenLibraryBook;
import com.library.backend.entities.Book;
import com.library.backend.entities.BookRepository;
import com.library.backend.service.OpenLibraryService;
import com.library.security.Role;
import com.library.ui.components.ViewToolbar;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

import java.util.List;

@Route("discover")
@PageTitle("Discover Books")
@Menu(order = 2, title = "Discover", icon = "vaadin:search")
@RolesAllowed(Role.RoleConstants.ADMIN)
public class Discover extends VerticalLayout {
    private final OpenLibraryService openLibraryService;
    private final BookRepository bookRepo;
    private final Grid<OpenLibraryBook> grid;

    public Discover(OpenLibraryService openLibraryService, BookRepository bookRepo) {
        this.openLibraryService = openLibraryService;
        this.bookRepo = bookRepo;

        setSizeFull();

        // --- Search Bar Components ---
        ComboBox<String> searchType = new ComboBox<>("Search By");
        searchType.setItems("author", "subject");
        searchType.setValue("author"); // Default value

        TextField searchField = new TextField("Search Query");
        searchField.setPlaceholder("e.g. tolkien, fantasy...");
        searchField.setWidth("300px");

        Button searchButton = new Button("Search");
        searchButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        // Let users press 'Enter' to search
        searchButton.addClickShortcut(Key.ENTER);

        HorizontalLayout searchLayout = new HorizontalLayout(searchType, searchField, searchButton);
        searchLayout.setDefaultVerticalComponentAlignment(Alignment.BASELINE);
        ViewToolbar header = new ViewToolbar("Search Open Library", searchLayout);

        // --- Grid Configuration ---
        grid = new Grid<>(OpenLibraryBook.class, false); // false disables auto-generated columns
        grid.addColumn(OpenLibraryBook::title).setHeader("Title").setAutoWidth(true);
        grid.addColumn(OpenLibraryBook::getFormattedAuthors).setHeader("Author(s)").setAutoWidth(true);
        grid.addComponentColumn(openLibraryBook -> {
            Button removeBtn = new Button("Remove from library");
            Button addBtn = new Button("Add to library");

            removeBtn.addClickListener(click -> {
                this.bookRepo.deleteByOpenLibraryKey(openLibraryBook.key());
                grid.getDataProvider().refreshItem(openLibraryBook);
            });
            addBtn.addClickListener(click -> {
                this.bookRepo.save(new Book(
                        openLibraryBook.key(),
                        openLibraryBook.title(),
                        openLibraryBook.getFormattedAuthors(),
                        null
                ));
                grid.getDataProvider().refreshItem(openLibraryBook);
            });

            return this.bookRepo.existsByOpenLibraryKey(openLibraryBook.key()) ? removeBtn : addBtn;
        });

        // --- Search Logic ---
        searchButton.addClickListener(e -> {
            String type = searchType.getValue();
            String query = searchField.getValue();

            // Show a loading state (optional but good UX)
            grid.setItems();

            // Fetch from API and update the grid
            List<OpenLibraryBook> results = this.openLibraryService.searchBooks(type, query);
            grid.setItems(results);
        });

        add(header, grid);
    }
}
