
package view;

import model.Card;
import model.GameRecord;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class GameView extends JFrame {

    private final JPanel dealerCards =
            new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 5));

    private final JPanel playerCards =
            new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 5));

    private final JLabel dealerValue = new JLabel();
    private final JLabel status = new JLabel("Place a bet to start.");
    private final JLabel playerLabel = new JLabel();
    private final JLabel balance = new JLabel();
    private final JLabel betLabel = new JLabel();

    private final JButton hit = action("HIT");
    private final JButton stand = action("STAND");
    private final JButton doubleButton = action("DOUBLE");
    private final JButton splitButton = action("SPLIT");
    private final JButton deal = action("NEW HAND");
    private final JButton clear = action("CLEAR");
    private final JButton historyButton = action("HISTORY");

    private final ArrayList<JButton> chips = new ArrayList<>();

    public GameView() {
        setTitle("Blackjack 21");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1200, 820);
        setMinimumSize(new Dimension(1050, 720));
        setLocationRelativeTo(null);

        UIManager.put("Button.foreground", Color.BLACK);
        UIManager.put("Button.disabledText", Color.BLACK);

        build();
    }

    private void build() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Theme.PANEL);

        JPanel header = new JPanel(new BorderLayout(20, 0));
        header.setBackground(Theme.TABLE_DARK);
        header.setBorder(new EmptyBorder(12, 22, 12, 22));

        JLabel title = new JLabel("BLACKJACK 21");
        title.setForeground(Theme.GOLD);
        title.setFont(new Font("Serif", Font.BOLD, 28));
        header.add(title, BorderLayout.WEST);

        JPanel info = new JPanel(new GridLayout(1, 3, 20, 0));
        info.setOpaque(false);

        styleInfo(playerLabel);
        styleInfo(balance);
        styleInfo(betLabel);

        info.add(playerLabel);
        info.add(balance);
        info.add(betLabel);

        header.add(info, BorderLayout.CENTER);

        historyButton.setPreferredSize(new Dimension(130, 38));
        header.add(historyButton, BorderLayout.EAST);

        root.add(header, BorderLayout.NORTH);

        JPanel table = new JPanel(new GridLayout(2, 1, 0, 10));
        table.setBackground(Theme.TABLE);
        table.setBorder(new EmptyBorder(18, 18, 14, 18));

        table.add(handPanel("DEALER", dealerCards, dealerValue));
        table.add(handPanel("PLAYER", playerCards, new JLabel()));

        root.add(table, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setBackground(Theme.PANEL);

        JPanel controlPanel = new JPanel(new GridLayout(1, 11, 6, 0));
        controlPanel.setOpaque(false);
        controlPanel.setBorder(new EmptyBorder(12, 12, 5, 12));

        controlPanel.add(hit);
        controlPanel.add(stand);
        controlPanel.add(doubleButton);
        controlPanel.add(splitButton);

        for (int amount : new int[]{10, 25, 50, 100, 250}) {
            JButton chip = chip("$" + amount);
            chip.setActionCommand(String.valueOf(amount));
            chips.add(chip);
            controlPanel.add(chip);
        }

        controlPanel.add(deal);
        controlPanel.add(clear);

        bottom.add(controlPanel, BorderLayout.CENTER);

        status.setHorizontalAlignment(SwingConstants.CENTER);
        status.setForeground(Theme.TEXT);
        status.setFont(new Font("SansSerif", Font.BOLD, 15));
        status.setBorder(new EmptyBorder(8, 5, 14, 5));

        bottom.add(status, BorderLayout.SOUTH);
        root.add(bottom, BorderLayout.SOUTH);

        setContentPane(root);
    }

    private JPanel handPanel(String name, JPanel cards, JLabel value) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        JLabel title = new JLabel(name);
        title.setForeground(Theme.CREAM);
        title.setFont(new Font("SansSerif", Font.BOLD, 15));
        title.setHorizontalAlignment(SwingConstants.CENTER);

        cards.setOpaque(false);

        value.setForeground(Theme.CREAM);
        value.setFont(new Font("SansSerif", Font.BOLD, 15));
        value.setHorizontalAlignment(SwingConstants.CENTER);

        panel.add(title, BorderLayout.NORTH);
        panel.add(cards, BorderLayout.CENTER);
        panel.add(value, BorderLayout.SOUTH);

        return panel;
    }

    private void styleInfo(JLabel label) {
        label.setForeground(Theme.TEXT);
        label.setFont(new Font("SansSerif", Font.BOLD, 14));
        label.setHorizontalAlignment(SwingConstants.CENTER);
    }

    private JButton action(String text) {
        JButton button = new JButton(text);

        button.setFont(new Font("SansSerif", Font.BOLD, 12));
        button.setForeground(Color.BLACK);
        button.setBackground(Theme.GOLD);
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setFocusPainted(false);

        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.GOLD_DARK),
                BorderFactory.createEmptyBorder(7, 4, 7, 4)
        ));

        button.setPreferredSize(new Dimension(90, 42));
        button.putClientProperty("JButton.disabledText", Color.BLACK);

        return button;
    }

    private JButton chip(String text) {
        JButton button = action(text);
        button.setPreferredSize(new Dimension(65, 42));
        return button;
    }

    // ---------- Datos del jugador ----------

    public void setPlayerName(String name) {
        setTitle("Blackjack 21 • Player: " + name);
        playerLabel.setText("Player: " + name);
    }

    public void showHeader(double balanceValue, double betValue) {
        balance.setText("Balance: " + money(balanceValue));
        betLabel.setText("Total bet: " + money(betValue));
    }

    // ---------- Tablero ----------

    public void showBoard(
            List<Card> dealerList,
            List<List<Card>> hands,
            List<Integer> scores,
            List<Double> bets,
            List<Boolean> busts,
            int activeIndex,
            boolean hideDealerCard,
            String dealerText) {

        dealerCards.removeAll();
        playerCards.removeAll();

        for (int i = 0; i < dealerList.size(); i++) {
            dealerCards.add(new CardPanel(
                    dealerList.get(i),
                    hideDealerCard && i == 1
            ));
        }

        dealerValue.setText(dealerText);

        for (int i = 0; i < hands.size(); i++) {
            JPanel handPanel = new JPanel(new BorderLayout(0, 5));
            handPanel.setOpaque(true);
            handPanel.setBackground(
                    i == activeIndex && hideDealerCard
                            ? new Color(40, 85, 55)
                            : Theme.TABLE_DARK
            );

            handPanel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(
                            i == activeIndex && hideDealerCard
                                    ? Theme.GOLD
                                    : Theme.GOLD_DARK,
                            i == activeIndex && hideDealerCard ? 2 : 1
                    ),
                    new EmptyBorder(6, 5, 6, 5)
            ));

            String heading = "HAND " + (i + 1)
                    + " • Bet: " + money(bets.get(i));

            if (i == activeIndex && hideDealerCard) {
                heading += " • ACTIVE";
            }

            JLabel title = new JLabel(heading);
            title.setHorizontalAlignment(SwingConstants.CENTER);
            title.setForeground(Theme.CREAM);
            title.setFont(new Font("SansSerif", Font.BOLD, 11));

            JPanel cardsPanel =
                    new JPanel(new FlowLayout(FlowLayout.CENTER, 3, 3));
            cardsPanel.setOpaque(false);

            for (Card card : hands.get(i)) {
                cardsPanel.add(new CardPanel(card, false));
            }

            String scoreText = "Total: " + scores.get(i);

            if (busts.get(i)) {
                scoreText += " • BUST";
            }

            JLabel score = new JLabel(scoreText);
            score.setHorizontalAlignment(SwingConstants.CENTER);
            score.setForeground(busts.get(i) ? Theme.RED : Theme.CREAM);
            score.setFont(new Font("SansSerif", Font.BOLD, 12));

            handPanel.add(title, BorderLayout.NORTH);
            handPanel.add(cardsPanel, BorderLayout.CENTER);
            handPanel.add(score, BorderLayout.SOUTH);

            playerCards.add(handPanel);
        }

        dealerCards.revalidate();
        dealerCards.repaint();
        playerCards.revalidate();
        playerCards.repaint();
    }

    // ---------- Estado de los botones ----------

    public void showButtons(
            boolean playing,
            double bet,
            boolean canDouble,
            boolean canSplit) {

        hit.setEnabled(playing);
        stand.setEnabled(playing);
        doubleButton.setEnabled(canDouble);
        splitButton.setEnabled(canSplit);

        for (JButton chip : chips) {
            chip.setEnabled(!playing);
        }

        deal.setEnabled(!playing && bet >= 1);
        clear.setEnabled(!playing && bet > 0);
    }

    // ---------- Mensajes ----------

    public void showInfo(String message) {
        showStatus(message, Theme.TEXT);
    }

    public void showWin(String message) {
        showStatus(message, Theme.GREEN);
    }

    public void showLose(String message) {
        showStatus(message, Theme.RED);
    }

    public void showPush(String message) {
        showStatus(message, Theme.GOLD);
    }

    private void showStatus(String message, Color color) {
        status.setForeground(color);
        status.setText(message);
    }

    // ---------- Historial y saldo ----------

    public void showHistory(ArrayList<GameRecord> history) {
        new HistoryView(this, history).setVisible(true);
    }

    public boolean askRecharge() {
        int option = JOptionPane.showConfirmDialog(
                this,
                "You are out of balance. Do you want to restart with $1,000?",
                "No balance",
                JOptionPane.YES_NO_OPTION
        );

        return option == JOptionPane.YES_OPTION;
    }

    private String money(double amount) {
        return String.format("$%,.2f", amount);
    }

    // ---------- Getters para Presenter ----------

    public JButton getBtnHit() {
        return hit;
    }

    public JButton getBtnStand() {
        return stand;
    }

    public JButton getBtnDouble() {
        return doubleButton;
    }

    public JButton getBtnSplit() {
        return splitButton;
    }

    public JButton getBtnDeal() {
        return deal;
    }

    public JButton getBtnClear() {
        return clear;
    }

    public JButton getBtnHistory() {
        return historyButton;
    }

    public ArrayList<JButton> getChips() {
        return chips;
    }
}
