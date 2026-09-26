package model;

public class GameRecord {

    private String username;
    private String result;
    private double bet;
    private int playerValue;
    private int dealerValue;
    private String date;

    public GameRecord(
            String username,
            String result,
            double bet,
            int playerValue,
            int dealerValue,
            String date) {

        this.username = username;
        this.result = result;
        this.bet = bet;
        this.playerValue = playerValue;
        this.dealerValue = dealerValue;
        this.date = date;
    }

    public String getUsername() {
        return username;
    }

    public String getResult() {
        return result;
    }

    public double getBet() {
        return bet;
    }

    public int getPlayerValue() {
        return playerValue;
    }

    public int getDealerValue() {
        return dealerValue;
    }

    public String getDate() {
        return date;
    }
}

