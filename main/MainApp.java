package com.shop.main;

import javax.swing.*;
import java.awt.*;
import com.shop.ui.*;
import com.shop.service.StoreService;

public class MainApp extends JFrame {

    public final CardLayout   layout    = new CardLayout();
    public final JPanel       container = new JPanel(layout);
    public final StoreService service   = new StoreService();

    private final BuyerPanel  buyerPanel;
    private final SellerPanel sellerPanel;

    public MainApp() {
        setTitle("SwiftCart — Online Shopping");
        setSize(1060, 700);
        setMinimumSize(new Dimension(860, 580));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        container.setBackground(UITheme.C_BG);

        buyerPanel  = new BuyerPanel(this);
        sellerPanel = new SellerPanel(this);

        container.add(new LoginPanel(this),     "Login");
        container.add(new SelectionPanel(this), "Select");
        container.add(sellerPanel,              "Seller");
        container.add(buyerPanel,               "Buyer");

        add(container);
        setVisible(true);
    }

    public void showPanel(String name) {
        if ("Buyer" .equals(name)) buyerPanel .refresh();
        if ("Seller".equals(name)) sellerPanel.refresh();
        layout.show(container, name);
    }

    public void logout() {
        service.cart.clear();
        showPanel("Login");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainApp::new);
    }
}
