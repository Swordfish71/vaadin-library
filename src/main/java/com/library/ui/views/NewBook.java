package com.library.ui.views;

import com.library.backend.Book;
import com.library.backend.MockBookRepository;
import com.library.security.Roles;
import com.library.ui.components.BookForm;
import com.library.ui.components.ViewToolbar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

@Route("books/new")
public class NewBook extends VerticalLayout {
    private final MockBookRepository bookRepo;
    private final Book book = new Book();
    private final BookForm bookForm = new BookForm();
    private final Button backBtn = new Button("Back to All Books", VaadinIcon.ARROW_LEFT.create());

    public NewBook(MockBookRepository bookRepo) {
        this.bookRepo = bookRepo;

        bookForm.setBook(book);
        bookForm.addSaveListener(this::saveBook);
        bookForm.setEditable(true);

        backBtn.addClickListener(e -> {
            getUI().ifPresent(ui -> ui.navigate("books"));
        });

        add(new ViewToolbar("New Book", backBtn), bookForm);

    }

    private void saveBook(Book book) {
        bookRepo.save(book);
        getUI().ifPresent(ui -> ui.navigate("books?message=created"));
    }

}
