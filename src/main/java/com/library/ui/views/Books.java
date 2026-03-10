package com.library.ui.views;

import com.library.backend.MockBookRepository;
import com.library.security.Roles;
import com.library.ui.components.BookGrid;
import com.library.ui.components.SearchBar;
import com.library.ui.components.ViewToolbar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;
import com.vaadin.flow.spring.security.AuthenticationContext;
import jakarta.annotation.security.PermitAll;

@Route("books")
@PageTitle("Catalogue")
@Menu(order = 1, icon = "vaadin:book", title = "Catalogue")
public class Books extends VerticalLayout {
    private final MockBookRepository bookRepo;

    public Books(MockBookRepository bookRepo) {
        this.bookRepo = bookRepo;

        BookGrid grid = new BookGrid(this.bookRepo.findAll());

        // navigate to book details view when clicking a row in the grid
        grid.addItemClickListener(event -> {
            getUI().ifPresent(ui -> ui.navigate("books/" + event.getItem().getId()));
        });

        Button createBtn = new Button("Add New Book", VaadinIcon.PLUS.create());
        createBtn.addClickListener(e -> {
            getUI().ifPresent(ui -> ui.navigate("books/new"));
        });


        // Eager search bar (refreshes data on keystroke)
        SearchBar searchBar = new SearchBar(grid::filter, true);

        ViewToolbar toolbar = new ViewToolbar("Catalogue", createBtn, searchBar);

        add(toolbar, grid);
    }

}
