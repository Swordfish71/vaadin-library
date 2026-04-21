package com.library.ui.views;

import com.library.backend.entities.Book;
import com.library.backend.entities.BookRepository;
import com.library.backend.entities.BranchRepository;
import com.library.backend.entities.GenreRepository;
import com.library.backend.service.UserService;
import com.library.ui.components.BookForm;
import com.library.ui.components.ViewToolbar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;


@Route("books")
@PermitAll
public class BookDetails extends VerticalLayout implements HasUrlParameter<Long> {
    private final BookRepository bookRepo;
    private final GenreRepository genreRepo;
    private final BranchRepository branchRepo;
    private final UserService userService;

    private Book book;
    private final BookForm bookForm = new BookForm();
    private Button editBtn = new Button("Edit");
    private Button deleteBtn = new Button("Delete");

    public BookDetails(
            BookRepository bookRepo,
            GenreRepository genreRepo,
            BranchRepository branchRepo,
            UserService userService
    ) {
        this.bookRepo = bookRepo;
        this.genreRepo = genreRepo;
        this.branchRepo = branchRepo;
        this.userService = userService;

        bookForm.setEditable(false);
        bookForm.addSaveListener(this::saveBook);
        bookForm.addCancelListener(this::cancelEdit);
        bookForm.setGenres(this.genreRepo.findAll());
        bookForm.setBranches(this.branchRepo.findAll());

        configureLayout();
        configureButtons();
    }

    private void configureLayout() {
        Button backBtn = new Button("Back to All Books");
        backBtn.addClickListener(click -> getUI().ifPresent(ui -> ui.navigate("books")));
        ViewToolbar toolbar = new ViewToolbar("Book Details", backBtn);
        HorizontalLayout actions = new HorizontalLayout(editBtn, deleteBtn);
        add(toolbar, bookForm, actions);
    }

    private void configureButtons() {
        // hide and disable edit/delete buttons if not Admin
        if(!userService.isAdmin()) {
            editBtn.setVisible(false);
            editBtn.setEnabled(false);
            deleteBtn.setVisible(false);
            deleteBtn.setEnabled(false);
            return;
        }

        // style
        editBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        deleteBtn.addThemeVariants(ButtonVariant.LUMO_ERROR);

        // logic
        editBtn.addClickListener(click -> setEditable(true));
        deleteBtn.addClickListener(click -> {
            // open a dialogue box to ask the user to confirm the delete action
            ConfirmDialog confirmDialog = new ConfirmDialog();
            confirmDialog.setHeader("Delete Book?");
            confirmDialog.setText("Are you sure you want to delete this book?");
            confirmDialog.setCancelable(true);
            confirmDialog.setConfirmText("Delete");
            confirmDialog.setConfirmButtonTheme("error primary");
            confirmDialog.addConfirmListener(event -> this.deleteBook(book));
            confirmDialog.open();
        });
    }


    @Override
    public void setParameter(BeforeEvent beforeEvent, Long bookId) {
        bookRepo.findById(bookId).ifPresentOrElse(
        b -> {
                book = b;
                bookForm.setBook(book);
            },
            () -> beforeEvent.forwardTo("books")
        );
    }

    private void setEditable(boolean isEditing) {
        bookForm.setEditable(isEditing);

        // turn off edit and delete buttons when I am editing
        editBtn.setEnabled(!isEditing);
        editBtn.setVisible(!isEditing);
        deleteBtn.setEnabled(!isEditing);
        deleteBtn.setVisible(!isEditing);
    }

    private void cancelEdit() {
        setEditable(false);
        bookForm.resetForm();
    }

    private void saveBook(Book book) {
        bookRepo.save(book);
        setEditable(false);
    }

    private void deleteBook(Book book) {
        bookRepo.delete(book);
        getUI().ifPresent(ui -> ui.navigate("books"));
    }
}
