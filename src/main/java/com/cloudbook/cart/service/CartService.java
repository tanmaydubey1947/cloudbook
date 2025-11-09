package com.cloudbook.cart.service;

import com.cloudbook.auth.model.User;
import com.cloudbook.auth.service.auth.AuthUserDetails;
import com.cloudbook.cart.dto.CartRequest;
import com.cloudbook.cart.dto.CartResponse;
import com.cloudbook.cart.model.Cart;
import com.cloudbook.cart.model.CartItem;
import com.cloudbook.cart.repository.CartRepository;
import com.cloudbook.cart.util.CartMapper;
import com.cloudbook.catalog.model.Book;
import com.cloudbook.catalog.repository.CatalogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CatalogRepository bookRepository;

    @Autowired
    private CartMapper cartMapper;

    public CartResponse getCurrentUserCart() {
        String username = getCurrentUsername();
        Cart cart = cartRepository.findByUsername(username).orElseGet(() -> {
            Cart newCart = new Cart(username);
            return cartRepository.save(newCart);
        });
        return cartMapper.toResponse(cart);
    }

    public CartResponse addToCart(CartRequest request) {
        String username = getCurrentUsername();
        Cart cart = cartRepository.findByUsername(username)
                .orElseGet(() -> new Cart(username));

        Book book = bookRepository.findById(request.getBookId())
                .orElseThrow(() -> new RuntimeException("Book not found"));

        if (request.getQuantity() <= 0) {
            throw new RuntimeException("Quantity must be positive");
        }

        Optional<CartItem> existing = cart.getItems().stream()
                .filter(i -> i.getBook().getId().equals(book.getId()))
                .findFirst();

        if (existing.isPresent()) {
            existing.get().setQuantity(existing.get().getQuantity() + request.getQuantity());
        } else {
            cart.getItems().add(new CartItem(book, request.getQuantity(), cart));
        }

        Cart saved = cartRepository.save(cart);
        return cartMapper.toResponse(saved);
    }

    public CartResponse removeItem(String bookId) {
        String username = getCurrentUsername();
        UUID bookUUID = UUID.fromString(bookId);
        Cart cart = cartRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Cart not found"));
        cart.getItems().removeIf(i -> i.getBook().getId().equals(bookUUID));
        Cart saved = cartRepository.save(cart);
        return cartMapper.toResponse(saved);
    }

    public CartResponse clearCart() {
        String username = getCurrentUsername();
        Cart cart = cartRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Cart not found"));
        cart.getItems().clear();
        Cart saved = cartRepository.save(cart);
        return cartMapper.toResponse(saved);
    }

    private String getCurrentUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getPrincipal() == null) {
            throw new RuntimeException("Unauthenticated");
        }

        Object principal = auth.getPrincipal();
        if (principal instanceof User user) {
            return user.getUsername();
        } else if (principal instanceof AuthUserDetails userDetails) {
            return userDetails.getUsername();
        }
        throw new RuntimeException("Invalid authentication principal");
    }
}
