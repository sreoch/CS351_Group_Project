package shared;

import java.io.Serializable;

public class Account implements Serializable {
    private String id;
    private String username;
    private String password;
    private boolean online;
    private double balance;

    public Account(String username, String password) {
        this.balance = 1000.0; // the starting balance is always 1000
        this.username = username;
        this.password = password;
        this.online = false;
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public boolean checkPassword(String password) {
        return password.equals(this.password);
    }
}
