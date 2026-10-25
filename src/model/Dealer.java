
package model;

public class Dealer {

    private Hand hand;

    public Dealer() {
        this.hand = new Hand();
    }

    public Hand getHand() {
        return hand;
    }

    public void hit(Card card) {
        hand.addCard(card);
    }

    public void resetHand() {
        hand.clearHand();
    }

    public boolean mustHit() {
        return hand.calculateScore() < 17;
    }

    public boolean isBust() {
        return hand.isBust();
    }
}
