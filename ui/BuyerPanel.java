package ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;
import main.MainApp;
import model.*;

public class BuyerPanel extends JPanel {

    private JTable            table;
    private DefaultTableModel model;
    private JTextField        searchField;
    private JLabel            cartBadge;
    private MainApp           app;

    public BuyerPanel(MainApp app) {
        this.app = app;
        setLayout(new BorderLayout());
        setBackground(UITheme.C_BG);

        // ── Header ─────────────────────────────────────────────────────────────
        JPanel header = UITheme.headerBar();
        header.add(UITheme.headerTitle(" SwiftCart  —  Product Catalogue"), BorderLayout.WEST);

        JPanel headerRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        headerRight.setOpaque(false);

        cartBadge = new JLabel("Cart (0)");
        cartBadge.setFont(new Font("Segoe UI", Font.BOLD, 13));
        cartBadge.setForeground(new Color(0xBFDBFE));

        // change the background of view cart button to a lighter white and text to blue
        // change the code to set the background of view cart button to a lighter white and text to blue and ther shuould not be hover effect on the button and the border should be a light white rounded border

        UITheme.RBtn cartBtn = UITheme.outlineBtn("View Cart");
        cartBtn.setForeground(Color.BLUE);
        cartBtn.setBorder(BorderFactory.createCompoundBorder(
            new javax.swing.border.LineBorder(new Color(0x60A5FA), 1, true),
            new EmptyBorder(7, 16, 7, 16)
        ));
        UITheme.RBtn logoutBtn = UITheme.outlineBtn("Logout");
        logoutBtn.setForeground(Color.BLUE);
        logoutBtn.setBorder(BorderFactory.createCompoundBorder(
            new javax.swing.border.LineBorder(new Color(0x60A5FA), 1, true),
            new EmptyBorder(7, 16, 7, 16)
        ));

        cartBtn.addActionListener(e  -> showCart());
        logoutBtn.addActionListener(e -> app.logout());

        headerRight.add(cartBadge);
        headerRight.add(cartBtn);
        headerRight.add(logoutBtn);
        header.add(headerRight, BorderLayout.EAST);

        // ── Toolbar: search + action ────────────────────────────────────────────
        JPanel toolbar = new JPanel(new BorderLayout(12, 0));
        toolbar.setBackground(UITheme.C_SURFACE);
        toolbar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, UITheme.C_BORDER),
            new EmptyBorder(14, 20, 14, 20)
        ));

        searchField = UITheme.textField();
        searchField.setPreferredSize(new Dimension(280, 38));
        searchField.setToolTipText("Search products...");

        // Placeholder hint
        searchField.setText("Search products...");
        searchField.setForeground(UITheme.C_TXT_L);
        searchField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent e) {
                if (searchField.getText().equals("Search products...")) {
                    searchField.setText("");
                    searchField.setForeground(UITheme.C_TXT_D);
                }
            }
            public void focusLost(java.awt.event.FocusEvent e) {
                if (searchField.getText().isEmpty()) {
                    searchField.setText("Search products...");
                    searchField.setForeground(UITheme.C_TXT_L);
                }
            }
        });
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate (javax.swing.event.DocumentEvent e) { filterTable(); }
            public void removeUpdate (javax.swing.event.DocumentEvent e) { filterTable(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filterTable(); }
        });

        UITheme.RBtn addBtn = UITheme.primaryBtn("+ Add to Cart");

        JPanel toolLeft = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        toolLeft.setOpaque(false);
        toolLeft.add(searchField);

        JPanel toolRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        toolRight.setOpaque(false);
        toolRight.add(addBtn);

        toolbar.add(toolLeft,  BorderLayout.WEST);
        toolbar.add(toolRight, BorderLayout.EAST);

        // ── Table ──────────────────────────────────────────────────────────────
        String[] cols = {"Product ID", "Name", "Unit Price (₹)", "In Stock"};
        model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(model);
        UITheme.styleTable(table);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        // Column widths
        table.getColumnModel().getColumn(0).setPreferredWidth(100);
        table.getColumnModel().getColumn(1).setPreferredWidth(300);
        table.getColumnModel().getColumn(2).setPreferredWidth(140);
        table.getColumnModel().getColumn(3).setPreferredWidth(100);

        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(UITheme.C_BG);
        tablePanel.setBorder(new EmptyBorder(16, 20, 16, 20));
        tablePanel.add(UITheme.scrollPane(table), BorderLayout.CENTER);

        // ── Bottom status bar ──────────────────────────────────────────────────
        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.setBackground(UITheme.C_SURFACE);
        statusBar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, UITheme.C_BORDER),
            new EmptyBorder(10, 20, 10, 20)
        ));
        JLabel statusLbl = UITheme.small("Select a product and click \"+ Add to Cart\"");
        statusBar.add(statusLbl, BorderLayout.WEST);

        // ── Actions ────────────────────────────────────────────────────────────
        addBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this,
                    "Please select a product from the table first.",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String id = (String) model.getValueAt(row, 0);
            Product p = app.service.inventory.stream()
                .filter(x -> x.getId().equals(id))
                .findFirst().orElse(null);
            if (p == null) return;

            // Already in cart amount
            int inCart = app.service.cart.stream()
                .filter(c -> c.getProduct().getId().equals(id))
                .mapToInt(CartItem::getQuantity).sum();
            int available = p.getQuantity() - inCart;

            if (available <= 0) {
                JOptionPane.showMessageDialog(this,
                    "This product is out of stock or fully added to cart.",
                    "Out of Stock", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String input = JOptionPane.showInputDialog(this,
                "Available: " + available + "  |  Product: " + p.getName(),
                "Enter Quantity", JOptionPane.PLAIN_MESSAGE);
            if (input == null) return;

            try {
                int qty = Integer.parseInt(input.trim());
                if (qty <= 0) throw new NumberFormatException();
                if (app.service.addToCart(p, qty)) {
                    updateCartBadge();
                    JOptionPane.showMessageDialog(this,
                        qty + " × " + p.getName() + " added to cart!",
                        "Added \u2713", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this,
                        "Requested quantity exceeds available stock.",
                        "Invalid Quantity", JOptionPane.WARNING_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this,
                    "Please enter a valid positive number.",
                    "Invalid Input", JOptionPane.ERROR_MESSAGE);
            }
        });

        // ── Layout ────────────────────────────────────────────────────────────
        add(header,     BorderLayout.NORTH);
        add(toolbar,    BorderLayout.BEFORE_FIRST_LINE);

        JPanel body = new JPanel(new BorderLayout());
        body.setBackground(UITheme.C_BG);
        body.add(toolbar,   BorderLayout.NORTH);
        body.add(tablePanel, BorderLayout.CENTER);
        body.add(statusBar, BorderLayout.SOUTH);

        add(header, BorderLayout.NORTH);
        add(body,   BorderLayout.CENTER);
    }

    // ── Refresh ───────────────────────────────────────────────────────────────
    public void refresh() {
        populateTable(app.service.inventory);
        updateCartBadge();
    }

    private void populateTable(List<Product> products) {
        model.setRowCount(0);
        for (Product p : products) {
            model.addRow(new Object[]{
                p.getId(), p.getName(),
                String.format("%.2f", p.getPrice()),
                p.getQuantity()
            });
        }
    }

    private void filterTable() {
        String q = searchField.getText().trim().toLowerCase();
        if (q.isEmpty() || q.equals("search products...")) {
            populateTable(app.service.inventory);
            return;
        }
        List<Product> filtered = app.service.inventory.stream()
            .filter(p -> p.getName().toLowerCase().contains(q)
                      || p.getId().toLowerCase().contains(q))
            .collect(Collectors.toList());
        populateTable(filtered);
    }

    private void updateCartBadge() {
        int total = app.service.cart.stream().mapToInt(CartItem::getQuantity).sum();
        cartBadge.setText("Cart (" + total + ")");
    }

    // ── Cart Dialog ───────────────────────────────────────────────────────────
    private void showCart() {
        JDialog dlg = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
            "Your Cart", true);
        dlg.setSize(520, 500);
        dlg.setLocationRelativeTo(this);
        dlg.setLayout(new BorderLayout());
        dlg.getContentPane().setBackground(UITheme.C_BG);

        // Header
        JPanel dlgHeader = new JPanel(new BorderLayout());
        dlgHeader.setBackground(UITheme.C_HEADER);
        dlgHeader.setBorder(new EmptyBorder(16, 20, 16, 20));
        JLabel dlgTitle = new JLabel("\uD83D\uDED2  Shopping Cart");
        dlgTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        dlgTitle.setForeground(Color.WHITE);
        dlgHeader.add(dlgTitle, BorderLayout.WEST);

        // Cart table
        String[] cols = {"Product", "Qty", "Unit Price", "Subtotal"};
        DefaultTableModel cartModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable cartTable = new JTable(cartModel);
        UITheme.styleTable(cartTable);
        cartTable.getColumnModel().getColumn(0).setPreferredWidth(200);

        double total = 0;
        if (app.service.cart.isEmpty()) {
            cartModel.addRow(new Object[]{"(cart is empty)", "", "", ""});
        } else {
            for (CartItem c : app.service.cart) {
                double sub = c.getProduct().getPrice() * c.getQuantity();
                total += sub;
                cartModel.addRow(new Object[]{
                    c.getProduct().getName(),
                    c.getQuantity(),
                    String.format("₹%.2f", c.getProduct().getPrice()),
                    String.format("₹%.2f", sub)
                });
            }
        }

        JPanel tableArea = new JPanel(new BorderLayout());
        tableArea.setBackground(UITheme.C_BG);
        tableArea.setBorder(new EmptyBorder(16, 16, 0, 16));
        tableArea.add(UITheme.scrollPane(cartTable), BorderLayout.CENTER);

        // Total + actions
        JPanel footer = new JPanel(new BorderLayout(16, 0));
        footer.setBackground(UITheme.C_SURFACE);
        footer.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, UITheme.C_BORDER),
            new EmptyBorder(16, 20, 16, 20)
        ));

        JLabel totalLbl = UITheme.h2("Total:  ₹" + String.format("%.2f", total));
        totalLbl.setForeground(UITheme.C_PRIMARY);

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnRow.setOpaque(false);

        UITheme.RBtn clearBtn    = UITheme.dangerBtn("Clear Cart");
        UITheme.RBtn checkoutBtn = UITheme.successBtn("Checkout");

        double finalTotal = total;
        clearBtn.addActionListener(e -> {
            app.service.cart.clear();
            updateCartBadge();
            dlg.dispose();
        });

        checkoutBtn.addActionListener(e -> {
            if (app.service.cart.isEmpty()) {
                JOptionPane.showMessageDialog(dlg,
                    "Your cart is empty.", "Empty Cart", JOptionPane.WARNING_MESSAGE);
                return;
            }
            doCheckout(dlg, finalTotal);
        });

        btnRow.add(clearBtn);
        btnRow.add(checkoutBtn);

        footer.add(totalLbl, BorderLayout.WEST);
        footer.add(btnRow,   BorderLayout.EAST);

        dlg.add(dlgHeader,  BorderLayout.NORTH);
        dlg.add(tableArea,  BorderLayout.CENTER);
        dlg.add(footer,     BorderLayout.SOUTH);
        dlg.setVisible(true);
    }

    // ── Checkout Dialog ───────────────────────────────────────────────────────
    private void doCheckout(JDialog cartDlg, double total) {
        JDialog payDlg = new JDialog(cartDlg, "Secure Checkout", true);
        payDlg.setSize(420, 280);
        payDlg.setLocationRelativeTo(cartDlg);
        payDlg.setLayout(new BorderLayout());
        payDlg.getContentPane().setBackground(UITheme.C_BG);

        JPanel payHeader = new JPanel(new BorderLayout());
        payHeader.setBackground(UITheme.C_SUCCESS);
        payHeader.setBorder(new EmptyBorder(14, 20, 14, 20));
        JLabel payTitle = new JLabel("\uD83D\uDD12  Secure Payment");
        payTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        payTitle.setForeground(Color.WHITE);
        payHeader.add(payTitle);

        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBackground(UITheme.C_BG);
        form.setBorder(new EmptyBorder(24, 28, 24, 28));

        JLabel amtLbl = UITheme.h3("Amount Due:  ₹" + String.format("%.2f", total));
        amtLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel cardLbl = UITheme.fieldLabel("16-Digit Card Number");
        cardLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextField cardField = UITheme.textField();
        cardField.setAlignmentX(Component.LEFT_ALIGNMENT);
        cardField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));

        form.add(amtLbl);
        form.add(Box.createVerticalStrut(20));
        form.add(cardLbl);
        form.add(Box.createVerticalStrut(6));
        form.add(cardField);

        JPanel payFooter = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        payFooter.setBackground(UITheme.C_SURFACE);
        payFooter.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, UITheme.C_BORDER));

        UITheme.RBtn cancelBtn = UITheme.outlineBtn("Cancel");
        UITheme.RBtn payBtn    = UITheme.successBtn("Pay Now");

        cancelBtn.addActionListener(e -> payDlg.dispose());
        payBtn.addActionListener(e -> {
            String card = cardField.getText().trim();
            if (app.service.checkout(card)) {
                payDlg.dispose();
                cartDlg.dispose();
                refresh();
                JOptionPane.showMessageDialog(this,
                    "Payment successful! Thank you for shopping with SwiftCart.",
                    "Order Confirmed \u2713", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(payDlg,
                    "Invalid card number. Please enter a 16-digit number.",
                    "Payment Failed", JOptionPane.ERROR_MESSAGE);
            }
        });

        payFooter.add(cancelBtn);
        payFooter.add(payBtn);

        payDlg.add(payHeader,  BorderLayout.NORTH);
        payDlg.add(form,       BorderLayout.CENTER);
        payDlg.add(payFooter,  BorderLayout.SOUTH);
        payDlg.setVisible(true);
    }
}
