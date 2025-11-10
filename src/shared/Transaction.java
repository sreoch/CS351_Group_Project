package shared;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Transaction implements Serializable {
    private String fromAccount;
    private String toAccount;
    private double amount;
    private TransactionType type;
    private LocalDateTime timestamp;

    public Transaction(String fromAccount, String toAccount, double amount, TransactionType type) {
        this.fromAccount = fromAccount;
        this.toAccount = toAccount;
        this.amount = amount;
        this.type = type;
        this.timestamp = LocalDateTime.now();
    }

    public String getFromAccount() {
        return fromAccount;
    }

    public String getToAccount() {
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
