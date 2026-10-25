package model;

public class Dealer extends Users{

    public Dealer() {
        super("Dealer", 0);
    }

    public boolean mustHit() {

        return getHand().getValue() < 17;
    }

    public boolean mustStand() {

        return getHand().getValue() >= 17;
    }
}