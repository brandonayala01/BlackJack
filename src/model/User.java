
package model;

public class User {

    public static final double INITIAL_BALANCE = 1000;

    private int id;
    private String username;
    private String email;
    private String password;
    private double balance;

    public User(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.balance = INITIAL_BALANCE;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public double getBalance() {
        return balance;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void addBalance(double amount) {
        if (!Double.isFinite(amount) || amount <= 0) {
            return;
        }

        double newBalance = balance + amount;

        if (Double.isFinite(newBalance)) {
            balance = newBalance;
        }
    }

    public boolean removeBalance(double amount) {
        if (!Double.isFinite(amount)
                || amount <= 0
                || amount > balance) {
            return false;
        }

        balance -= amount;
        return true;
    }

    public void resetBalance() {
        balance = INITIAL_BALANCE;
    }
}
