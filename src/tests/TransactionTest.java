package tests;

import org.junit.jupiter.api.Test;
import shared.Transaction;
import shared.TransactionType;

import static org.junit.jupiter.api.Assertions.*;

public class TransactionTest {

    @Test
    public void testTransaction() {
        Transaction t = new Transaction("scott", "jack", 100.0, TransactionType.TRANSFER);

        assertEquals("scott", t.getFromAccount());
        assertEquals("jack", t.getToAccount());
        assertEquals(100.0, t.getAmount(), 0.01);
        assertEquals(TransactionType.TRANSFER, t.getType());
        assertNotNull(t.getTimestamp());
    }

    @Test
    public void testDepositTransaction() {
        Transaction t = new Transaction(null, "jack", 100.0, TransactionType.DEPOSIT);

        assertNull(t.getFromAccount());
        assertEquals("jack", t.getToAccount());
        assertEquals(100.0, t.getAmount(), 0.01);
        assertEquals(TransactionType.DEPOSIT, t.getType());
    }

    @Test
    public void testWithdrawTransaction() {
        Transaction t = new Transaction("jack", null, 75.0, TransactionType.WITHDRAW);

        assertEquals("jack", t.getFromAccount());
        assertNull(t.getToAccount());
        assertEquals(75.0, t.getAmount(), 0.01);
        assertEquals(TransactionType.WITHDRAW, t.getType());
    }

    @Test
    public void testInterestTransaction() {
        Transaction t = new Transaction("SYSTEM", "fraser", 100.0, TransactionType.INTEREST);

        assertEquals("SYSTEM", t.getFromAccount());
        assertEquals("fraser",  t.getToAccount());
        assertEquals(100.0, t.getAmount(), 0.01);
        assertEquals(TransactionType.INTEREST, t.getType());
    }

    @Test
    public void testTimestampExists() {
        Transaction t =  new Transaction("jack", "fraser", 100.0, TransactionType.TRANSFER);

        assertNotNull(t.getTimestamp());
    }

}
