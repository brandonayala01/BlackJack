
package model;

public class Card {

    private String suit;
    private String rank;

    public Card(String suit, String rank) {
        if (suit == null || rank == null) {
            throw new IllegalArgumentException(
                    "Suit and rank cannot be null.");
        }

        if (!suit.equals("Hearts")
                && !suit.equals("Diamonds")
                && !suit.equals("Clubs")
                && !suit.equals("Spades")) {
            throw new IllegalArgumentException("Invalid suit: " + suit);
        }

        if (!rank.matches("([2-9]|10|J|Q|K|A)")) {
            throw new IllegalArgumentException("Invalid rank: " + rank);
        }

        this.suit = suit;
        this.rank = rank;
    }

    public String getSuit() {
        return suit;
    }

    public String getRank() {
        return rank;
    }

    public int getValue() {
        if (rank.equals("A")) {
            return 11;
        }

        if (rank.equals("J") || rank.equals("Q")
                || rank.equals("K")) {
            return 10;
        }

        return Integer.parseInt(rank);
    }

    @Override
    public String toString() {
        return rank + " of " + suit;
    }
}
