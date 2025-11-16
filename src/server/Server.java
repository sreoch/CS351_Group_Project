package server;

import shared.*;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server implements Runnable {
    private HashMap<String, Account> accounts;
    private ServerSocket serverSocket;
    private int interestPeriod;
    private float interestRate;
    private ExecutorService threadPool;
    private TransactionLedger ledger;
    private int port;

    public Server(int port, int threadCount) throws IOException {
        this.accounts = new HashMap<>();
        this.serverSocket = new ServerSocket(port);
        this.threadPool = Executors.newFixedThreadPool(threadCount);
        this.ledger = new TransactionLedger();
    }
    public TransactionLedger getLedger() {
        return ledger;
    }

    @Override
    public void run() {
        System.out.println("Server running...");
        while (true) {
            try {
                Socket socket = serverSocket.accept();
                System.out.println("passing connection to client handler");
                threadPool.submit(new ClientHandler(socket, this));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public Account authenticateAccount(String username, String password) {
        Account account = accounts.get(username);
        if (account != null) {
            if (account.checkPassword(password)) {
                System.out.println("User is authenticated");
                return account;
            }
            return null;
        }
        return null;
    }

    public synchronized boolean addNewAccount(String username, String password) {
        System.out.println("Adding new user");
        if (accounts.get(username) != null) {
            System.out.println("User already exists");
            return false;
        }
        Account newAccount = new Account(username, password);
        accounts.put(username, newAccount);
        System.out.println("New user added");
        return true;
    }

    public Account getAccount(String username) {
        return accounts.get(username);
    }

    public boolean transfer(Account sender, Account recipient, double amount) {
        if (amount > sender.getBalance()) {
            System.out.println("Amount is greater than the balance");
            return false;
        }

        Transaction transaction = new Transaction(sender, recipient, amount, TransactionType.TRANSFER);
        ledger.addTransaction(transaction);
        sender.deductBalance(amount);
        recipient.addBalance(amount);
        return true;
    }

    public boolean deposit(Account recipient, double amount) {
        Transaction transaction = new Transaction(null, recipient, amount, TransactionType.DEPOSIT);
        recipient.addBalance(amount);
        ledger.addTransaction(transaction);
        return true;
    }

    public boolean withdraw(Account source, double amount) {
        Transaction transaction = new Transaction(source, null, amount, TransactionType.WITHDRAW);
        if (amount > source.getBalance()) {
            return false;
        }
        source.deductBalance(amount);
        ledger.addTransaction(transaction);
        return true;
    }

    public static void main(String[] args) {
        try {
            Server server = new Server(9000, 10);
            server.run();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
