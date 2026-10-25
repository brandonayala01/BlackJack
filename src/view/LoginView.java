package view;

import javax.swing.*;
import java.awt.*;

public class LoginView extends JFrame {
    private final JTextField usernameField = new JTextField();
    private final JTextField emailField = new JTextField();
    private final JPasswordField passwordField = new JPasswordField();
    private JButton btnStart;

    public LoginView() {
        setTitle("Blackjack 21");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(620, 560);
        setLocationRelativeTo(null);
        setResizable(false);
        build();
    }

    private void build() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Theme.PANEL);

        JPanel hero = new JPanel();
        hero.setBackground(Theme.TABLE_DARK);
        hero.setLayout(new BoxLayout(hero, BoxLayout.Y_AXIS));
        hero.setBorder(BorderFactory.createEmptyBorder(40, 45, 35, 45));

        JLabel logo = new JLabel("♠  BLACKJACK 21  ♥");
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);
        logo.setForeground(Theme.GOLD);
        logo.setFont(new Font("Serif", Font.BOLD, 34));

        JLabel subtitle = new JLabel("Casino table • Dealer stands on 17 • Blackjack pays 3:2");
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        subtitle.setForeground(Theme.CREAM);
        subtitle.setFont(Theme.NORMAL);

        hero.add(logo);
        hero.add(Box.createVerticalStrut(12));
        hero.add(subtitle);
        hero.add(Box.createVerticalStrut(30));

        JPanel form = new JPanel(new GridLayout(6, 1, 0, 6));
        form.setOpaque(false);
        form.add(prompt("Username"));
        form.add(usernameField);
        form.add(prompt("Email"));
        form.add(emailField);
        form.add(prompt("Password"));
        form.add(passwordField);
        hero.add(form);

        btnStart = styledButton("ENTER THE TABLE");
        hero.add(Box.createVerticalStrut(20));
        hero.add(btnStart);

        root.add(hero, BorderLayout.CENTER);
        setContentPane(root);
        getRootPane().setDefaultButton(btnStart);
    }

    private JLabel prompt(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(Theme.TEXT);
        label.setFont(Theme.BUTTON);
        return label;
    }

    private JButton styledButton(String text) {
        JButton b = new JButton(text);
        b.setAlignmentX(Component.CENTER_ALIGNMENT);
        b.setFont(Theme.BUTTON);
        b.setForeground(Theme.GOLD);
        b.setBackground(Theme.TABLE);
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.GOLD),
                BorderFactory.createEmptyBorder(9, 20, 9, 20)));
        return b;
    }

    public JButton getBtnStart() {
        return btnStart;
    }

    public String getUsername() {
        return usernameField.getText().trim();
    }

    public String getEmail() {
        return emailField.getText().trim();
    }

    public String getPassword() {
        return new String(passwordField.getPassword());
    }

    public void showMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Missing data", JOptionPane.WARNING_MESSAGE);
    }
}
