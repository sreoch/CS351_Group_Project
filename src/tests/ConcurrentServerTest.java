package tests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.Server;
import shared.Account;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ConcurrentServerTest {
    private Server server;
    private Account account1;
    private Account account2;
    private Account account3;

    @BeforeEach
    public void setUp() throws IOException {
        server = new Server(9001, 20);
        server.addNewAccount("user1", "password1");
        server.addNewAccount("user2", "password2");
        server.addNewAccount("user3", "password3");

        account1 = server.getAccount("user1");
        account2 = server.getAccount("user2");
        account3 = server.getAccount("user3");
    }

    /**
     * this test verifies that concurrent transfers maintain money conservation
     * tests that when 100 threads transfer £1 at the same time, exactly £100
     * is transferred with no money lost or created due to race conditions
     */
    @Test
    public void testConcurrentTransfers_NoRaceCondition() throws InterruptedException {
        int numThreads = 100;
        double transferAmount = 1.0;

        double initialBalance1 = account1.getBalance();
        double initialBalance2 = account2.getBalance();

        ExecutorService executor = Executors.newFixedThreadPool(10);
        CountDownLatch latch = new CountDownLatch(numThreads);

        for (int i = 0; i < numThreads; i++) {
            executor.submit(() -> {
                try {
                    server.transfer(account1, account2, transferAmount);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(10, TimeUnit.SECONDS);
        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.SECONDS);

        double expectedBalance1 = initialBalance1 - (numThreads * transferAmount);
        double expectedBalance2 = initialBalance2 + (numThreads * transferAmount);

        assertEquals(expectedBalance1, account1.getBalance(), 0.01, "Account1 balance should be exactly " + expectedBalance1);
        assertEquals(expectedBalance2, account2.getBalance(), 0.01, "Account2 balance should be exactly " + expectedBalance2);

        double totalMoney = account1.getBalance() + account2.getBalance();
        double expectedTotal = initialBalance1 + initialBalance2;
        assertEquals(expectedTotal, totalMoney, 0.01, "Total money in system should be exactly " + expectedTotal);
    }

    /**
     * this test verifies that concurrent withdrawals never cause overdrafts
     * it tests that when 100 threads try to withdraw £20 from an account with £1000
     * the balance never goes negative
     */
    @Test
    public void testConcurrentWithdrawals_NoOverdraft() throws InterruptedException {
        int numThreads = 100;
        double withdrawAmount = 20.0;

        ExecutorService executor = Executors.newFixedThreadPool(100);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch endLatch = new CountDownLatch(numThreads);
        List<Boolean> results = new CopyOnWriteArrayList<>();

        for (int i = 0; i < numThreads; i++) {
            executor.submit(() -> {
                try {
                    startLatch.await();
                    boolean result = server.withdraw(account1, withdrawAmount);
                    results.add(result);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    endLatch.countDown();
                }
            });
        }

        Thread.sleep(100);

        startLatch.countDown();

        endLatch.await(10, TimeUnit.SECONDS);
        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.SECONDS);

        double finalBalance = account1.getBalance();
        long successfulWithdrawals = results.stream().filter(r -> r).count();
        double expectedBalance = 1000 - (successfulWithdrawals * withdrawAmount);

        System.out.println("Final balance: £" + finalBalance);
        System.out.println("Expected balance: £" + expectedBalance);
        System.out.println("Successful withdrawals: " + successfulWithdrawals + " out of " + numThreads);

        assertTrue(finalBalance >= 0);

        assertEquals(expectedBalance, finalBalance, 0.01);
    }

    /**
     * test verifies that the transaction ledger records all concurrent operations
     * tests that when multiple threads perform operations simultaneously, no transactions
     * are lost due to race conditions in the ledger.
     */
    @Test
    public void testConcurrentOperations_LedgerAccuracy() throws InterruptedException {
        int numDeposits = 50;
        int numWithdrawals = 50;

        ExecutorService executor = Executors.newFixedThreadPool(20);
        CountDownLatch latch = new CountDownLatch(numDeposits + numWithdrawals);

        for (int i = 0; i < numDeposits; i++) {
            executor.submit(() -> {
                try {
                    server.deposit(account1, 10.0);
                } finally {
                    latch.countDown();
                }
            });
        }

        for (int i = 0; i < numWithdrawals; i++) {
            executor.submit(() -> {
                try {
                    server.withdraw(account1, 5.0);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(10, TimeUnit.SECONDS);
        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.SECONDS);

        List<shared.Transaction> transactions = server.getLedger().getAllTransactionsForUser("user1");
        assertEquals(numDeposits + numWithdrawals, transactions.size());
    }
}