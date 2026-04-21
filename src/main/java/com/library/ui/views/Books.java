package com.library.ui.views;

import com.library.backend.entities.Book;
import com.library.backend.entities.BookRepository;
import com.library.backend.service.UserService;
import com.library.ui.components.BookGrid;
import com.library.ui.components.SearchBar;
import com.library.ui.components.ViewToolbar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;
import jakarta.annotation.security.PermitAll;

@Route("books")
@PageTitle("Catalogue")
@Menu(order = 1, icon = "vaadin:book", title = "Catalogue")
@PermitAll
public class Books extends VerticalLayout implements BeforeEnterObserver {
    private final BookRepository bookRepo;
    private final UserService userService;

    public Books(BookRepository bookRepo, UserService userService) {
        this.bookRepo = bookRepo;
        this.userService = userService;

        BookGrid grid = new BookGrid(this.bookRepo::findAll);

        // Add Favourites Button
        grid.addFavoriteColumn(
            this.userService.getFavouriteBooks(),
            this.userService::toggleFavouriteBooks
        );

        // navigate to Book Details page when I click on the grid item for that book
        grid.addItemClickListener(click -> {
            Book targetBook = click.getItem();
            getUI().ifPresent(ui -> ui.navigate("books/" + targetBook.getId()));
        });

        Button addBtn = new Button("Add New Book");
        // set mouse to pointer
        addBtn.getElement().setAttribute("style", "cursor: pointer;");
        addBtn.addClickListener(click -> {
            getUI().ifPresent(ui -> ui.navigate("books/new")); // programmatically navigate
        });

        // hide and disable the addBtn if user is not admin
        if(!userService.isAdmin()) {
            addBtn.setVisible(false);
            addBtn.setEnabled(false);
        }

        SearchBar searchBar = new SearchBar(grid::filter, true);

        // this is the top bar for the page
        ViewToolbar toolbar = new ViewToolbar("Catalogue", addBtn, searchBar);

        // add the top bar and the grid to the overall vertical layout
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
                    Notification.show("Book deleted!").addThemeVariants(NotificationVariant.LUMO_ERROR);
                    break;
                default:
                    break;
            }
        }
    }
}
