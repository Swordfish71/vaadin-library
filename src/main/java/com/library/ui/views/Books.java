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
@PermitAll
public class Books extends VerticalLayout implements BeforeEnterObserver {
    private final MockBookRepository bookRepo;
    private final AuthenticationContext authContext;

    public Books(MockBookRepository bookRepo, AuthenticationContext authContext) {
        this.bookRepo = bookRepo;
        this.authContext = authContext;

        BookGrid grid = new BookGrid(this.bookRepo.findAll());

        // navigate to book details view when clicking a row in the grid
        grid.addItemClickListener(event -> {
            getUI().ifPresent(ui -> ui.navigate("books/" + event.getItem().getId()));
        });

        Button createBtn = new Button("Add New Book", VaadinIcon.PLUS.create());
        createBtn.addClickListener(e -> {
            getUI().ifPresent(ui -> ui.navigate("books/new"));
        });

        if(!authContext.hasRole(Roles.ADMIN)) {
            createBtn.setVisible(false);
            createBtn.setEnabled(false);
        };

        // Eager search bar (refreshes data on keystroke)
        SearchBar searchBar = new SearchBar(grid::filter, true);

        ViewToolbar toolbar = new ViewToolbar("Catalogue", createBtn, searchBar);

        add(toolbar, grid);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        var queryParams = beforeEnterEvent.getLocation().getQueryParameters().getParameters();
        if(queryParams.containsKey("message")) {
            String message = queryParams.get("message").getFirst();
            switch (message) {
                case "created":
                    Notification.show("Book created!").addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                    break;
                case "deleted":
                    Notification.show("Book deleted!").addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                    break;
                default:
                    break;
            }
        }
    }
}
