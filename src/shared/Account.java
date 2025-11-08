package shared;

import java.io.Serializable;

public class Account implements Serializable {
    private String username;
    private String passwordHash;
    private double balance;

    public Account(String username, String password) {
        this.username = username;
        this.passwordHash = password; //TODO: need to hash properly
        this.balance = 1000.0; // the starting balance is always 1000
    }

    public String getUsername() {
        return username;
    }

    public double getBalance() {
        return balance;
    }

    public synchronized void addBalance(double amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Cannot add negative amount");
        }
        this.balance += amount;
    }

    public synchronized boolean deductBalance(double amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Cannot deduct negative amount");
        }

        if (balance >= amount) {
            balance -= amount;
            return true;
        }
        return false;
    }

    public boolean checkPassword(String password) {
        return this.passwordHash.equals(password);
    }
}
