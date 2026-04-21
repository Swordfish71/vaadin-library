package com.library.ui.views;

import com.library.backend.entities.Book;
import com.library.backend.service.UserService;
import com.library.ui.components.BookGrid;
import com.library.ui.components.ViewToolbar;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;

@Route("books/favourites")
@PageTitle("My Books")
@Menu(order = 3, title = "My Books", icon = "vaadin:star")
@PermitAll
public class MyBooks extends VerticalLayout {
    private final UserService userService;

    public MyBooks(UserService userService) {
        this.userService = userService;

        BookGrid grid = new BookGrid(() -> this.userService.getFavouriteBooks().stream().toList());
        grid.addFavoriteColumn(
            this.userService.getFavouriteBooks(),
            this.userService::toggleFavouriteBooks
        );

        // navigate to Book Details page when I click on the grid item for that book
        grid.addItemClickListener(click -> {
            Book targetBook = click.getItem();
            getUI().ifPresent(ui -> ui.navigate("books/" + targetBook.getId()));
        });

        ViewToolbar header = new ViewToolbar("My Books");

        add(header, grid);
    }
}
