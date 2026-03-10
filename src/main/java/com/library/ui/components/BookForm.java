package com.library.ui.components;

import com.library.backend.Book;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;

import java.util.function.Consumer;

public class BookForm extends VerticalLayout {
    private Book book;
    private Binder<Book> binder;

    private final FormLayout formLayout = new FormLayout();
    private final Button saveBtn = new Button("Save");
    private final Button cancelBtn = new Button("Cancel");

    private Consumer<Book> onSave;
    private Runnable onCancel;

    public BookForm() {
        binder = new Binder<>(Book.class);

        TextField title = new TextField("Title");
        TextField author = new TextField("Author");
        TextField isbn = new TextField("ISBN");

        binder.forField(title)
                .asRequired()
                .bind(Book::getTitle, Book::setTitle);
        binder.forField(author)
                .asRequired()
                .bind(Book::getAuthor, Book::setAuthor);
        binder.forField(isbn)
                .asRequired()
                .bind(Book::getIsbn, Book::setIsbn);

        formLayout.setResponsiveSteps(new FormLayout.ResponsiveStep("0",1));

        formLayout.add(title, author, isbn);

        configureButtons();

        add(formLayout, new HorizontalLayout(saveBtn, cancelBtn));
    }

    private void configureButtons() {
        // Styling
        saveBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        cancelBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        // Logic
        cancelBtn.addClickListener(e -> {
            resetForm();
            if(onCancel != null) onCancel.run();
        });

        saveBtn.addClickListener(e -> {
            if (binder.writeBeanIfValid(book)) {
                if(onSave != null) onSave.accept(book); // pass book to save listener
            } else {
                binder.validate();
            }
        });
    }

    public void setBook(Book book) { this.book = book; }

    private void resetForm() {
        binder.readBean(book);
    }

    public void addSaveListener(Consumer<Book> onSave) {
        this.onSave = onSave;
    }

    public void addCancelListener(Runnable onCancel) {
        this.onCancel = onCancel;
    }

    public void setEditable(boolean isEditing) {
        binder.getFields().forEach(field -> field.setReadOnly(!isEditing));
        saveBtn.setEnabled(isEditing);
        saveBtn.setVisible(isEditing);
        cancelBtn.setEnabled(isEditing);
        cancelBtn.setVisible(isEditing);
    }
}
