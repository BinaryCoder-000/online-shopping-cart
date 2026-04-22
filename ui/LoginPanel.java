package ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import main.MainApp;

public class LoginPanel extends JPanel {

    public LoginPanel(MainApp app) {
        setLayout(new BorderLayout());
        setBackground(UITheme.C_BG);

        // ── LEFT: Branded Sidebar ─────────────────────────────────────────────
        JPanel sidebar = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(
                    0, 0,            new Color(0x1E40AF),
                    0, getHeight(),  new Color(0x0F172A)
                );
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        sidebar.setPreferredSize(new Dimension(360, 0));
        sidebar.setOpaque(false);

        JPanel sideInner = new JPanel();
        sideInner.setLayout(new BoxLayout(sideInner, BoxLayout.Y_AXIS));
        sideInner.setOpaque(false);
        sideInner.setBorder(new EmptyBorder(0, 32, 0, 32));

        JLabel icon = new JLabel("\uD83D\uDED2");
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 52));
        icon.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel brand = new JLabel("SwiftCart");
        brand.setFont(new Font("Segoe UI", Font.BOLD, 34));
        brand.setForeground(Color.WHITE);
        brand.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel tagline = new JLabel("Shop smarter. Sell faster.");
        tagline.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tagline.setForeground(new Color(0x93C5FD));
        tagline.setAlignmentX(Component.CENTER_ALIGNMENT);

        sideInner.add(Box.createVerticalGlue());
        sideInner.add(icon);
        sideInner.add(Box.createVerticalStrut(10));
        sideInner.add(brand);
        sideInner.add(Box.createVerticalStrut(8));
        sideInner.add(tagline);
        sideInner.add(Box.createVerticalStrut(40));

        String[] features = {
            "Real-time inventory tracking",
            "Secure card checkout",
            "Buyer & Seller roles",
            "Instant product management"
        };
        for (String f : features) {
            JLabel fl = new JLabel(f);
            fl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            fl.setForeground(new Color(0xBAE6FD));
            fl.setAlignmentX(Component.CENTER_ALIGNMENT);
            sideInner.add(fl);
            sideInner.add(Box.createVerticalStrut(10));
        }
        sideInner.add(Box.createVerticalGlue());

        sidebar.add(sideInner);

        // ── RIGHT: Login Form ─────────────────────────────────────────────────
        JPanel formArea = new JPanel(new GridBagLayout());
        formArea.setBackground(UITheme.C_BG);

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(UITheme.C_SURFACE);
        card.setBorder(BorderFactory.createCompoundBorder(
            new javax.swing.border.LineBorder(UITheme.C_BORDER, 1, true),
            new EmptyBorder(44, 44, 44, 44)
        ));

        // ── Form elements ──────────────────────────────────────────────────────
        JLabel title    = UITheme.h1("Welcome back");
        JLabel subtitle = UITheme.body("Sign in or create a new account");
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel userLbl = UITheme.fieldLabel("Username");
        userLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        JTextField userField = UITheme.textField();
        userField.setAlignmentX(Component.LEFT_ALIGNMENT);
        userField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));

        // set password field to show a hint "Enter your password" when empty and not focused, and hide the hint when focused or has text
        // and also add a toggle button to show/hide password text

        JLabel passLbl = UITheme.fieldLabel("Password");
        passLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPasswordField passField = UITheme.passField();
        passField.setAlignmentX(Component.LEFT_ALIGNMENT);
        passField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));

        UITheme.RBtn loginBtn = UITheme.primaryBtn("Sign In");
        loginBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        loginBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));

        UITheme.RBtn regBtn = UITheme.outlineBtn("Create Account");
        regBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        regBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));

        // Divider
        JPanel divRow = new JPanel(new BorderLayout(8, 0));
        divRow.setOpaque(false);
        divRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 24));
        divRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        JSeparator sepL = new JSeparator(); sepL.setForeground(UITheme.C_BORDER);
        JSeparator sepR = new JSeparator(); sepR.setForeground(UITheme.C_BORDER);
        JLabel orLbl = UITheme.small("  or  ");
        divRow.add(sepL,  BorderLayout.WEST);
        divRow.add(orLbl, BorderLayout.CENTER);
        divRow.add(sepR,  BorderLayout.EAST);

        card.add(title);
        card.add(Box.createVerticalStrut(4));
        card.add(subtitle);
        card.add(Box.createVerticalStrut(32));
        card.add(userLbl);
        card.add(Box.createVerticalStrut(6));
        card.add(userField);
        card.add(Box.createVerticalStrut(18));
        card.add(passLbl);
        card.add(Box.createVerticalStrut(6));
        card.add(passField);
        card.add(Box.createVerticalStrut(28));
        card.add(loginBtn);
        card.add(Box.createVerticalStrut(12));
        card.add(divRow);
        card.add(Box.createVerticalStrut(12));
        card.add(regBtn);

        // Preferred card width
        card.setPreferredSize(new Dimension(380, 480));

        formArea.add(card);

        // ── Actions ───────────────────────────────────────────────────────────
        loginBtn.addActionListener(e -> {
            String u = userField.getText().trim();
            String p = new String(passField.getPassword());
            if (u.isEmpty() || p.isEmpty()) {
                showMsg("Please fill in all fields.", "Missing Fields", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (app.service.login(u, p)) {
                userField.setText("");
                passField.setText("");
                app.showPanel("Select");
            } else {
                showMsg("Invalid credentials. Register first.", "Login Failed", JOptionPane.ERROR_MESSAGE);
            }
        });

        regBtn.addActionListener(e -> {
            String u = userField.getText().trim();
            String p = new String(passField.getPassword());
            if (u.isEmpty()) {
                showMsg("Please enter a username.", "Missing Fields", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (p.length() < 3) {
                showMsg("Password must be at least 3 characters.", "Weak Password", JOptionPane.WARNING_MESSAGE);
                return;
            }
            app.service.register(u, p);
            showMsg("Account created! You can now sign in.", "Registered \u2713", JOptionPane.INFORMATION_MESSAGE);
        });

        // Enter key on password triggers login
        passField.addActionListener(e -> loginBtn.doClick());

        add(sidebar,  BorderLayout.WEST);
        add(formArea, BorderLayout.CENTER);
    }

    private void showMsg(String msg, String title, int type) {
        JOptionPane.showMessageDialog(this, msg, title, type);
    }
}
