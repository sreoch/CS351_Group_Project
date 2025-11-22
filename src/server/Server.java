package server;

import shared.*;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.*;

public class Server implements Runnable {
    private ConcurrentHashMap<String, Account> accounts;
    private ServerSocket serverSocket;
    private ExecutorService threadPool;
    private ScheduledExecutorService scheduledThreadPool;
    private InterestThread interestThread;
    private TransactionLedger ledger;
    private double interestRate;
    private int interestPeriod;
    private ScheduledFuture<?> interestTask;

    public Server(int port, int threadCount) throws IOException {
        this.accounts = new ConcurrentHashMap<>();
        this.serverSocket = new ServerSocket(port);
        this.threadPool = Executors.newFixedThreadPool(threadCount);
        this.scheduledThreadPool = Executors.newScheduledThreadPool(1);
        this.interestRate = Constants.DEFAULT_INTEREST_RATE;
        this.interestPeriod = Constants.DEFAULT_INTEREST_PERIOD_SECONDS;
        this.interestThread = new InterestThread(this, interestRate);
        this.ledger = new TransactionLedger();
    }
    public TransactionLedger getLedger() {
        return ledger;
    }

    @Override
    public void run() {
        System.out.println("Server running on port " + serverSocket.getLocalPort());
        System.out.println("Starting Interest Thread (rate: " + (interestRate * 100) + "%, period: " + interestPeriod + " seconds)...");

        interestTask = scheduledThreadPool.scheduleAtFixedRate(interestThread, interestPeriod, interestPeriod, TimeUnit.SECONDS);

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

    public boolean addNewAccount(String username, String password) {
        System.out.println("Adding new user");
        Account newAccount = new Account(username, password);
        Account existing = accounts.putIfAbsent(username, newAccount);
        if (existing != null) {
            System.out.println("User already exists");
            return false;
        }
        System.out.println("New user added");
        return true;
    }

    public Account getAccount(String username) {
        return accounts.get(username);
    }

    public ConcurrentHashMap<String, Account> getAccounts() {
        return this.accounts;
    }

    public synchronized boolean transfer(Account sender, Account recipient, double amount) {
        if (amount > sender.getBalance()) {
            System.out.println("Amount is greater than the balance");
            return false;
        }

        sender.deductBalance(amount);
        recipient.addBalance(amount);

        Transaction transaction = new Transaction(sender, recipient, amount, TransactionType.TRANSFER);
        ledger.addTransaction(transaction);
        return true;
    }

    public synchronized boolean deposit(Account recipient, double amount) {
        recipient.addBalance(amount);
        Transaction transaction = new Transaction(null, recipient, amount, TransactionType.DEPOSIT);
        ledger.addTransaction(transaction);
        return true;
    }

    public synchronized boolean withdraw(Account source, double amount) {
        if (amount > source.getBalance()) {
            return false;
        }

        source.deductBalance(amount);

        Transaction transaction = new Transaction(source, null, amount, TransactionType.WITHDRAW);
        ledger.addTransaction(transaction);
        return true;
    }

    public void addInterest(Account account, double rate) {
        double interest = Math.round(account.getBalance() * rate * 100d) / 100d;
        Transaction interestTransaction = new Transaction(null, account, interest, TransactionType.INTEREST);
        ledger.addTransaction(interestTransaction);
        account.addBalance(interest);
    }

    public void updateInterestRate(double rate) {
        this.interestRate = rate;
        this.interestThread.setRate(rate);
    }

    public void updateInterestPeriod(int period) {
        this.interestPeriod = period;

        if (interestTask != null) {
            interestTask.cancel(false);
        }

        interestTask = scheduledThreadPool.scheduleAtFixedRate(interestThread, interestPeriod, interestPeriod, TimeUnit.SECONDS);

        System.out.println("Interest period updated to " + period + " seconds");
    }
    public void writeAccountsToFile(){
        String directory = System.getProperty("user.dir");
        directory = (directory + "/src/FileData/Accounts.ser");
        System.out.println("DIRECTORY " + directory);

        System.out.println("serializing theData");
        try {
            FileOutputStream outputStream = new FileOutputStream(directory, false);
            ObjectOutputStream objectStream = new ObjectOutputStream(outputStream);
            for (Account acc : accounts.values()) {
                objectStream.writeObject(acc);
            }
            objectStream.close();
            outputStream.close();
        }
        catch (Exception e) { e.printStackTrace(); }
    }

    public void readAccountsFromFile() throws IOException {
        String directory = System.getProperty("user.dir");
        directory = (directory + "/src/FileData/Accounts.ser");
        System.out.println("DIRECTORY " + directory);

        System.out.println("deserializing theData");
        FileInputStream inputStream = new FileInputStream(directory);
        ObjectInputStream objectStream = new ObjectInputStream(inputStream);
        Object object = new Object();
        while (true) {
            try {
                object = objectStream.readObject();
                if (!accounts.containsKey(((Account)object).getUsername())){
                    accounts.put(((Account)object).getUsername(), (Account) object);
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
    }

    public void writeTransactionsToFile(TransactionLedger ledger){
        String directory = System.getProperty("user.dir");
        directory = (directory + "/src/FileData/Ledger.ser");
        System.out.println("DIRECTORY " + directory);

        System.out.println("serializing theData");
        try {
            FileOutputStream outputStream = new FileOutputStream(directory);
            ObjectOutputStream objectStream = new ObjectOutputStream(outputStream);
            for (Transaction transaction : ledger.getAllTransactions()){
                objectStream.writeObject(transaction);
            }
            objectStream.close();
            outputStream.close();
        }
        catch (Exception e) { e.printStackTrace(); }
    }

    public void readTransactionsFromFile() throws IOException {
        String directory = System.getProperty("user.dir");
        directory = (directory + "/src/FileData/Ledger.ser");
        System.out.println("DIRECTORY " + directory);

        System.out.println("deserializing theData");
        FileInputStream inputStream = new FileInputStream(directory);
        ObjectInputStream objectStream = new ObjectInputStream(inputStream);
        Object object = new Object();
        ArrayList<Transaction> transactions = new ArrayList<>();
        while (true){
            try {
                object = objectStream.readObject();
                transactions.add((Transaction)object);
            }
            catch (EOFException | FileNotFoundException e) {
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
        ArrayList<Transaction> orderedMessages = new ArrayList<>();

        while (!transactions.isEmpty()){
            LocalDateTime minTime = LocalDateTime.now();
            int minIndex = 0;
            for (int i = 0; i < transactions.size(); i++){
                if (minTime.isAfter(transactions.get(i).getTimestamp())){
                    minIndex = i;
                    minTime = transactions.get(i).getTimestamp();
                }
            }
            orderedMessages.add(transactions.remove(minIndex));
        }

    }


    public static void main(String[] args) {
        try {
            Server server = new Server(Constants.DEFAULT_PORT, Constants.DEFAULT_THREAD_POOL_SIZE);
            server.run();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
