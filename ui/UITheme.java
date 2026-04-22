package com.shop.ui;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;

public class UITheme {

    // ── Color Palette ─────────────────────────────────────────────────────────
    public static final Color C_BG       = new Color(0xF1F5F9);
    public static final Color C_SURFACE  = Color.WHITE;
    public static final Color C_HEADER   = new Color(0x1E3A8A);
    public static final Color C_HEADER2  = new Color(0x0F172A);
    public static final Color C_PRIMARY  = new Color(0x2563EB);
    public static final Color C_PRIM_H   = new Color(0x1D4ED8);
    public static final Color C_SUCCESS  = new Color(0x16A34A);
    public static final Color C_SUCC_H   = new Color(0x15803D);
    public static final Color C_DANGER   = new Color(0xDC2626);
    public static final Color C_DANG_H   = new Color(0xB91C1C);
    public static final Color C_WARN     = new Color(0xD97706);
    public static final Color C_TXT_D    = new Color(0x0F172A);
    public static final Color C_TXT_M    = new Color(0x475569);
    public static final Color C_TXT_L    = new Color(0x94A3B8);
    public static final Color C_BORDER   = new Color(0xE2E8F0);
    public static final Color C_ROW_ALT  = new Color(0xF8FAFC);
    public static final Color C_ROW_SEL  = new Color(0xDBEAFE);

    // ── Fonts ─────────────────────────────────────────────────────────────────
    private static final String FF = "Segoe UI";
    public static final Font F_H1    = new Font(FF, Font.BOLD, 26);
    public static final Font F_H2    = new Font(FF, Font.BOLD, 18);
    public static final Font F_H3    = new Font(FF, Font.BOLD, 14);
    public static final Font F_BODY  = new Font(FF, Font.PLAIN, 13);
    public static final Font F_SMALL = new Font(FF, Font.PLAIN, 11);
    public static final Font F_BTN   = new Font(FF, Font.BOLD, 13);
    public static final Font F_LABEL = new Font(FF, Font.BOLD, 12);

    // ── Rounded Button ────────────────────────────────────────────────────────
    public static class RBtn extends JButton {
        private final Color bg, hov;
        private boolean over;

        public RBtn(String text, Color bg, Color hov, Color fg) {
            super(text);
            this.bg = bg;
            this.hov = hov;
            setFont(F_BTN);
            setForeground(fg);
            setOpaque(false);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setBorder(new EmptyBorder(10, 24, 10, 24));
            addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) { over = true;  repaint(); }
                public void mouseExited (MouseEvent e) { over = false; repaint(); }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(over ? hov : bg);
            g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));
            g2.dispose();
            super.paintComponent(g);
        }
    }

    // ── Button Factories ──────────────────────────────────────────────────────
    public static RBtn primaryBtn(String t) {
        return new RBtn(t, C_PRIMARY, C_PRIM_H, Color.WHITE);
    }

    public static RBtn dangerBtn(String t) {
        return new RBtn(t, C_DANGER, C_DANG_H, Color.WHITE);
    }

    public static RBtn successBtn(String t) {
        return new RBtn(t, C_SUCCESS, C_SUCC_H, Color.WHITE);
    }

    public static RBtn outlineBtn(String t) {
        return new RBtn(t, C_SURFACE, C_BG, C_TXT_M) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? C_BG : C_SURFACE);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));
                g2.setColor(C_BORDER);
                g2.draw(new RoundRectangle2D.Float(0.5f, 0.5f, getWidth() - 1, getHeight() - 1, 10, 10));
                g2.dispose();
                super.paintComponent(g);
            }
        };
    }

    // ── Text Inputs ───────────────────────────────────────────────────────────
    public static JTextField textField() {
        JTextField f = new JTextField();
        applyInputStyle(f);
        return f;
    }

    public static JPasswordField passField() {
        JPasswordField f = new JPasswordField();
        applyInputStyle(f);
        return f;
    }

    private static void applyInputStyle(JComponent c) {
        c.setFont(F_BODY);
        c.setBackground(C_SURFACE);
        c.setForeground(C_TXT_D);
        c.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(C_BORDER, 1, true),
            new EmptyBorder(8, 12, 8, 12)
        ));
        c.setPreferredSize(new Dimension(160, 38));
    }

    // ── Table Styling ─────────────────────────────────────────────────────────
    public static void styleTable(JTable t) {
        t.setFont(F_BODY);
        t.setRowHeight(40);
        t.setShowGrid(false);
        t.setIntercellSpacing(new Dimension(0, 0));
        t.setFillsViewportHeight(true);
        t.setSelectionBackground(C_ROW_SEL);
        t.setSelectionForeground(C_TXT_D);
        t.setBackground(C_SURFACE);

        JTableHeader h = t.getTableHeader();
        h.setFont(F_LABEL);
        h.setForeground(C_TXT_M);
        h.setBackground(C_BG);
        h.setPreferredSize(new Dimension(0, 44));
        h.setReorderingAllowed(false);
        h.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, C_BORDER));

        t.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable tbl, Object val, boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(tbl, val, sel, foc, row, col);
                if (!sel) {
                    setBackground(row % 2 == 0 ? C_SURFACE : C_ROW_ALT);
                    setForeground(C_TXT_D);
                }
                setBorder(new EmptyBorder(0, 14, 0, 14));
                setFont(F_BODY);
                return this;
            }
        });
    }

    public static JScrollPane scrollPane(Component c) {
        JScrollPane sp = new JScrollPane(c);
        sp.setBorder(new LineBorder(C_BORDER, 1));
        sp.setBackground(C_SURFACE);
        sp.getViewport().setBackground(C_SURFACE);
        sp.getVerticalScrollBar().setUnitIncrement(16);
        return sp;
    }

    // ── Labels ────────────────────────────────────────────────────────────────
    public static JLabel h1(String s)    { return label(s, F_H1,    C_TXT_D); }
    public static JLabel h2(String s)    { return label(s, F_H2,    C_TXT_D); }
    public static JLabel h3(String s)    { return label(s, F_H3,    C_TXT_D); }
    public static JLabel body(String s)  { return label(s, F_BODY,  C_TXT_M); }
    public static JLabel small(String s) { return label(s, F_SMALL, C_TXT_L); }
    public static JLabel fieldLabel(String s) { return label(s, F_LABEL, C_TXT_D); }

    private static JLabel label(String s, Font f, Color c) {
        JLabel l = new JLabel(s);
        l.setFont(f);
        l.setForeground(c);
        return l;
    }

    // ── Header Bar ────────────────────────────────────────────────────────────
    public static JPanel headerBar() {
        JPanel bar = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, C_HEADER, getWidth(), 0, C_HEADER2);
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        bar.setOpaque(false);
        bar.setBorder(new EmptyBorder(14, 24, 14, 24));
        bar.setPreferredSize(new Dimension(0, 64));
        return bar;
    }

    public static JLabel headerTitle(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font(FF, Font.BOLD, 18));
        l.setForeground(Color.WHITE);
        return l;
    }
}
