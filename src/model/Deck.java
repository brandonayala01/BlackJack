package model;

import java.util.Random;

public class Deck {
    private Card[] cards;
    private int top;
    private Random random;

    public Deck() {
        cards = new Card[52];
        top = 0;
        random = new Random();

        createDeck();
    }

    private void createDeck() {
        String[] suits = {"Hearts", "Diamonds", "Clubs", "Spades"};
        String[] ranks = {"2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K", "A"};
        int index = 0;

        for (String suit : suits) {
            for (String rank : ranks) {
                cards[index] = new Card(suit, rank);
                index++;
            }
        }
    }

    public void shuffle() {
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
