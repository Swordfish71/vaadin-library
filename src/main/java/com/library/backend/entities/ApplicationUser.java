package com.library.backend.entities;

import com.library.security.Role;
import jakarta.persistence.*;
import jakarta.annotation.Nonnull;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "app_users")
public class ApplicationUser implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    // element collection to allow multiple roles to be added to a user, but they must be an enum
    @ElementCollection(fetch = FetchType.EAGER) // load roles for the user on every fetch of the user
    // table to store the role/user associations
    @CollectionTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id")
    )
    @Enumerated(EnumType.STRING) // important to use the string value of the enum, not its index
    @Column(name = "role")
    private final Set<Role> roles = Set.of(Role.USER); // default to USER role

    // user "owns" favourites relationship, not the book
    // we choose to update the user rather than the book in case multiple users concurrently
    // were to try to modify their favourites, leading to a race condition on the book side
    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "user_favourite_books",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "book_id")
    )
    private final Set<Book> favouriteBooks = new HashSet<>();

    public ApplicationUser() {}

    public ApplicationUser(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public Long getId() { return id; }

    // Roles
    public Set<Role> getRoles() { return roles; }
    public void addRole(Role role) {
        roles.add(role);
    }
    public void revokeRole(Role role) {
        roles.remove(role);
    }

    // Favourite Books
    public Set<Book> getFavouriteBooks() { return favouriteBooks;
    }
    public void addFavouriteBook(Book book) {
        favouriteBooks.add(book);
    }
    public void removeFavouriteBook(Book book) {
        favouriteBooks.remove(book);
    }


    // UserDetails methods
    @Override
    public @Nonnull Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .collect(Collectors.toSet());
    }

    @Override
    public @Nonnull String getPassword() {
        return password;
    }

    @Override
    public @Nonnull String getUsername() {
        return username;
    }
}
