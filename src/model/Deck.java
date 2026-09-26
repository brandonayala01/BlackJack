package model;

import java.util.Random;

public class Deck {

    private Card[] cards;
    private int top;

    public Deck() {
        cards = new Card[52];
        top = 0;

        createDeck();
        shuffle();
    }

    private void createDeck() {

        String[] suits = {
            "Corazones",
            "Diamantes",
            "Tréboles",
            "Picas"
        };

        String[] ranks = {
            "A",
            "2",
            "3",
            "4",
            "5",
            "6",
            "7",
            "8",
            "9",
            "10",
            "J",
            "Q",
            "K"
        };

        int position = 0;

        for (int i = 0; i < suits.length; i++) {

            for (int j = 0; j < ranks.length; j++) {

                cards[position] = new Card(suits[i], ranks[j]);
                position++;
            }
        }
    }

    public void shuffle() {

        Random random = new Random();

        for (int i = cards.length - 1; i > 0; i--) {

            int position = random.nextInt(i + 1);

            Card temporary = cards[i];
            cards[i] = cards[position];
            cards[position] = temporary;
        }

        top = 0;
    }

    public Card drawCard() {

        if (top >= cards.length) {
            return null;
        }

        Card card = cards[top];
        top++;

        return card;
    }

    public int getRemainingCards() {
        return cards.length - top;
    }
}
