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

    public synchronized ArrayList<Transaction> getAccountTransactions(Account account) {
//        System.out.println("Looking for transactions");
//        Stream<Transaction> filteredTransactions = transactions.stream().filter(
//                transaction -> (transaction.getFromAccount().getUsername().equals(account.getUsername()) || transaction.getToAccount().getUsername().equals(account.getUsername()))
//        );
//        System.out.println("Done!");
//        ArrayList<Transaction> blah = new ArrayList<Transaction>(filteredTransactions.collect(Collectors.toList()));
//        System.out.println("Finished collecting");
//        return blah;
        ArrayList<Transaction> filteredTransactions = new ArrayList<>();
        for (Transaction transaction : transactions) {
            System.out.println("Transaction");
            System.out.println(transaction.getFromAccount());
            System.out.println(transaction.getToAccount());
            System.out.println(transaction.getFromAccount() == account || transaction.getToAccount() == account);
            if (transaction.getFromAccount() == account || transaction.getToAccount() == account) {
                System.out.println("Adding transaction");
                filteredTransactions.add(transaction);
                System.out.println("Added transaction");
            }
            System.out.println("Done loop");
        }
        System.out.println("Returning filteredTransactions");
        return filteredTransactions;
    }
}
