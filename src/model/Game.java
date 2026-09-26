package model;
import Player;

public class Game {
    private Deck deck;
    private Player player;
    private Dealer dealer;
    private String status;
    private String result;

    public Game(Player player) {
        this.player = player;
        this.dealer = new Dealer();
        this.deck = new Deck();
        this.status = GameStatus.WAITING;
        this.result = null;
    }

    public void startGame() {

        player.resetHand();
        dealer.resetHand();

        deck = new Deck();

        status = GameStatus.PLAYING;
        result = null;

        player.getHand().addCard(deck.drawCard());
        dealer.getHand().addCard(deck.drawCard());

        player.getHand().addCard(deck.drawCard());
        dealer.getHand().addCard(deck.drawCard());
    }

    public void playerHit() {

        if (!status.equals(GameStatus.PLAYING)) {
            return;
        }

        player.getHand().addCard(deck.drawCard());

        if (player.getHand().isBust()) {
            result = GameResult.PLAYER_BUST;
            status = GameStatus.FINISHED;
        }
    }

    public void playerStand() {

        if (!status.equals(GameStatus.PLAYING)) {
            return;
        }

        status = GameStatus.DEALER_TURN;

        playDealer();
        determineResult();
    }

    private void playDealer() {

        while (dealer.mustHit()) {
            dealer.getHand().addCard(deck.drawCard());
        }
    }

    private void determineResult() {

        int playerValue = player.getHand().getValue();
        int dealerValue = dealer.getHand().getValue();

        if (dealer.isBust()) {
            result = GameResult.DEALER_BUST;
        } else if (playerValue > dealerValue) {
            result = GameResult.PLAYER_WIN;
        } else if (playerValue < dealerValue) {
            result = GameResult.DEALER_WIN;
        } else {
            result = GameResult.PUSH;
        }

        status = GameStatus.FINISHED;
    }

    public Deck getDeck() {
        return deck;
    }

    public Player getPlayer() {
        return player;
    }

    public Dealer getDealer() {
        return dealer;
    }

    public String getStatus() {
        return status;
    }

    public String getResult() {
        return result;
    }

    
}