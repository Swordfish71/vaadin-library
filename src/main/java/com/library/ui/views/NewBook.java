package com.library.ui.views;

import com.library.backend.entities.Book;
import com.library.backend.entities.BookRepository;
import com.library.security.Role;
import com.library.ui.components.BookForm;
import com.library.ui.components.ViewToolbar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

@Route("books/new")
@RolesAllowed(Role.RoleConstants.ADMIN)
public class NewBook extends VerticalLayout {
    private final BookRepository bookRepo;
    private final Book book = new Book();
    private final BookForm bookForm = new BookForm();
    private final Button backBtn = new Button("Back to All Books", VaadinIcon.ARROW_LEFT.create());

    public NewBook(BookRepository bookRepo) {
        this.bookRepo = bookRepo;

        BookForm bookform = new BookForm();
        bookform.setBook(new Book());
        bookform.setEditable(true);
        bookform.addSaveListener(this::saveBook);
        bookform.addCancelListener(() -> getUI().ifPresent(ui -> ui.navigate("books")));

        add(new ViewToolbar("New Book"), bookform);
    }

    private void saveBook(Book book) {
        bookRepo.save(book);
        getUI().ifPresent(ui -> ui.navigate("books"));
    }
}
