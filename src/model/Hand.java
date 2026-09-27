package model;

import java.util.ArrayList;
import java.util.List;

public class Hand {
    private List<Card> cards;

    public Hand() {
        this.cards = new ArrayList<>();
    }

    public void addCard(Card card) {
        if (card != null) {
            cards.add(card);
        }
    }

    public int calculateScore() {
        int total = 0;
        int aces = 0;

        for (Card card : cards) {
            int val = card.getValue();
            if (val == 11) {
                aces++;
            }
            total += val;
        }

        while (total > 21 && aces > 0) {
            total -= 10;
            aces--;
        }

        return total;
    }

    public int getValue() {
        return calculateScore();
    }

    public boolean isBust() {
        return calculateScore() > 21;
    }

    public boolean isBlackjack() {
        return cards.size() == 2 && calculateScore() == 21;
    }

    public void clear() {
        cards.clear();
    }

    public List<Card> getCards() {
        return new ArrayList<>(cards);
    }

    public int getCardCount() {
        return cards.size();
    }

    @Override
    public String toString() {
        return cards.toString() + " (Puntos: " + calculateScore() + ")";
    }
}