package test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.Server;
import shared.*;

import java.io.*;
import java.security.KeyException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.*;

public class ServerTest {
    @Test
    public void testWriteAccountsToFile() throws IOException {
        Server server = new Server(Constants.DEFAULT_PORT, Constants.DEFAULT_THREAD_POOL_SIZE);

        server.addNewAccount("Reoch123", "securestpassword123");
        server.addNewAccount("Nixon123", "abcdefghijklmnopqrstuvwxyz");
        server.addNewAccount("Bray123", "1234");

        server.writeAccountsToFile();

        String directory = System.getProperty("user.dir");
        directory = (directory + "/src/FileData/Accounts.ser");
        System.out.println("DIRECTORY " + directory);

        System.out.println("deserializing theData");
        FileInputStream inputStream = new FileInputStream(directory);
        ObjectInputStream objectStream = new ObjectInputStream(inputStream);
        Object object = new Object();
        ArrayList<Account> accounts = new ArrayList<>();
        while (true) {
            try {
                object = objectStream.readObject();
                Account currentAccount = (Account) object;
                accounts.add(currentAccount);
                String accountName = currentAccount.getUsername();
                ConcurrentHashMap<String, Account> serverAccounts = server.getAccounts();
                try {
                    serverAccounts.get(accountName);
                } catch (Exception e) {
                    fail();
                }
            } catch (EOFException | FileNotFoundException e) {
                break;
            } catch (IOException e) {
                objectStream.close();
                throw new RuntimeException(e);
            } catch (ClassNotFoundException e) {
                objectStream.close();
                throw new RuntimeException(e);
            }
        }
        objectStream.close();
        inputStream.close();

        assertEquals(server.getAccounts().size(), accounts.size());
    }

    @Test
    public void testReadAccountsFromFile() throws IOException {
        Server server = new Server(Constants.DEFAULT_PORT, Constants.DEFAULT_THREAD_POOL_SIZE);

        server.addNewAccount("Reoch123", "securestpassword123");
        server.addNewAccount("Nixon123", "abcdefghijklmnopqrstuvwxyz");
        server.addNewAccount("Bray123", "1234");

        String directory = System.getProperty("user.dir");
        directory = (directory + "/src/FileData/Accounts.ser");
        System.out.println("DIRECTORY " + directory);

        System.out.println("serializing theData");
        try {
            FileOutputStream outputStream = new FileOutputStream(directory);
            ObjectOutputStream objectStream = new ObjectOutputStream(outputStream);
            for (Account acc : server.getAccounts().values()) {
                objectStream.writeObject(acc);
            }
            objectStream.close();
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }


        Server server2 = new Server(8999, Constants.DEFAULT_THREAD_POOL_SIZE);
        server2.readAccountsFromFile();

        ConcurrentHashMap<String, Account> server2Accounts = server2.getAccounts();
        ConcurrentHashMap<String, Account> serverAccounts = server.getAccounts();

        for (String accountName : server2Accounts.keySet()) {
            try {
                serverAccounts.get(accountName);
            } catch (Exception e) {
                fail();
            }
        }

        assertEquals(3, server2.getAccounts().size());
    }

    @Test
    public void testWriteTransactionToLedger() throws IOException {
        Server server = new Server(Constants.DEFAULT_PORT, Constants.DEFAULT_THREAD_POOL_SIZE);

        Account account1 = new Account("Reoch123", "securestpassword123");
        Account account2 = new Account("Nixon123", "abcdefghijklmnopqrstuvwxyz");
        Account account3 = new Account("Bray123", "1234");

        TransactionLedger ledger = server.getLedger();
        ledger.addTransaction(new Transaction(account2, account1, 400, TransactionType.TRANSFER));
        ledger.addTransaction(new Transaction(account3, account1, 400, TransactionType.TRANSFER));
        ledger.addTransaction(new Transaction(account2, account1, 200, TransactionType.TRANSFER));

        server.writeTransactionsToFile(ledger);

        String directory = System.getProperty("user.dir");
        directory = (directory + "/src/FileData/Ledger.ser");
        System.out.println("DIRECTORY " + directory);

        System.out.println("deserializing theData");
        FileInputStream inputStream = new FileInputStream(directory);
        ObjectInputStream objectStream = new ObjectInputStream(inputStream);
        Object object = new Object();
        ArrayList<Transaction> transactions = new ArrayList<>();
        while (true) {
            try {
                object = objectStream.readObject();
                transactions.add((Transaction) object);
            } catch (EOFException | FileNotFoundException e) {
                break;
            } catch (IOException e) {
                objectStream.close();
                throw new RuntimeException(e);
            } catch (ClassNotFoundException e) {
                objectStream.close();
                throw new RuntimeException(e);
            }
        }
        objectStream.close();
        inputStream.close();

        assertEquals(transactions.size(), ledger.getAllTransactions().size());
    }

    @Test
    public void testReadTransactionsFromLedger() throws IOException {
        Server server = new Server(Constants.DEFAULT_PORT, Constants.DEFAULT_THREAD_POOL_SIZE);

        Account account1 = new Account("Reoch123", "securestpassword123");
        Account account2 = new Account("Nixon123", "abcdefghijklmnopqrstuvwxyz");
        Account account3 = new Account("Bray123", "1234");

        TransactionLedger ledger = server.getLedger();
        ledger.addTransaction(new Transaction(account2, account1, 400, TransactionType.TRANSFER));
        ledger.addTransaction(new Transaction(account3, account1, 400, TransactionType.TRANSFER));
        ledger.addTransaction(new Transaction(account2, account1, 200, TransactionType.TRANSFER));

        String directory = System.getProperty("user.dir");
        directory = (directory + "/src/FileData/Ledger.ser");
        System.out.println("DIRECTORY " + directory);

        System.out.println("serializing theData");
        try {
            FileOutputStream outputStream = new FileOutputStream(directory, false);
            ObjectOutputStream objectStream = new ObjectOutputStream(outputStream);
            for (Transaction transaction : ledger.getAllTransactions()) {
                objectStream.writeObject(transaction);
            }
            objectStream.close();
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        server.readTransactionsFromFile();

        assertEquals(ledger.getAllTransactions().size(), server.getLedger().getAllTransactions().size());
    }
}