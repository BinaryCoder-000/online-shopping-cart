package com.shop.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import com.shop.main.MainApp;
import com.shop.model.Product;

public class SellerPanel extends JPanel {

    private JTable            table;
    private DefaultTableModel model;
    private JTextField        idField, nameField, priceField, qtyField;
    private MainApp           app;

    public SellerPanel(MainApp app) {
        this.app = app;
        setLayout(new BorderLayout());
        setBackground(UITheme.C_BG);

        // ── Header ─────────────────────────────────────────────────────────────
        JPanel header = UITheme.headerBar();
        header.add(UITheme.headerTitle("Inventory Manager"), BorderLayout.WEST);

        JPanel headerRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        headerRight.setOpaque(false);

        UITheme.RBtn menuBtn   = UITheme.outlineBtn("\u2190 Back to Menu");
        UITheme.RBtn logoutBtn = UITheme.outlineBtn("Logout");
        for (UITheme.RBtn b : new UITheme.RBtn[]{menuBtn, logoutBtn}) {
            b.setForeground(Color.BLUE);
            b.setBorder(BorderFactory.createCompoundBorder(
                new javax.swing.border.LineBorder(new Color(0x60A5FA), 1, true),
                new EmptyBorder(7, 16, 7, 16)
            ));
        }
        menuBtn.addActionListener(e   -> app.showPanel("Select"));
        logoutBtn.addActionListener(e -> app.logout());

        headerRight.add(menuBtn);
        headerRight.add(logoutBtn);
        header.add(headerRight, BorderLayout.EAST);

        // ── Add / Update Form ─────────────────────────────────────────────────
        JPanel formCard = new JPanel(new BorderLayout(0, 12));
        formCard.setBackground(UITheme.C_SURFACE);
        formCard.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, UITheme.C_BORDER),
            new EmptyBorder(18, 22, 18, 22)
        ));

        JLabel formTitle = UITheme.h3("Add / Update Product");
        formTitle.setBorder(new EmptyBorder(0, 0, 8, 0));

        // Input grid: 4 fields in a row
        JPanel fields = new JPanel(new GridLayout(2, 4, 12, 6));
        fields.setOpaque(false);

        idField    = UITheme.textField();
        nameField  = UITheme.textField();
        priceField = UITheme.textField();
        qtyField   = UITheme.textField();

        fields.add(UITheme.fieldLabel("Product ID"));
        fields.add(UITheme.fieldLabel("Name"));
        fields.add(UITheme.fieldLabel("Unit Price (₹)"));
        fields.add(UITheme.fieldLabel("Quantity"));
        fields.add(idField);
        fields.add(nameField);
        fields.add(priceField);
        fields.add(qtyField);

        UITheme.RBtn saveBtn  = UITheme.primaryBtn("Save Product");
        UITheme.RBtn clearBtn = UITheme.outlineBtn("Clear Fields");

        JPanel formActions = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        formActions.setOpaque(false);
        formActions.add(saveBtn);
        formActions.add(clearBtn);

        formCard.add(formTitle,    BorderLayout.NORTH);
        formCard.add(fields,       BorderLayout.CENTER);
        formCard.add(formActions,  BorderLayout.SOUTH);

        // ── Table ──────────────────────────────────────────────────────────────
        String[] cols = {"Product ID", "Name", "Unit Price (₹)", "In Stock"};
        model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(model);
        UITheme.styleTable(table);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getColumnModel().getColumn(0).setPreferredWidth(100);
        table.getColumnModel().getColumn(1).setPreferredWidth(280);
        table.getColumnModel().getColumn(2).setPreferredWidth(130);
        table.getColumnModel().getColumn(3).setPreferredWidth(100);

        // Click row → populate form
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = table.getSelectedRow();
                if (row >= 0) {
                    idField.setText((String) model.getValueAt(row, 0));
                    nameField.setText((String) model.getValueAt(row, 1));
                    // Strip formatting before putting back
                    priceField.setText(model.getValueAt(row, 2).toString());
                    qtyField.setText(model.getValueAt(row, 3).toString());
                }
            }
        });

        JPanel tableArea = new JPanel(new BorderLayout());
        tableArea.setBackground(UITheme.C_BG);
        tableArea.setBorder(new EmptyBorder(12, 20, 12, 20));
        tableArea.add(UITheme.scrollPane(table), BorderLayout.CENTER);

        // ── Bottom action bar ──────────────────────────────────────────────────
        JPanel bottomBar = new JPanel(new BorderLayout());
        bottomBar.setBackground(UITheme.C_SURFACE);
        bottomBar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, UITheme.C_BORDER),
            new EmptyBorder(12, 20, 12, 20)
        ));

        JLabel hint = UITheme.small("Select a row to auto-fill the form for editing, then click Save.");
        UITheme.RBtn deleteBtn = UITheme.dangerBtn("\uD83D\uDDD1 Delete Selected");

        bottomBar.add(hint,      BorderLayout.WEST);
        bottomBar.add(deleteBtn, BorderLayout.EAST);

        // ── Actions ────────────────────────────────────────────────────────────
        saveBtn.addActionListener(e -> {
            try {
                String id    = idField.getText().trim();
                String name  = nameField.getText().trim();
                double price = Double.parseDouble(priceField.getText().trim());
                int qty      = Integer.parseInt(qtyField.getText().trim());

                if (id.isEmpty() || name.isEmpty()) {
                    JOptionPane.showMessageDialog(this,
                        "ID and Name cannot be empty.", "Validation", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                if (price <= 0 || qty < 0) {
                    JOptionPane.showMessageDialog(this,
                        "Price must be > 0 and Quantity must be >= 0.", "Validation", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                app.service.addProduct(new Product(id, name, price, qty));
                refresh();
                clearForm();
                JOptionPane.showMessageDialog(this,
                    "Product saved successfully!", "Saved \u2713", JOptionPane.INFORMATION_MESSAGE);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this,
                    "Price and Quantity must be valid numbers.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
            }
        });

        clearBtn.addActionListener(e -> clearForm());

        deleteBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this,
                    "Please select a product to delete.", "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String pid  = (String) model.getValueAt(row, 0);
            String name = (String) model.getValueAt(row, 1);
            int confirm = JOptionPane.showConfirmDialog(this,
                "Delete \"" + name + "\"? This cannot be undone.",
                "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (confirm == JOptionPane.YES_OPTION) {
                app.service.deleteProduct(pid);
                refresh();
                clearForm();
            }
        });

        // ── Layout assembly ────────────────────────────────────────────────────
        JPanel body = new JPanel(new BorderLayout());
        body.setBackground(UITheme.C_BG);
        body.add(formCard,   BorderLayout.NORTH);
        body.add(tableArea,  BorderLayout.CENTER);
        body.add(bottomBar,  BorderLayout.SOUTH);

        add(header, BorderLayout.NORTH);
        add(body,   BorderLayout.CENTER);
    }

    public void refresh() {
        model.setRowCount(0);
        for (Product p : app.service.inventory) {
            model.addRow(new Object[]{
                p.getId(), p.getName(),
                String.format("%.2f", p.getPrice()),
                p.getQuantity()
            });
        }
    }

    private void clearForm() {
        idField.setText("");
        nameField.setText("");
        priceField.setText("");
        qtyField.setText("");
        table.clearSelection();
    }
}
