package model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import repositories.HistoryRepository;
import repositories.UsersRepository;

public class Game {

    public static final double MIN_BET = 1;
    public static final double MAX_BET = 5000;

    private UsersRepository usersRepository;
    private HistoryRepository historyRepository;
    private User currentUser;
    private boolean returningUser;

    private Deck deck;
<<<<<<< HEAD
    private Users player;
=======
    private Hand playerHand;
>>>>>>> e32d335cf210bfa0957d30e32772fab6cd3adcd2
    private Dealer dealer;
    private double bet;
    private String status;
    private String result;
    private String resultMessage;

<<<<<<< HEAD
    public Game(Users player) {
        this.player = player;
        this.dealer = new Dealer();
=======
    public Game() {
        this.usersRepository = new UsersRepository();
        this.historyRepository = new HistoryRepository();
>>>>>>> e32d335cf210bfa0957d30e32772fab6cd3adcd2
        this.deck = new Deck();
        this.playerHand = new Hand();
        this.dealer = new Dealer();
        this.bet = 0;
        this.status = GameStatus.WAITING;
        this.result = null;
        this.resultMessage = "";
    }

    // ---------- Usuario ----------

    // Si el usuario existe lo reutiliza (conserva su saldo). Devuelve null si todo salio bien
    // o un mensaje de error para mostrar
    public String login(String username, String email, String password) {
        if (username.length() < 2 || email.isEmpty() || password.isEmpty()) {
            return "Enter a username (at least 2 characters), an email and a password.";
        }

        User found = this.usersRepository.getMyUsers().findByName(username);
        if (found == null) {
            this.currentUser = new User(username, email, password);
            this.usersRepository.addUser(this.currentUser);
            this.returningUser = false;
            return null;
        }
        if (!found.getPassword().equals(password)) {
            return "Wrong password for that user.";
        }
        this.currentUser = found;
        this.returningUser = true;
        return null;
    }

    public boolean isReturningUser() {
        return this.returningUser;
    }

    // Sin saldo suficiente para la apuesta minima
    public boolean isBroke() {
        return !isPlaying() && this.currentUser.getBalance() < MIN_BET;
    }

    public void recharge() {
        this.currentUser.resetBalance();
        this.usersRepository.save();
    }

    // ---------- Apuesta ----------

    // Suma una ficha a la apuesta. Devuelve null si se pudo o un mensaje de error
    public String addToBet(double amount) {
        if (isPlaying()) {
            return "You can't change the bet during a hand.";
        }
        if (this.bet + amount > MAX_BET) {
            return "The maximum bet is $5,000.";
        }
        if (this.bet + amount > this.currentUser.getBalance()) {
            return "Not enough balance for that bet.";
        }
        this.bet += amount;
        return null;
    }

    public void clearBet() {
        if (!isPlaying()) {
            this.bet = 0;
        }
    }

    // ---------- Mano ----------

    // Descuenta la apuesta, reparte y guarda el saldo. Devuelve null si se pudo o un mensaje de error
    public String startRound() {
        if (isPlaying()) {
            return "There is already a hand in progress.";
        }
        if (this.bet < MIN_BET) {
            return "Place a bet first.";
        }
        if (!this.currentUser.removeBalance(this.bet)) {
            return "Not enough balance for that bet.";
        }
        this.usersRepository.save();

        this.deck.shuffle();
        this.playerHand.clearHand();
        this.dealer.resetHand();
        this.result = null;
        this.resultMessage = "";
        this.status = GameStatus.PLAYING;

        this.playerHand.addCard(this.deck.drawCard());
        this.dealer.hit(this.deck.drawCard());
        this.playerHand.addCard(this.deck.drawCard());
        this.dealer.hit(this.deck.drawCard());

        if (this.playerHand.isBlackjack()) {
            this.determineResult();
        }
        return null;
    }

    public void playerHit() {
        if (!isPlaying()) {
            return;
        }

        this.playerHand.addCard(this.deck.drawCard());

        if (this.playerHand.isBust()) {
            this.determineResult();
        } else if (this.playerHand.calculateScore() == 21) {
            this.playerStand();
        }
    }

    public void playerStand() {
        if (!isPlaying()) {
            return;
        }

        this.status = GameStatus.DEALER_TURN;
        this.playDealer();
        this.determineResult();
    }

    private void playDealer() {
        while (this.dealer.mustHit()) {
            this.dealer.hit(this.deck.drawCard());
        }
    }

    // Define el resultado, paga la apuesta y guarda el saldo y la mano en los archivos
    private void determineResult() {
        Hand dealerHand = this.dealer.getHand();
        int playerScore = this.playerHand.calculateScore();
        int dealerScore = dealerHand.calculateScore();
        double payout = 0;

        if (this.playerHand.isBust()) {
            this.result = GameResult.PLAYER_BUST;
            this.resultMessage = "BUST! You went over 21 • -" + money(this.bet);
        } else if (this.playerHand.isBlackjack()) {
            if (dealerHand.isBlackjack()) {
                this.result = GameResult.PUSH;
                payout = this.bet;
                this.resultMessage = "PUSH. Your bet is returned.";
            } else {
                this.result = GameResult.PLAYER_BLACKJACK;
                payout = this.bet * 2.5;
                this.resultMessage = "BLACKJACK! Pays 3:2 • +" + money(this.bet * 1.5);
            }
        } else if (this.dealer.isBust()) {
            this.result = GameResult.DEALER_BUST;
            payout = this.bet * 2;
            this.resultMessage = "The dealer busts with " + dealerScore + ". YOU WIN! • +" + money(this.bet);
        } else if (dealerHand.isBlackjack()) {
            this.result = GameResult.DEALER_WIN;
            this.resultMessage = "DEALER WINS! " + dealerScore + " vs " + playerScore + " • -" + money(this.bet);
        } else if (playerScore > dealerScore) {
            this.result = GameResult.PLAYER_WIN;
            payout = this.bet * 2;
            this.resultMessage = "YOU WIN! " + playerScore + " vs " + dealerScore + " • +" + money(this.bet);
        } else if (playerScore < dealerScore) {
            this.result = GameResult.DEALER_WIN;
            this.resultMessage = "DEALER WINS! " + dealerScore + " vs " + playerScore + " • -" + money(this.bet);
        } else {
            this.result = GameResult.PUSH;
            payout = this.bet;
            this.resultMessage = "PUSH. Your bet is returned.";
        }

        this.currentUser.addBalance(payout);
        this.usersRepository.save();

        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        this.historyRepository.addRecord(new GameRecord(
                this.currentUser.getUsername(), this.result, this.bet, playerScore, dealerScore, date));

        this.bet = 0;
        this.status = GameStatus.FINISHED;
    }

    private String money(double n) {
        return String.format("$%,.2f", n);
    }

<<<<<<< HEAD
    public Users getPlayer() {
        return player;
=======
    // ---------- Consultas ----------

    public boolean isPlaying() {
        return this.status.equals(GameStatus.PLAYING);
>>>>>>> e32d335cf210bfa0957d30e32772fab6cd3adcd2
    }

    public boolean isFinished() {
        return this.status.equals(GameStatus.FINISHED);
    }

    public ArrayList<GameRecord> getHistory() {
        return this.historyRepository.getRecordsByUser(this.currentUser.getUsername());
    }

    public User getCurrentUser() {
        return this.currentUser;
    }

    public double getBet() {
        return this.bet;
    }

    public String getResult() {
        return this.result;
    }

    public String getResultMessage() {
        return this.resultMessage;
    }

    public List<Card> getPlayerCards() {
        return this.playerHand.getCards();
    }

    public List<Card> getDealerCards() {
        return this.dealer.getHand().getCards();
    }

    public int getPlayerScore() {
        return this.playerHand.calculateScore();
    }

    public int getDealerScore() {
        return this.dealer.getHand().calculateScore();
    }

    public boolean isPlayerBust() {
        return this.playerHand.isBust();
    }

    public boolean isDealerBust() {
        return this.dealer.isBust();
    }
}
