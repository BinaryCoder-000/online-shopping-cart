package ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import main.MainApp;

public class SelectionPanel extends JPanel {

    public SelectionPanel(MainApp app) {
        setLayout(new BorderLayout());
        setBackground(UITheme.C_BG);

        // ── Header ────────────────────────────────────────────────────────────
        JPanel header = UITheme.headerBar();
        header.add(UITheme.headerTitle("SwiftCart"), BorderLayout.WEST);

        UITheme.RBtn logoutBtn = UITheme.outlineBtn("Logout");
        logoutBtn.setForeground(Color.BLUE);
        logoutBtn.setBorder(BorderFactory.createCompoundBorder(
            new javax.swing.border.LineBorder(new Color(0x60A5FA), 1, true),
            new EmptyBorder(7, 18, 7, 18)
        ));
        logoutBtn.addActionListener(e -> app.logout());
        header.add(logoutBtn, BorderLayout.EAST);

        // ── Center ────────────────────────────────────────────────────────────
        JPanel center = new JPanel(new GridBagLayout());
        center.setBackground(UITheme.C_BG);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);

        JLabel heading = UITheme.h1("Choose Your Role");
        heading.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel sub = UITheme.body("How would you like to continue today?");
        sub.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel cards = new JPanel(new GridLayout(1, 2, 24, 0));
        cards.setOpaque(false);
        cards.setAlignmentX(Component.CENTER_ALIGNMENT);
        cards.setMaximumSize(new Dimension(680, 300));

        cards.add(buildRoleCard(
            "\uD83D\uDECD\uFE0F", "Buyer",
            "Browse products, add to cart,\nand check out securely.",
            UITheme.C_PRIMARY, UITheme.C_PRIM_H,
            e -> app.showPanel("Buyer")
        ));
        cards.add(buildRoleCard(
            "\uD83D\uDCE6", "Seller",
            "Add, update, and manage\nyour product inventory.",
            UITheme.C_SUCCESS, UITheme.C_SUCC_H,
            e -> app.showPanel("Seller")
        ));

        content.add(heading);
        content.add(Box.createVerticalStrut(8));
        content.add(sub);
        content.add(Box.createVerticalStrut(40));
        content.add(cards);

        center.add(content);

        add(header, BorderLayout.NORTH);
        add(center, BorderLayout.CENTER);
    }

    private JPanel buildRoleCard(String emoji, String role, String desc,
                                  Color btnColor, Color btnHover,
                                  java.awt.event.ActionListener action) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(UITheme.C_SURFACE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.setColor(UITheme.C_BORDER);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);
                g2.dispose();
            }
        };
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(36, 32, 36, 32));

        JLabel emojiLbl = new JLabel(emoji);
        emojiLbl.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48));
        emojiLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel roleLbl = UITheme.h2(role);
        roleLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel descLbl = new JLabel("<html><center>" + desc.replace("\n","<br>") + "</center></html>");
        descLbl.setFont(UITheme.F_BODY);
        descLbl.setForeground(UITheme.C_TXT_M);
        descLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        UITheme.RBtn btn = new UITheme.RBtn("Enter as " + role, btnColor, btnHover, Color.WHITE);
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.addActionListener(action);

        card.add(emojiLbl);
        card.add(Box.createVerticalStrut(16));
        card.add(roleLbl);
        card.add(Box.createVerticalStrut(10));
        card.add(descLbl);
        card.add(Box.createVerticalStrut(28));
        card.add(btn);

        return card;
    }
}
