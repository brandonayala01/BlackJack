package view;

import model.Card;
import javax.swing.*;
import java.awt.*;

public class CardPanel extends JPanel {
    private final Card card;
    private final boolean hidden;

    public CardPanel(Card card, boolean hidden) {
        this.card = card;
        this.hidden = hidden;
        setPreferredSize(new Dimension(82, 116));
        setMinimumSize(new Dimension(82, 116));
        setOpaque(false);
    }

    private String symbolOf(String suit) {
        if (suit.equals("Hearts")) return "♥";
        if (suit.equals("Diamonds")) return "♦";
        if (suit.equals("Clubs")) return "♣";
        return "♠";
    }

    private boolean isRed(String suit) {
        return suit.equals("Hearts") || suit.equals("Diamonds");
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth() - 9, h = getHeight() - 9;
        g2.setColor(new Color(0, 0, 0, 70));
        g2.fillRoundRect(7, 8, w, h, 14, 14);

        if (hidden) {
            g2.setColor(new Color(22, 43, 61));
            g2.fillRoundRect(2, 2, w, h, 14, 14);
            g2.setColor(Theme.GOLD);
            g2.setStroke(new BasicStroke(1.5f));
            g2.drawRoundRect(8, 8, w - 12, h - 12, 9, 9);
            g2.setFont(new Font("Serif", Font.BOLD, 29));
            g2.drawString("21", 29, 67);
            g2.dispose();
            return;
        }

        g2.setColor(Color.WHITE);
        g2.fillRoundRect(2, 2, w, h, 14, 14);
        g2.setColor(isRed(card.getSuit()) ? Theme.RED : Color.BLACK);
        g2.setFont(new Font("SansSerif", Font.BOLD, 23));
        g2.drawString(card.getRank(), 11, 31);
        g2.setFont(new Font("Serif", Font.PLAIN, 29));
        g2.drawString(symbolOf(card.getSuit()), 30, 67);
        g2.setFont(new Font("SansSerif", Font.BOLD, 12));
        g2.drawString(card.getRank(), w - 22, h - 10);
        g2.dispose();
    }
}
