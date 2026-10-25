
package presenter;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JButton;

import model.Card;
import model.Game;
import model.GameResult;
import view.GameView;
import view.LoginView;

public class Presenter implements ActionListener {

    private Game game;
    private LoginView loginView;
    private GameView gameView;

    public Presenter() {
        game = new Game();
        loginView = new LoginView();
        setLoginEvents();
    }

    public void run() {
        loginView.setVisible(true);
    }

    private void setLoginEvents() {
        loginView.getBtnStart().addActionListener(this);
    }

    private void setGameEvents() {
        gameView.getBtnHit().addActionListener(this);
        gameView.getBtnStand().addActionListener(this);
        gameView.getBtnDeal().addActionListener(this);
        gameView.getBtnClear().addActionListener(this);
        gameView.getBtnHistory().addActionListener(this);
        gameView.getBtnDouble().addActionListener(this);
        gameView.getBtnSplit().addActionListener(this);

        for (JButton chip : gameView.getChips()) {
            chip.addActionListener(this);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        if (source == loginView.getBtnStart()) {
            login();
            return;
        }

        if (gameView == null) {
            return;
        }

        if (source == gameView.getBtnHit()) {
            hit();
        } else if (source == gameView.getBtnStand()) {
            stand();
        } else if (source == gameView.getBtnDouble()) {
            doubleDown();
        } else if (source == gameView.getBtnSplit()) {
            split();
        } else if (source == gameView.getBtnDeal()) {
            startRound();
        } else if (source == gameView.getBtnClear()) {
            clearBet();
        } else if (source == gameView.getBtnHistory()) {
            gameView.showHistory(game.getHistory());
        } else if (gameView.getChips().contains(source)) {
            double amount = Double.parseDouble(
                    ((JButton) source).getActionCommand());

            placeBet(amount);
        }
    }

    // ---------- Login ----------

    private void login() {
        String error = game.login(
                loginView.getUsername(),
                loginView.getEmail(),
                loginView.getPassword()
        );

        if (error != null) {
            loginView.showMessage(error);
            return;
        }

        loginView.dispose();

        gameView = new GameView();
        setGameEvents();

        gameView.setPlayerName(game.getCurrentUser().getUsername());
        updateAll();

        if (game.isReturningUser()) {
            gameView.showInfo(
                    "Welcome back, "
                    + game.getCurrentUser().getUsername()
                    + ". Place a bet to start."
            );
        }

        gameView.setVisible(true);
        checkBroke();
    }

    // ---------- Apuestas ----------

    private void placeBet(double amount) {
        String error = game.addToBet(amount);

        if (error != null) {
            gameView.showInfo(error);
            return;
        }

        updateAll();
        gameView.showInfo(
                "Bet: " + money(game.getBet()) + " • Press NEW HAND.");
    }

    private void clearBet() {
        game.clearBet();
        updateAll();
        gameView.showInfo("Bet cleared.");
    }

    // ---------- Partida ----------

    private void startRound() {
        String error = game.startRound();

        if (error != null) {
            gameView.showInfo(error);
            return;
        }

        updateAll();

        if (game.isFinished()) {
            finishRound();
        } else {
            gameView.showInfo("Hand 1: HIT, STAND, DOUBLE or SPLIT.");
        }
    }

    private void hit() {
        game.playerHit();
        updateAll();

        if (game.isFinished()) {
            finishRound();
        } else {
            showCurrentHand();
        }
    }

    private void stand() {
        if (!game.isPlaying()) {
            return;
        }

        game.playerStand();
        updateAll();

        if (game.isFinished()) {
            finishRound();
        } else {
            showCurrentHand();
        }
    }

    private void doubleDown() {
        String error = game.doubleDown();

        if (error != null) {
            gameView.showInfo(error);
            return;
        }

        updateAll();

        if (game.isFinished()) {
            finishRound();
        } else {
            showCurrentHand();
        }
    }

    private void split() {
        String error = game.split();

        if (error != null) {
            gameView.showInfo(error);
            return;
        }

        updateAll();

        if (game.isFinished()) {
            finishRound();
        } else {
            showCurrentHand();
        }
    }

    private void showCurrentHand() {
        gameView.showInfo(
                "Playing hand " + (game.getCurrentHandIndex() + 1)
                + " of " + game.getHandCount()
                + ". Bet: " + money(game.getCurrentHandBet()));
    }

    // ---------- Resultado ----------

    private void finishRound() {
        String result = game.getResult();

        if (result == null) {
            return;
        }

        String message = game.getResultMessage();

        if (result.equals(GameResult.PLAYER_BLACKJACK)
                || result.equals(GameResult.PLAYER_WIN)
                || result.equals(GameResult.DEALER_BUST)) {
            gameView.showWin(message);
        } else if (result.equals(GameResult.PUSH)) {
            gameView.showPush(message);
        } else {
            gameView.showLose(message);
        }

        updateAll();
        checkBroke();
    }

    private void checkBroke() {
        if (game.isBroke() && gameView.askRecharge()) {
            game.recharge();
            updateAll();
            gameView.showInfo("Balance restored. Place a bet to start.");
        }
    }

    // ---------- Actualización de la interfaz ----------

    private void updateAll() {
        updateHeader();
        updateBoard();

        gameView.showButtons(
                game.isPlaying(),
                game.getBet(),
                game.canDouble(),
                game.canSplit()
        );
    }

    private void updateHeader() {
        gameView.showHeader(
                game.getCurrentUser().getBalance(),
                game.getBet()
        );
    }

    private void updateBoard() {
        List<Card> dealerCards = game.getDealerCards();

        String dealerText = "";

        if (!dealerCards.isEmpty()) {
            if (game.isPlaying()) {
                dealerText = "Visible card: "
                        + dealerCards.get(0).getRank()
                        + " • Hidden card: ?";
            } else {
                dealerText = "Total: " + game.getDealerScore()
                        + (game.isDealerBust() ? " • BUST" : "");
            }
        }

        gameView.showBoard(
                dealerCards,
                game.getPlayerHands(),
                game.getPlayerScores(),
                game.getHandBets(),
                game.getPlayerBusts(),
                game.getCurrentHandIndex(),
                game.isPlaying(),
                dealerText
        );
    }

    private String money(double amount) {
        return String.format("$%,.2f", amount);
    }
}
