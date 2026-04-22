package service;

import java.util.*;
import model.*;
import database.DatabaseManager;

public class StoreService {

    public List<Product>  inventory = DatabaseManager.loadProducts();
    public List<CartItem> cart      = new ArrayList<>();
    public List<User>     users     = new ArrayList<>();

    // ── Persistence ───────────────────────────────────────────────────────────
    public void save() {
        DatabaseManager.saveProducts(inventory);
    }

    // ── Auth ──────────────────────────────────────────────────────────────────
    public boolean login(String username, String password) {
        return users.stream()
            .anyMatch(u -> u.getUsername().equals(username)
                       && u.getPassword().equals(password));
    }

    public void register(String username, String password) {
        // Prevent duplicate registrations
        if (users.stream().noneMatch(u -> u.getUsername().equals(username))) {
            users.add(new User(username, password));
        }
    }

    // ── Inventory ─────────────────────────────────────────────────────────────
    /**
     * Add a new product or increase stock if a product with the same ID or
     * name already exists.
     */
    public void addProduct(Product p) {
        Product existing = inventory.stream()
            .filter(x -> x.getId().equalsIgnoreCase(p.getId())
                      || x.getName().equalsIgnoreCase(p.getName()))
            .findFirst().orElse(null);

        if (existing != null) {
            // Update price and add to stock
            existing.setPrice(p.getPrice());
            existing.setQuantity(existing.getQuantity() + p.getQuantity());
        } else {
            inventory.add(p);
        }
        save();
    }

    public void deleteProduct(String id) {
        inventory.removeIf(p -> p.getId().equals(id));
        // Also remove from cart if present
        cart.removeIf(c -> c.getProduct().getId().equals(id));
        save();
    }

    // ── Cart ──────────────────────────────────────────────────────────────────
    /**
     * Add qty units of p to the cart.
     * Validates against actual remaining stock (stock minus what's already carted).
     */
    public boolean addToCart(Product p, int qty) {
        if (qty <= 0) return false;

        int alreadyCarted = cart.stream()
            .filter(c -> c.getProduct().getId().equals(p.getId()))
            .mapToInt(CartItem::getQuantity)
            .sum();

        // Ensure we don't exceed real stock
        if (alreadyCarted + qty > p.getQuantity()) return false;

        CartItem existing = cart.stream()
            .filter(c -> c.getProduct().getId().equals(p.getId()))
            .findFirst().orElse(null);

        if (existing != null) {
            existing.setQuantity(existing.getQuantity() + qty);
        } else {
            cart.add(new CartItem(p, qty));
        }
        return true;
    }

    /** Grand total of all items in the cart. */
    public double cartTotal() {
        return cart.stream()
            .mapToDouble(c -> c.getProduct().getPrice() * c.getQuantity())
            .sum();
    }

    // ── Checkout ──────────────────────────────────────────────────────────────
    /**
     * Validate the card (must be exactly 16 digits), deduct stock, persist,
     * and clear the cart.
     */
    public boolean checkout(String card) {
        if (card == null || !card.matches("\\d{16}")) return false;

        for (CartItem c : cart) {
            Product p = c.getProduct();
            p.setQuantity(p.getQuantity() - c.getQuantity());
        }

        save();
        cart.clear();
        return true;
    }
}
