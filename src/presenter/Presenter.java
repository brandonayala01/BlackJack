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
        this.game = new Game();
        this.loginView = new LoginView();
        this.setLoginEvents();
    }

    public void run() {
        this.loginView.setVisible(true);
    }

    public void setLoginEvents() {
        this.loginView.getBtnStart().addActionListener(this);
    }

    public void setGameEvents() {
        this.gameView.getBtnHit().addActionListener(this);
        this.gameView.getBtnStand().addActionListener(this);
        this.gameView.getBtnDeal().addActionListener(this);
        this.gameView.getBtnClear().addActionListener(this);
        this.gameView.getBtnHistory().addActionListener(this);
        for (JButton chip : this.gameView.getChips()) {
            chip.addActionListener(this);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        if (source == this.loginView.getBtnStart()) {
            this.login();
            return;
        }
        if (this.gameView == null) {
            return;
        }

        if (source == this.gameView.getBtnHit()) {
            this.hit();
        } else if (source == this.gameView.getBtnStand()) {
            this.stand();
        } else if (source == this.gameView.getBtnDeal()) {
            this.startRound();
        } else if (source == this.gameView.getBtnClear()) {
            this.clearBet();
        } else if (source == this.gameView.getBtnHistory()) {
            this.gameView.showHistory(this.game.getHistory());
        } else if (this.gameView.getChips().contains(source)) {
            // el valor de la ficha viene en el actionCommand ("10", "25", ...)
            this.placeBet(Double.parseDouble(((JButton) source).getActionCommand()));
        }
    }

    // ---------- Login ----------

    private void login() {
        String error = this.game.login(this.loginView.getUsername(),
                this.loginView.getEmail(), this.loginView.getPassword());
        if (error != null) {
            this.loginView.showMessage(error);
            return;
        }

        this.loginView.dispose();
        this.gameView = new GameView();
        this.setGameEvents();
        this.gameView.setPlayerName(this.game.getCurrentUser().getUsername());
        this.updateAll();
        if (this.game.isReturningUser()) {
            this.gameView.showInfo("Welcome back, " + this.game.getCurrentUser().getUsername()
                    + ". Place a bet to start.");
        }
        this.gameView.setVisible(true);
        this.checkBroke();
    }

    // ---------- Acciones de la mano ----------

    private void placeBet(double amount) {
        String error = this.game.addToBet(amount);
        if (error != null) {
            this.gameView.showInfo(error);
            return;
        }
        this.updateAll();
        this.gameView.showInfo("Bet: " + String.format("$%,.2f", this.game.getBet()) + " • Press NEW HAND.");
    }

    private void clearBet() {
        this.game.clearBet();
        this.updateAll();
        this.gameView.showInfo("Bet cleared.");
    }

    private void startRound() {
        String error = this.game.startRound();
        if (error != null) {
            this.gameView.showInfo(error);
            return;
        }
        this.updateAll();
        if (this.game.isFinished()) {
            this.finishRound();
        } else {
            this.gameView.showInfo("Your turn. HIT or STAND.");
        }
    }

    private void hit() {
        this.game.playerHit();
        this.updateAll();
        if (this.game.isFinished()) {
            this.finishRound();
        }
    }

    private void stand() {
        this.game.playerStand();
        this.updateAll();
        this.finishRound();
    }

    private void finishRound() {
        String result = this.game.getResult();
        String message = this.game.getResultMessage();

        if (result.equals(GameResult.PLAYER_BLACKJACK) || result.equals(GameResult.PLAYER_WIN)
                || result.equals(GameResult.DEALER_BUST)) {
            this.gameView.showWin(message);
        } else if (result.equals(GameResult.PUSH)) {
            this.gameView.showPush(message);
        } else {
            this.gameView.showLose(message);
        }
        this.checkBroke();
    }

    private void checkBroke() {
        if (this.game.isBroke() && this.gameView.askRecharge()) {
            this.game.recharge();
            this.updateAll();
            this.gameView.showInfo("Balance restored. Place a bet to start.");
        }
    }

    // ---------- Pintar la vista con los datos del modelo ----------

    private void updateAll() {
        this.updateHeader();
        this.updateBoard();
        this.gameView.showButtons(this.game.isPlaying(), this.game.getBet());
    }

    private void updateHeader() {
        this.gameView.showHeader(this.game.getCurrentUser().getBalance(), this.game.getBet());
    }

    private void updateBoard() {
        List<Card> dealerCards = this.game.getDealerCards();
        List<Card> playerCards = this.game.getPlayerCards();
        boolean playing = this.game.isPlaying();

        String dealerText = "";
        if (!dealerCards.isEmpty()) {
            if (playing) {
                dealerText = "Visible card: " + dealerCards.get(0).getValue() + "  •  Hidden card: ?";
            } else {
                dealerText = "Total: " + this.game.getDealerScore()
                        + (this.game.isDealerBust() ? "  •  BUST" : "");
            }
        }

        String playerText = "";
        if (!playerCards.isEmpty()) {
            playerText = "Total: " + this.game.getPlayerScore()
                    + (this.game.isPlayerBust() ? "  •  BUST" : "");
        }

        this.gameView.showBoard(dealerCards, playerCards, playing, dealerText, playerText);
    }
}
