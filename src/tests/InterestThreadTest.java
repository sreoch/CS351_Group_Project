package tests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.Server;
import shared.Account;
import shared.Transaction;
import shared.TransactionType;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class InterestThreadTest {
    private Server server;
    private Account testAccount;

    @BeforeEach
    public void setup() throws IOException {
        server = new Server(9004, 10);
        server.addNewAccount("testuser", "password");
        testAccount = server.getAccount("testuser");
    }

    @Test
    public void testInterestRecordedInLedger() {
        server.addInterest(testAccount, 0.025);

        List<Transaction> transactions = server.getLedger().getAllTransactions();

        assertEquals(1, transactions.size());
        assertEquals(TransactionType.INTEREST, transactions.get(0).getType());
        assertEquals(25.0, transactions.getFirst().getAmount(), 0.01);
        assertNull(transactions.getFirst().getFromAccount());
        assertEquals(testAccount, transactions.get(0).getToAccount());
    }
}
