package shared;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class TransactionLedger implements Serializable {
    private List<Transaction> transactions;

    public TransactionLedger() {
        transactions = new ArrayList<Transaction>();
    }

    public synchronized void addTransaction(Transaction t) {
        transactions.add(t);
    }

    public synchronized List<Transaction> getAllTransactions() {
        return new ArrayList<>(transactions);
    }

    public synchronized List<Transaction> getAllTransactionsForUser(String username) {
        List<Transaction> userTransactions = new ArrayList<>();
        for (Transaction t : transactions) {
            if (t.getFromAccount() != null && t.getFromAccount().getUsername().equals(username)) {
                userTransactions.add(t);
            } else if (t.getToAccount() != null && t.getToAccount().getUsername().equals(username)) {
                userTransactions.add(t);
            }
        }
        return userTransactions;
    }
}
