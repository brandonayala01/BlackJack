package model;

public class Player {
    private String username;
    private double balance;
    private double currentBet;
    private Hand hand;

    public Player(String username, double initialBalance) {
        this.username = username;
        this.balance = initialBalance;
        this.currentBet = 0.0;
        this.hand = new Hand();
    }

    public boolean placeBet(double amount) {
        if (amount > 0 && amount <= balance) {
            this.currentBet = amount;
            this.balance -= amount;
            return true;
        }
        return false;
    }

    public void winBet(double multiplier) {
        this.balance += (this.currentBet * multiplier);
        this.currentBet = 0.0;
    }

    public void pushBet() {
        this.balance += this.currentBet;
        this.currentBet = 0.0;
    }

    public void loseBet() {
        this.currentBet = 0.0;
    }

    public void resetHand() {
        this.hand.clear();
        this.currentBet = 0.0;
    }

    public boolean isBust() {
        return this.hand.isBust();
    }

    public String getUsername() {
        return username;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public double getCurrentBet() {
        return currentBet;
    }

    public Hand getHand() {
        return hand;
    }
}