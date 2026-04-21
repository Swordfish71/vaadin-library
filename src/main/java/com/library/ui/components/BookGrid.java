package com.library.ui.components;

import com.library.backend.entities.Book;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.data.provider.ListDataProvider;
import utils.StringUtils;

import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class BookGrid  extends Grid<Book> {
    private final Supplier<List<Book>> dataFetcher;
    private ListDataProvider<Book> dataProvider;

    public BookGrid(Supplier<List<Book>> dataFetcher) {
        this.dataFetcher = dataFetcher;

        refreshData();

        addColumn(Book::getTitle).setHeader("Title").setSortable(true);
        addColumn(Book::getAuthor).setHeader("Author").setSortable(true);
        addColumn(Book::getIsbn).setHeader("ISBN");
    }

    private void refreshData() {
        List<Book> freshData = dataFetcher.get();
        this.dataProvider = new ListDataProvider<>(freshData);
        setDataProvider(dataProvider);
    }

    public void filter(String searchTerm) {
        dataProvider.setFilter(book ->
            searchTerm == null ||
            searchTerm.isEmpty() ||
            StringUtils.containsIgnoreCase(book.getTitle(), searchTerm) ||
            StringUtils.containsIgnoreCase(book.getAuthor(), searchTerm) ||
            StringUtils.containsIgnoreCase(book.getIsbn(), searchTerm)
        );
    }

    // THE NEW "DUMB" HOOK
    // It only needs the username to check the set, and a callback method for the click
    public void addFavoriteColumn(Set<Book> favouriteBooks, Consumer<Book> onFavoriteToggle) {

        addComponentColumn(book -> {
            boolean isFavourited = favouriteBooks.contains(book);

            Icon starIcon = isFavourited ? VaadinIcon.STAR.create() : VaadinIcon.STAR_O.create();
            starIcon.setColor(isFavourited ? "gold" : "gray");

            Button favBtn = new Button(starIcon);
            favBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_ICON);

            favBtn.addClickListener(click -> {
                // update db
                onFavoriteToggle.accept(book);

                // mutate UI state
                if (isFavourited) {
                    favouriteBooks.remove(book);
                } else {
                    favouriteBooks.add(book);
                }

                // reload single row
                this.dataProvider.refreshItem(book);
            });

            return favBtn;

        }).setHeader("Favorite").setAutoWidth(true).setFlexGrow(0);
    }

}
