package tests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import shared.Account;
import shared.Transaction;
import shared.TransactionType;

import static org.junit.jupiter.api.Assertions.*;

public class TransactionTest {
    private Account scottAccount;
    private Account jackAccount;
    private Account fraserAccount;
    private Account systemAccount;

    @BeforeEach
    public void setup() {
        scottAccount = new Account("scott", "password123");
        jackAccount = new Account("jack", "password123");
        fraserAccount = new Account("fraser", "password123");
        systemAccount = new Account("SYSTEM", "systempass");
    }

    @Test
    public void testTransaction() {
        Transaction t = new Transaction(scottAccount, jackAccount, 100.0, TransactionType.TRANSFER);

        assertEquals(scottAccount, t.getFromAccount());
        assertEquals(jackAccount, t.getToAccount());
        assertEquals(100.0, t.getAmount(), 0.01);
        assertEquals(TransactionType.TRANSFER, t.getType());
        assertNotNull(t.getTimestamp());
    }

    @Test
    public void testDepositTransaction() {
        Transaction t = new Transaction(null, jackAccount, 100.0, TransactionType.DEPOSIT);

        assertNull(t.getFromAccount());
        assertEquals(jackAccount, t.getToAccount());
        assertEquals(100.0, t.getAmount(), 0.01);
        assertEquals(TransactionType.DEPOSIT, t.getType());
    }

    @Test
    public void testWithdrawTransaction() {
        Transaction t = new Transaction(jackAccount, null, 75.0, TransactionType.WITHDRAW);

        assertEquals(jackAccount, t.getFromAccount());
        assertNull(t.getToAccount());
        assertEquals(75.0, t.getAmount(), 0.01);
        assertEquals(TransactionType.WITHDRAW, t.getType());
    }

    @Test
    public void testInterestTransaction() {
        Transaction t = new Transaction(systemAccount, fraserAccount, 100.0, TransactionType.INTEREST);

        assertEquals(systemAccount, t.getFromAccount());
        assertEquals(fraserAccount,  t.getToAccount());
        assertEquals(100.0, t.getAmount(), 0.01);
        assertEquals(TransactionType.INTEREST, t.getType());
    }

    @Test
    public void testTimestampExists() {
        Transaction t =  new Transaction(jackAccount, fraserAccount, 100.0, TransactionType.TRANSFER);

        assertNotNull(t.getTimestamp());
    }

}
