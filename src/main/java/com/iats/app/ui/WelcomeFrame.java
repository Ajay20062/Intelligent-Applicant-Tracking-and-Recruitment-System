package com.iats.app.ui;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

public class WelcomeFrame extends JFrame {
    public WelcomeFrame() {
        super("IATS");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(new Dimension(560, 400));
        setResizable(false);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(AuthUi.BG);

        add(buildPage(), BorderLayout.CENTER);
    }

    private JPanel buildPage() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(AuthUi.BG);

        JLabel title = new JLabel("Intelligent Applicant Tracking System", SwingConstants.CENTER);
        title.setFont(AuthUi.TITLE_FONT);

        JLabel subtitle = new JLabel("Please choose one option", SwingConstants.CENTER);
        subtitle.setForeground(AuthUi.TEXT_SUBTLE);
        subtitle.setFont(AuthUi.BODY_FONT);

        JPanel card = AuthUi.createCard();
        card.setLayout(new GridBagLayout());

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.insets = new Insets(8, 8, 8, 8);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;

        card.add(title, c);
        c.gridy++;
        card.add(subtitle, c);

        JButton loginButton = AuthUi.primaryButton("Login");
        loginButton.addActionListener(e -> {
            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setVisible(true);
            dispose();
        });

        JButton registerButton = AuthUi.secondaryButton("Register");
        registerButton.addActionListener(e -> {
            RegisterFrame registerFrame = new RegisterFrame();
            registerFrame.setVisible(true);
            dispose();
        });

        c.gridy++;
        c.insets = new Insets(24, 8, 8, 8);
        card.add(loginButton, c);
        c.gridy++;
        c.insets = new Insets(10, 8, 8, 8);
        card.add(registerButton, c);

        panel.add(card);
        return panel;
    }
}
