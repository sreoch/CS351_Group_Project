package tests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import shared.Account;
import shared.Transaction;
import shared.TransactionLedger;
import shared.TransactionType;

import java.util.List;

public class TransactionLedgerTest {
    private TransactionLedger ledger;
    private Account scottAccount;
    private Account jackAccount;
    private Account fraserAccount;

    @BeforeEach
    public void setUp() {
        ledger = new TransactionLedger();
        scottAccount = new Account("scott", "password123");
        jackAccount = new Account("jack", "password123");
        fraserAccount = new Account("fraser", "password123");
    }

    @Test
    public void testAddTransaction() {
        Transaction t = new Transaction(scottAccount, jackAccount, 100, TransactionType.TRANSFER);
        ledger.addTransaction(t);

        List<Transaction> all = ledger.getAllTransactions();
        assertEquals(1, all.size());
        assertEquals(t, all.get(0));
    }

    @Test
    public void testAddMultipleTransactions() {
        Transaction t1 = new Transaction(scottAccount, jackAccount, 100, TransactionType.TRANSFER);
        Transaction t2 = new Transaction(jackAccount, fraserAccount, 200, TransactionType.TRANSFER);
        Transaction t3 = new Transaction(null, scottAccount, 300, TransactionType.DEPOSIT);

        ledger.addTransaction(t1);
        ledger.addTransaction(t2);
        ledger.addTransaction(t3);

        assertEquals(3, ledger.getAllTransactions().size());
    }

    @Test
    public void testGetTransactionsForUser() {
        Transaction t1 = new Transaction(scottAccount, jackAccount, 100, TransactionType.TRANSFER);
        Transaction t2 = new Transaction(jackAccount, fraserAccount, 200, TransactionType.TRANSFER);
        Transaction t3 = new Transaction(null, scottAccount, 300, TransactionType.DEPOSIT);
        Transaction t4 = new Transaction(fraserAccount, jackAccount, 200, TransactionType.TRANSFER);

        ledger.addTransaction(t1);
        ledger.addTransaction(t2);
        ledger.addTransaction(t3);
        ledger.addTransaction(t4);

        List<Transaction> scottTransactions = ledger.getAllTransactionsForUser("scott");
        assertEquals(2, scottTransactions.size());

        List<Transaction> jackTransactions = ledger.getAllTransactionsForUser("jack");
        assertEquals(3, jackTransactions.size());

        List<Transaction> fraserTransactions = ledger.getAllTransactionsForUser("fraser");
        assertEquals(2, fraserTransactions.size());
    }

    @Test
    public void testGetTransactionsForUserWithNoTransactions() {
        List<Transaction> transactions = ledger.getAllTransactionsForUser("scott");
        assertEquals(0, transactions.size());
    }

}