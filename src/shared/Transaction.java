package shared;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

public class Transaction implements Serializable, Comparable {
    private Account fromAccount;
    private Account toAccount;
    private double amount;
    private TransactionType type;
    private LocalDateTime timestamp;

    public Transaction(Account fromAccount, Account toAccount, double amount, TransactionType type) {
        this.fromAccount = fromAccount;
        this.toAccount = toAccount;
        this.amount = amount;
        this.type = type;
        this.timestamp = LocalDateTime.now();
    }

    public Account getFromAccount() {
        return fromAccount;
    }

    public Account getToAccount() {
        return toAccount;
    }

    public double getAmount() {
        return amount;
    }

    public TransactionType getType() {
        return type;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        String fromAccountString = fromAccount != null ? fromAccount.toString() : "null";
        String toAccountString = toAccount != null ? toAccount.toString() : "null";
        return String.format("[%s] %s -> %s: £%.2f (%s)", timestamp, fromAccountString, toAccountString, amount, type);
    }

    @Override
    public int compareTo(Object o) {
        Transaction other = (Transaction) o;
        return this.getTimestamp().compareTo(other.getTimestamp());
    }
}
