package shared;

import java.sql.Array;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Ledger {
    private ArrayList<Transaction> transactions;

    public Ledger() {
        this.transactions = new ArrayList<>();
    }

    public synchronized void addToLedger(Transaction transaction) {
        transactions.add(transaction);
    }

    public ArrayList<Transaction> getLedgerData() {
        return transactions;
    }

    public ArrayList<Transaction> getUserTransactions(String username) {
        Stream<Transaction> filteredTransactions = transactions.stream().filter(
                transaction -> (transaction.getFromAccount().getUser().getUsername().equals(username) || transaction.getToAccount().getUser().getUsername().equals(username))
        );

        return new ArrayList<Transaction>(filteredTransactions.collect(Collectors.toList()));
    }
}
