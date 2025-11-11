package shared;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

public class Transaction implements Serializable {
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
        return String.format("[%s] %s -> %s: £%.2f (%s)", timestamp, fromAccount, toAccount, amount, type);
    }
}
