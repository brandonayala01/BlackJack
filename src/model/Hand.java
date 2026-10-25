
package model;

import java.util.ArrayList;
import java.util.List;

public class Hand {

    private ArrayList<Card> cards;

    public Hand() {
        cards = new ArrayList<>();
    }

    public void addCard(Card card) {
        if (card != null) {
            cards.add(card);
        }
    }

    public Card removeCardAt(int index) {
        if (index < 0 || index >= cards.size()) {
            throw new IndexOutOfBoundsException("Invalid card index.");
        }

        return cards.remove(index);
    }

    public Card getCardAt(int index) {
        return cards.get(index);
    }

    public int getCardCount() {
        return cards.size();
    }

    public int calculateScore() {
        int total = 0;
        int aces = 0;

        for (Card card : cards) {
            int value = card.getValue();

            if (value == 11) {
                aces++;
            }

            total += value;
        }

        while (total > 21 && aces > 0) {
            total -= 10;
            aces--;
        }

        return total;
    }

    public boolean isBust() {
        return calculateScore() > 21;
    }

    public boolean isBlackjack() {
        return cards.size() == 2 && calculateScore() == 21;
    }

    public void clearHand() {
        cards.clear();
    }

    public List<Card> getCards() {
        return new ArrayList<>(cards);
    }

}
