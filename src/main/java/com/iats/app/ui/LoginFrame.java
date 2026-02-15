package com.iats.app.ui;

import com.iats.app.model.User;
import com.iats.app.service.AuthService;

import javax.swing.JCheckBox;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.sql.SQLException;
import java.util.Optional;

public class LoginFrame extends JFrame {
    private final AuthService authService;
    private final JTextField loginIdField;
    private final JPasswordField loginPasswordField;

    public LoginFrame() {
        super("IATS Login");
        this.authService = new AuthService();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(new Dimension(560, 460));
        setResizable(false);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(AuthUi.BG);

        loginIdField = new JTextField();
        loginPasswordField = new JPasswordField();
        AuthUi.styleField(loginIdField);
        AuthUi.styleField(loginPasswordField);

        add(buildPage(), BorderLayout.CENTER);
    }

    private JPanel buildPage() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(AuthUi.BG);

        JPanel card = AuthUi.createCard();
        card.setLayout(new GridBagLayout());

        JLabel title = new JLabel("Login", SwingConstants.CENTER);
        title.setFont(AuthUi.TITLE_FONT);

        JLabel subtitle = new JLabel("Sign in to continue", SwingConstants.CENTER);
        subtitle.setForeground(AuthUi.TEXT_SUBTLE);
        subtitle.setFont(AuthUi.BODY_FONT);

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.insets = new Insets(6, 8, 6, 8);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;

        card.add(title, c);
        c.gridy++;
        card.add(subtitle, c);
        c.gridy++;
        c.insets = new Insets(20, 8, 6, 8);
        card.add(new JLabel("Email or Username"), c);
        c.gridy++;
        c.insets = new Insets(0, 8, 8, 8);
        card.add(loginIdField, c);
        c.gridy++;
        c.insets = new Insets(6, 8, 6, 8);
        card.add(new JLabel("Password"), c);
        c.gridy++;
        c.insets = new Insets(0, 8, 4, 8);
        card.add(loginPasswordField, c);

        JCheckBox showPassword = new JCheckBox("Show password");
        showPassword.setOpaque(false);
        showPassword.addActionListener(e -> loginPasswordField.setEchoChar(showPassword.isSelected() ? (char) 0 : '\u2022'));
        c.gridy++;
        c.insets = new Insets(0, 8, 10, 8);
        card.add(showPassword, c);

        JButton loginButton = AuthUi.primaryButton("Login");
        loginButton.addActionListener(e -> onLogin());
        getRootPane().setDefaultButton(loginButton);
        c.gridy++;
        c.insets = new Insets(6, 8, 6, 8);
        card.add(loginButton, c);

        JButton goRegisterButton = AuthUi.secondaryButton("Go To Register");
        goRegisterButton.addActionListener(e -> {
            RegisterFrame registerFrame = new RegisterFrame();
            registerFrame.setVisible(true);
            dispose();
        });
        c.gridy++;
        card.add(goRegisterButton, c);

        JButton homeButton = AuthUi.secondaryButton("Back To Main");
        homeButton.addActionListener(e -> {
            WelcomeFrame welcomeFrame = new WelcomeFrame();
            welcomeFrame.setVisible(true);
            dispose();
        });
        c.gridy++;
        card.add(homeButton, c);

        panel.add(card);
        return panel;
    }

    private void onLogin() {
        try {
            Optional<User> userOptional = authService.login(
                    loginIdField.getText(),
                    new String(loginPasswordField.getPassword())
            );

            if (userOptional.isEmpty()) {
                showValidation("Invalid credentials.");
                return;
            }

            User user = userOptional.get();
            MainFrame mainFrame = new MainFrame(user.getFullName(), user.getRole());
            mainFrame.setVisible(true);
            dispose();
        } catch (IllegalArgumentException e) {
            showValidation(e.getMessage());
        } catch (SQLException e) {
            showDbError(e);
        }
    }

    private void showValidation(String message) {
        JOptionPane.showMessageDialog(this, message, "Validation Error", JOptionPane.WARNING_MESSAGE);
    }

    private void showDbError(SQLException ex) {
        JOptionPane.showMessageDialog(this, ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
    }
}
