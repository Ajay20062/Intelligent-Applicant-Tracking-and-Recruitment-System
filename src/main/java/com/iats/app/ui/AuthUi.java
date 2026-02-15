package com.iats.app.ui;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

public final class AuthUi {
    public static final Color BG = new Color(245, 247, 251);
    public static final Color CARD = Color.WHITE;
    public static final Color PRIMARY = new Color(30, 99, 184);
    public static final Color TEXT_SUBTLE = new Color(86, 100, 120);
    public static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 28);
    public static final Font BODY_FONT = new Font("Segoe UI", Font.PLAIN, 13);

    private AuthUi() {
    }

    public static JPanel createCard() {
        JPanel card = new JPanel();
        card.setBackground(CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(221, 227, 238)),
                BorderFactory.createEmptyBorder(18, 18, 18, 18)
        ));
        return card;
    }

    public static void styleField(JTextField field) {
        field.setPreferredSize(new Dimension(260, 32));
        field.setFont(BODY_FONT);
    }

    public static JButton primaryButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(PRIMARY);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        return button;
    }

    public static JButton secondaryButton(String text) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        return button;
    }

    public static void setMarginTop(JComponent component, int top) {
        component.setBorder(BorderFactory.createEmptyBorder(top, 0, 0, 0));
    }
}
