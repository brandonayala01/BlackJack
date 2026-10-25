
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
    public static final int MAX_HANDS = 4;

    private final UsersRepository usersRepository;
    private final HistoryRepository historyRepository;

    private User currentUser;
    private boolean returningUser;

    private Deck deck;
    private Dealer dealer;

    private ArrayList<Hand> playerHands;
    private ArrayList<Double> handBets;
    private ArrayList<Boolean> handFinished;
    private ArrayList<Boolean> naturalEligible;

    private int currentHandIndex;
    private double bet;
    private String status;
    private String result;
    private String resultMessage;

    public Game() {
        this.usersRepository = new UsersRepository();
        this.historyRepository = new HistoryRepository();

        this.deck = new Deck();
        this.dealer = new Dealer();

        this.playerHands = new ArrayList<>();
        this.handBets = new ArrayList<>();
        this.handFinished = new ArrayList<>();
        this.naturalEligible = new ArrayList<>();

        this.currentHandIndex = 0;
        this.bet = 0;
        this.status = GameStatus.WAITING;
        this.result = null;
        this.resultMessage = "";
    }

    // ---------- Usuario ----------

    public String login(String username, String email, String password) {
        if (username == null || email == null || password == null) {
            return "Username, email and password are required.";
        }

        username = username.trim();
        email = email.trim();

        if (username.length() < 2 || email.isEmpty() || password.isEmpty()) {
            return "Enter a username, email and password.";
        }

        User found = usersRepository.getMyUsers().findByName(username);

        if (found == null) {
            currentUser = new User(username, email, password);
            usersRepository.addUser(currentUser);
            returningUser = false;
            return null;
        }

        if (!found.getPassword().equals(password)) {
            return "Wrong password for that user.";
        }

        currentUser = found;
        returningUser = true;
        return null;
    }

    public boolean isReturningUser() {
        return returningUser;
    }

    public boolean isBroke() {
        return currentUser != null
                && !isPlaying()
                && currentUser.getBalance() < MIN_BET;
    }

    public void recharge() {
        if (currentUser != null) {
            currentUser.resetBalance();
            usersRepository.save();
        }
    }

    // ---------- Apuestas ----------

    public String addToBet(double amount) {
        if (currentUser == null) {
            return "Log in before placing a bet.";
        }

        if (isPlaying()) {
            return "You can't change the bet during a hand.";
        }

        if (!Double.isFinite(amount) || amount <= 0) {
            return "Enter a valid positive bet.";
        }

        if (bet + amount > MAX_BET) {
            return "The maximum initial bet is $5,000.";
        }

        if (bet + amount > currentUser.getBalance()) {
            return "Not enough balance for that bet.";
        }

        bet += amount;
        return null;
    }

    public void clearBet() {
        if (!isPlaying()) {
            bet = 0;
        }
    }

    // ---------- Inicio de la partida ----------

    public String startRound() {
        if (currentUser == null) {
            return "Log in before starting a game.";
        }

        if (isPlaying()) {
            return "There is already a hand in progress.";
        }

        if (bet < MIN_BET) {
            return "Place a bet first.";
        }

        if (!currentUser.removeBalance(bet)) {
            return "Not enough balance for that bet.";
        }

        usersRepository.save();

        deck.shuffle();
        dealer.resetHand();

        playerHands.clear();
        handBets.clear();
        handFinished.clear();
        naturalEligible.clear();

        Hand firstHand = new Hand();
        playerHands.add(firstHand);
        handBets.add(bet);
        handFinished.add(false);
        naturalEligible.add(true);

        currentHandIndex = 0;
        result = null;
        resultMessage = "";
        status = GameStatus.PLAYING;

        firstHand.addCard(deck.drawCard());
        dealer.hit(deck.drawCard());
        firstHand.addCard(deck.drawCard());
        dealer.hit(deck.drawCard());

        if (firstHand.isBlackjack()) {
            handFinished.set(0, true);
            resolveRound();
        }

        return null;
    }

    // ---------- Acciones del jugador ----------

    public void playerHit() {
        if (!isPlaying() || !isCurrentHandActive()) {
            return;
        }

        Hand hand = playerHands.get(currentHandIndex);
        hand.addCard(deck.drawCard());

        if (hand.isBust() || hand.calculateScore() >= 21) {
            handFinished.set(currentHandIndex, true);
            advanceToNextHand();
        }
    }

    public void playerStand() {
        if (!isPlaying() || !isCurrentHandActive()) {
            return;
        }

        handFinished.set(currentHandIndex, true);
        advanceToNextHand();
    }

    // ---------- Doblar ----------

    public String doubleDown() {
        if (!isPlaying() || !isCurrentHandActive()) {
            return "There is no active hand to double.";
        }

        Hand hand = playerHands.get(currentHandIndex);
        double handBet = handBets.get(currentHandIndex);

        if (hand.getCardCount() != 2) {
            return "You can only double on your first two cards.";
        }

        if (currentUser.getBalance() < handBet) {
            return "Not enough balance to double.";
        }

        if (!currentUser.removeBalance(handBet)) {
            return "Unable to deduct the additional bet.";
        }

        handBets.set(currentHandIndex, handBet * 2);
        hand.addCard(deck.drawCard());

        handFinished.set(currentHandIndex, true);
        usersRepository.save();

        advanceToNextHand();
        return null;
    }

    // ---------- Dividir ----------

    public String split() {
        if (!isPlaying() || !isCurrentHandActive()) {
            return "There is no active hand to split.";
        }

        Hand hand = playerHands.get(currentHandIndex);

        if (hand.getCardCount() != 2) {
            return "You can only split your first two cards.";
        }

        if (playerHands.size() >= MAX_HANDS) {
            return "You can play a maximum of four hands.";
        }

        Card firstCard = hand.getCardAt(0);
        Card secondCard = hand.getCardAt(1);

        if (!firstCard.getRank().equals(secondCard.getRank())) {
            return "You need a pair of equal ranks to split.";
        }

        double handBet = handBets.get(currentHandIndex);

        if (currentUser.getBalance() < handBet) {
            return "Not enough balance for the additional bet.";
        }

        if (!currentUser.removeBalance(handBet)) {
            return "Unable to deduct the additional bet.";
        }

        boolean splitAces = firstCard.getRank().equals("A");

        hand.removeCardAt(1);

        Hand newHand = new Hand();
        newHand.addCard(secondCard);

        playerHands.add(currentHandIndex + 1, newHand);
        handBets.add(currentHandIndex + 1, handBet);

        handFinished.add(currentHandIndex + 1, false);
        naturalEligible.set(currentHandIndex, false);
        naturalEligible.add(currentHandIndex + 1, false);

        // Cada mano recibe una carta nueva.
        hand.addCard(deck.drawCard());
        newHand.addCard(deck.drawCard());

        // Regla habitual: las parejas de ases reciben una sola carta.
        if (splitAces) {
            handFinished.set(currentHandIndex, true);
            handFinished.set(currentHandIndex + 1, true);
        } else {
            handFinished.set(currentHandIndex,
                    hand.isBust() || hand.calculateScore() >= 21);

            handFinished.set(currentHandIndex + 1,
                    newHand.isBust() || newHand.calculateScore() >= 21);
        }

        usersRepository.save();

        if (handFinished.get(currentHandIndex)) {
            advanceToNextHand();
        }

        return null;
    }

    public boolean canDouble() {
        if (!isPlaying() || !isCurrentHandActive()) {
            return false;
        }

        Hand hand = playerHands.get(currentHandIndex);
        double handBet = handBets.get(currentHandIndex);

        return hand.getCardCount() == 2
                && currentUser.getBalance() >= handBet;
    }

    public boolean canSplit() {
        if (!isPlaying() || !isCurrentHandActive()
                || playerHands.size() >= MAX_HANDS) {
            return false;
        }

        Hand hand = playerHands.get(currentHandIndex);

        if (hand.getCardCount() != 2) {
            return false;
        }

        return hand.getCardAt(0).getRank().equals(
                hand.getCardAt(1).getRank())
                && currentUser.getBalance() >= handBets.get(currentHandIndex);
    }

    private boolean isCurrentHandActive() {
        return currentHandIndex >= 0
                && currentHandIndex < playerHands.size()
                && !handFinished.get(currentHandIndex);
    }

    // ---------- Avance entre manos ----------

    private void advanceToNextHand() {
        for (int i = currentHandIndex + 1; i < playerHands.size(); i++) {
            if (!handFinished.get(i)) {
                currentHandIndex = i;
                status = GameStatus.PLAYING;
                return;
            }
        }

        resolveRound();
    }

    private void playDealer() {
        while (dealer.mustHit()) {
            Card card = deck.drawCard();

            if (card == null) {
                break;
            }

            dealer.hit(card);
        }
    }

    // ---------- Resultado y pagos ----------

    private void resolveRound() {
        status = GameStatus.DEALER_TURN;

        boolean hasLiveHand = false;

        for (Hand hand : playerHands) {
            if (!hand.isBust()) {
                hasLiveHand = true;
                break;
            }
        }

        if (hasLiveHand) {
            playDealer();
        }

        Hand dealerHand = dealer.getHand();
        int dealerScore = dealerHand.calculateScore();

        double totalPayout = 0;
        boolean anyWin = false;
        boolean anyPush = false;
        boolean allNaturalBlackjack = playerHands.size() == 1
                && naturalEligible.get(0)
                && playerHands.get(0).isBlackjack()
                && !dealerHand.isBlackjack();

        StringBuilder messages = new StringBuilder();
        String date = LocalDateTime.now().format(
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        for (int i = 0; i < playerHands.size(); i++) {
            Hand hand = playerHands.get(i);
            double handBet = handBets.get(i);
            int playerScore = hand.calculateScore();
            double payout = 0;
            String handResult;
            String message;

            if (hand.isBust()) {
                handResult = GameResult.PLAYER_BUST;
                message = "BUST";
            } else if (naturalEligible.get(i) && hand.isBlackjack()) {
                if (dealerHand.isBlackjack()) {
                    handResult = GameResult.PUSH;
                    payout = handBet;
                    message = "BLACKJACK PUSH";
                } else {
                    handResult = GameResult.PLAYER_BLACKJACK;
                    payout = handBet * 2.5;
                    message = "BLACKJACK! Pays 3:2";
                }
            } else if (dealerHand.isBlackjack()) {
                handResult = GameResult.DEALER_WIN;
                message = "DEALER BLACKJACK";
            } else if (dealerHand.isBust()) {
                handResult = GameResult.DEALER_BUST;
                payout = handBet * 2;
                message = "DEALER BUSTS - YOU WIN";
            } else if (playerScore > dealerScore) {
                handResult = GameResult.PLAYER_WIN;
                payout = handBet * 2;
                message = "YOU WIN";
            } else if (playerScore < dealerScore) {
                handResult = GameResult.DEALER_WIN;
                message = "DEALER WINS";
            } else {
                handResult = GameResult.PUSH;
                payout = handBet;
                message = "PUSH";
            }

            if (handResult.equals(GameResult.PLAYER_WIN)
                    || handResult.equals(GameResult.PLAYER_BLACKJACK)
                    || handResult.equals(GameResult.DEALER_BUST)) {
                anyWin = true;
            }

            if (handResult.equals(GameResult.PUSH)) {
                anyPush = true;
            }

            totalPayout += payout;

            if (messages.length() > 0) {
                messages.append(" | ");
            }

            messages.append("Hand ").append(i + 1)
                    .append(": ").append(message)
                    .append(" (").append(money(payout - handBet)).append(" net)");

            historyRepository.addRecord(new GameRecord(
                    currentUser.getUsername(),
                    handResult,
                    handBet,
                    playerScore,
                    dealerScore,
                    date
            ));
        }

        currentUser.addBalance(totalPayout);
        usersRepository.save();

        if (allNaturalBlackjack) {
            result = GameResult.PLAYER_BLACKJACK;
        } else if (anyWin) {
            result = GameResult.PLAYER_WIN;
        } else if (anyPush) {
            result = GameResult.PUSH;
        } else {
            result = GameResult.DEALER_WIN;
        }

        resultMessage = messages.toString();
        bet = 0;
        status = GameStatus.FINISHED;
    }

    private String money(double amount) {
        return String.format("$%,.2f", amount);
    }

    // ---------- Consultas ----------

    public boolean isPlaying() {
        return GameStatus.PLAYING.equals(status)
                || GameStatus.DEALER_TURN.equals(status);
    }

    public boolean isFinished() {
        return GameStatus.FINISHED.equals(status);
    }

    public ArrayList<GameRecord> getHistory() {
        if (currentUser == null) {
            return new ArrayList<>();
        }

        return historyRepository.getRecordsByUser(
                currentUser.getUsername());
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public double getBet() {
        if (isPlaying()) {
            double total = 0;

            for (double handBet : handBets) {
                total += handBet;
            }

            return total;
        }

        return bet;
    }

    public double getCurrentHandBet() {
        if (currentHandIndex < 0 || currentHandIndex >= handBets.size()) {
            return 0;
        }

        return handBets.get(currentHandIndex);
    }

    public int getCurrentHandIndex() {
        return currentHandIndex;
    }

    public int getHandCount() {
        return playerHands.size();
    }

    public List<Double> getHandBets() {
        return new ArrayList<>(handBets);
    }

    public List<List<Card>> getPlayerHands() {
        List<List<Card>> hands = new ArrayList<>();

        for (Hand hand : playerHands) {
            hands.add(hand.getCards());
        }

        return hands;
    }

    public List<Integer> getPlayerScores() {
        List<Integer> scores = new ArrayList<>();

        for (Hand hand : playerHands) {
            scores.add(hand.calculateScore());
        }

        return scores;
    }

    public List<Boolean> getPlayerBusts() {
        List<Boolean> busts = new ArrayList<>();

        for (Hand hand : playerHands) {
            busts.add(hand.isBust());
        }

        return busts;
    }

    public String getResult() {
        return result;
    }

    public String getResultMessage() {
        return resultMessage;
    }

    public List<Card> getPlayerCards() {
        if (playerHands.isEmpty()) {
            return new ArrayList<>();
        }

        return playerHands.get(currentHandIndex).getCards();
    }

    public List<Card> getDealerCards() {
        return dealer.getHand().getCards();
    }

    public int getPlayerScore() {
        if (playerHands.isEmpty()) {
            return 0;
        }

        return playerHands.get(currentHandIndex).calculateScore();
    }

    public int getDealerScore() {
        return dealer.getHand().calculateScore();
    }

    public boolean isPlayerBust() {
        return !playerHands.isEmpty()
                && playerHands.get(currentHandIndex).isBust();
    }

    public boolean isDealerBust() {
        return dealer.isBust();
    }
}
