
package model;

<<<<<<< HEAD
public class Dealer extends Users{
=======
public class Dealer {

    private Hand hand;
>>>>>>> e32d335cf210bfa0957d30e32772fab6cd3adcd2

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
