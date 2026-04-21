package com.library.backend.service;

import com.library.backend.entities.ApplicationUser;
import com.library.backend.entities.Book;
import com.library.backend.entities.UserRepository;
import com.library.security.Role;
import com.vaadin.flow.spring.security.AuthenticationContext;
import jakarta.annotation.Nonnull;
import jakarta.transaction.Transactional;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

@Service
public class UserService implements UserDetailsService {
    private final UserRepository userRepo;
    private final AuthenticationContext authContext;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepo, AuthenticationContext authContext, PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.authContext = authContext;
        this.passwordEncoder = passwordEncoder;
    }

    public void createUser(String username, String rawPassword) {
        String hashedPassword = passwordEncoder.encode(rawPassword);
        ApplicationUser newUser = new ApplicationUser(username, hashedPassword);
        userRepo.save(newUser);
    }

    public boolean userExists(String username) {
        return userRepo.findByUsername(username).isPresent();
    }

    @Transactional
    public void toggleFavouriteBooks(Book book) {
        Long sessionUserId = getSessionUserId().orElse(null);
        if(sessionUserId == null) return;

        // load a fresh user in the db transaction (important for hibernate to merge updates)
        ApplicationUser activeUser = userRepo.findById(sessionUserId).orElse(null);
        if(activeUser == null) return;

        if(activeUser.getFavouriteBooks().contains(book)) {
            activeUser.removeFavouriteBook(book);
        } else {
            activeUser.addFavouriteBook(book);
        }
        userRepo.save(activeUser);
    }

    public Set<Book> getFavouriteBooks() {
        Long sessionUserId = getSessionUserId().orElse(null);
        if(sessionUserId == null) return Set.of();

        // load a fresh user from the db
        ApplicationUser activeUser = userRepo.findById(sessionUserId).orElse(null);
        if(activeUser == null) return Set.of();

        return activeUser.getFavouriteBooks();
    }

    public boolean isAdmin() {
        return authContext.getAuthenticatedUser(ApplicationUser.class)
                .map(user -> user.getRoles().contains(Role.ADMIN)).orElse(false);
    }

    private Optional<Long> getSessionUserId() {
        return authContext.getAuthenticatedUser(ApplicationUser.class).map(ApplicationUser::getId);
    }

    @Override
    public @Nonnull UserDetails loadUserByUsername(@Nonnull String username) throws UsernameNotFoundException {
        return userRepo.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}
