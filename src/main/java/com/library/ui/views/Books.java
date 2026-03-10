package com.library.ui.views;

import com.library.backend.Book;
import com.library.backend.MockBookRepository;
import com.library.ui.components.BookGrid;
import com.library.ui.components.SearchBar;
import com.library.ui.components.ViewToolbar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route("books")
@PageTitle("Catalogue")
@Menu(order = 1, icon = "vaadin:book", title = "Catalogue")
public class Books extends VerticalLayout {
    private final MockBookRepository bookRepo;

    public Books(MockBookRepository bookRepo) {
        this.bookRepo = bookRepo;

        BookGrid grid = new BookGrid(this.bookRepo.findAll());
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

        SearchBar searchBar = new SearchBar(grid::filter, true);

        // this is the top bar for the page
        ViewToolbar toolbar = new ViewToolbar("Catalogue", addBtn, searchBar);

        // add the top bar and the grid to the overall vertical layout
        add(toolbar, grid);
    }

}
