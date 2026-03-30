package com.library.ui.views;

import com.library.backend.Book;
import com.library.backend.BookRepository;
import com.library.security.Roles;
import com.library.ui.components.BookForm;
import com.library.ui.components.ViewToolbar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.security.AuthenticationContext;
import jakarta.annotation.security.PermitAll;


@Route("books")
@PermitAll
public class BookDetails extends VerticalLayout implements HasUrlParameter<Long> {
    private final BookRepository bookRepo;
    private final AuthenticationContext authContext;

    private Book book;
    private final BookForm bookForm = new BookForm();

    private final Button backBtn = new Button("Back to All Books", VaadinIcon.ARROW_LEFT.create());
    private final Button editBtn = new Button(VaadinIcon.PENCIL.create());
    private final Button deleteBtn = new Button(VaadinIcon.TRASH.create());

    private final ViewToolbar toolbar = new ViewToolbar("Book Details", backBtn);

    public BookDetails(BookRepository bookRepo, AuthenticationContext authContext) {
        this.bookRepo = bookRepo;
        this.authContext = authContext;

        configureLayout();
        configureButtons();
    }

    @Override
    public void setParameter(BeforeEvent beforeEvent, Long bookId) {
        bookRepo.findById(bookId).ifPresentOrElse(
            (b) -> {
                this.book = b;
                toolbar.setTitle(b.getTitle());
                bookForm.setBook(b);
                setIsEditing(false);
            },
            () -> beforeEvent.forwardTo("books")
        );
    }

    private void configureLayout() {
        setIsEditing(false);
        HorizontalLayout actions = new HorizontalLayout();

        if(authContext.hasRole(Roles.ADMIN)) {
            actions.add(editBtn, deleteBtn);
        } else {
            editBtn.setEnabled(false);
            deleteBtn.setEnabled(false);
        }

        add(toolbar, bookForm, actions);
    }

    private void saveBook(Book book) {
        bookRepo.save(book);
        Notification.show("Book saved").addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        setIsEditing(false);
    }

    private void cancelEdit() {
        setIsEditing(false);
    }

    private void deleteBook(Book book) {
        bookRepo.delete(book);
        getUI().ifPresent(ui -> ui.navigate("books?message=deleted"));
    }

    private void configureButtons() {
        // Styling
        deleteBtn.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_TERTIARY);

        // Logic
        editBtn.addClickListener(e -> setIsEditing(true));

        deleteBtn.addClickListener(e -> {
            ConfirmDialog dialog = new ConfirmDialog();
            dialog.setHeader("Delete Book?");
            dialog.setText("Are you sure you want to delete this book?");
            dialog.setCancelable(true);
            dialog.setConfirmText("Delete");
            dialog.setConfirmButtonTheme("error primary");
            dialog.addConfirmListener(event -> this.deleteBook(book));
            dialog.open();
        });

        backBtn.addClickListener(e -> {
            getUI().ifPresent(ui -> ui.navigate("books"));
        });

        bookForm.addSaveListener(this::saveBook);
        bookForm.addCancelListener(this::cancelEdit);
    }

    private void setIsEditing(boolean isEditing) {
        editBtn.setVisible(!isEditing);
        deleteBtn.setVisible(!isEditing);
        bookForm.setEditable(isEditing);
    }

}
