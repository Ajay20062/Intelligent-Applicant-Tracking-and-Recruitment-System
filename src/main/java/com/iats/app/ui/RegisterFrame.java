package com.iats.app.ui;

import com.iats.app.service.AuthService;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
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

public class RegisterFrame extends JFrame {
    private final AuthService authService;

    private final JTextField fullNameField;
    private final JTextField emailField;
    private final JTextField usernameField;
    private final JComboBox<String> roleBox;
    private final JPasswordField passwordField;
    private final JPasswordField confirmPasswordField;

    public RegisterFrame() {
        super("IATS Register");
        this.authService = new AuthService();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(new Dimension(600, 560));
        setResizable(false);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(AuthUi.BG);

        fullNameField = new JTextField();
        emailField = new JTextField();
        usernameField = new JTextField();
        roleBox = new JComboBox<>(new String[]{"Recruiter", "Admin"});
        passwordField = new JPasswordField();
        confirmPasswordField = new JPasswordField();
        AuthUi.styleField(fullNameField);
        AuthUi.styleField(emailField);
        AuthUi.styleField(usernameField);
        roleBox.setFont(AuthUi.BODY_FONT);
        AuthUi.styleField(passwordField);
        AuthUi.styleField(confirmPasswordField);

        add(buildPage(), BorderLayout.CENTER);
    }

    private JPanel buildPage() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(AuthUi.BG);

        JPanel card = AuthUi.createCard();
        card.setLayout(new GridBagLayout());

        JLabel title = new JLabel("Register", SwingConstants.CENTER);
        title.setFont(AuthUi.TITLE_FONT);

        JLabel subtitle = new JLabel("Create a new Admin or Recruiter account", SwingConstants.CENTER);
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
        c.insets = new Insets(18, 8, 6, 8);
        card.add(new JLabel("Full Name"), c);
        c.gridy++;
        c.insets = new Insets(0, 8, 8, 8);
        card.add(fullNameField, c);
        c.gridy++;
        c.insets = new Insets(6, 8, 6, 8);
        card.add(new JLabel("Email"), c);
        c.gridy++;
        c.insets = new Insets(0, 8, 8, 8);
        card.add(emailField, c);
        c.gridy++;
        c.insets = new Insets(6, 8, 6, 8);
        card.add(new JLabel("Username"), c);
        c.gridy++;
        c.insets = new Insets(0, 8, 8, 8);
        card.add(usernameField, c);
        c.gridy++;
        c.insets = new Insets(6, 8, 6, 8);
        card.add(new JLabel("Role"), c);
        c.gridy++;
        c.insets = new Insets(0, 8, 8, 8);
        card.add(roleBox, c);
        c.gridy++;
        c.insets = new Insets(6, 8, 6, 8);
        card.add(new JLabel("Password"), c);
        c.gridy++;
        c.insets = new Insets(0, 8, 8, 8);
        card.add(passwordField, c);
        c.gridy++;
        c.insets = new Insets(6, 8, 6, 8);
        card.add(new JLabel("Confirm Password"), c);
        c.gridy++;
        c.insets = new Insets(0, 8, 8, 8);
        card.add(confirmPasswordField, c);

        JCheckBox showPassword = new JCheckBox("Show passwords");
        showPassword.setOpaque(false);
        showPassword.addActionListener(e -> {
            char echo = showPassword.isSelected() ? (char) 0 : '\u2022';
            passwordField.setEchoChar(echo);
            confirmPasswordField.setEchoChar(echo);
        });
        c.gridy++;
        c.insets = new Insets(0, 8, 10, 8);
        card.add(showPassword, c);

        JButton registerButton = AuthUi.primaryButton("Register");
        registerButton.addActionListener(e -> onRegister());
        getRootPane().setDefaultButton(registerButton);
        c.gridy++;
        c.insets = new Insets(6, 8, 6, 8);
        card.add(registerButton, c);

        JButton goLoginButton = AuthUi.secondaryButton("Back To Login");
        goLoginButton.addActionListener(e -> {
            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setVisible(true);
            dispose();
        });
        c.gridy++;
        card.add(goLoginButton, c);

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

    private void onRegister() {
        try {
            authService.register(
                    fullNameField.getText(),
                    emailField.getText(),
                    usernameField.getText(),
                    String.valueOf(roleBox.getSelectedItem()),
                    new String(passwordField.getPassword()),
                    new String(confirmPasswordField.getPassword())
            );

            JOptionPane.showMessageDialog(this, "Registration successful. Please login.");
            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setVisible(true);
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
