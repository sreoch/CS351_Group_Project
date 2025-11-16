package shared;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Array;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server implements Runnable {
    private HashMap<String, User> users;
    private ServerSocket serverSocket;
    private int interestPeriod;
    private float interestRate;
    private ExecutorService threadPool;
    private Ledger ledger;
    private int port;

    public Server(int port, int threadCount) throws IOException {
        this.users = new HashMap<>();
        this.serverSocket = new ServerSocket(port);
        this.threadPool = Executors.newFixedThreadPool(threadCount);
        this.ledger = new Ledger();
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

    public User authenticateUser(String username, String password) {
        User user = users.get(username);
        if (user != null) {
            if (user.checkPassword(password)) {
                System.out.println("User is authenticated");
                return user;
            }
            return null;
        }
        return null;
    }

    public void writeAccountsToFile(){
        String directory = System.getProperty("user.dir");
        directory = (directory + "/src/FileData/Accounts.ser");
        System.out.println("DIRECTORY " + directory);

        System.out.println("serializing theData");
        try {
            FileOutputStream outputStream = new FileOutputStream(directory);
            ObjectOutputStream objectStream = new ObjectOutputStream(outputStream);
            for (User user : users.values()){
                if (!user.getAccounts().isEmpty()){
                    for(Account account : user.getAccounts()){
                        objectStream.writeObject(account);
                    }
                }
            }
            objectStream.close();
        }
        catch (Exception e) { e.printStackTrace(); }
    }

    public void writeMessageToLedger(Message message){
        String directory = System.getProperty("user.dir");
        directory = (directory + "/src/FileData/Ledger.ser");
        System.out.println("DIRECTORY " + directory);

        System.out.println("serializing theData");
        try {
            FileOutputStream outputStream = new FileOutputStream(directory);
            ObjectOutputStream objectStream = new ObjectOutputStream(outputStream);
            objectStream.writeObject(message);
            objectStream.close();
        }
        catch (Exception e) { e.printStackTrace(); }
    }

    public void readMessagesFromLedger() throws IOException {
        String directory = System.getProperty("user.dir");
        directory = (directory + "/src/FileData/Ledger.ser");
        System.out.println("DIRECTORY " + directory);

        System.out.println("deserializing theData");
        FileInputStream inputStream = new FileInputStream(directory);
        ObjectInputStream objectStream = new ObjectInputStream(inputStream);
        Object object = new Object();
        ArrayList<Message> messages = new ArrayList<>();
        while (true){
            try {
                object = objectStream.readObject();
                messages.add((Message)object);
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
        ArrayList<Message> orderedMessages = new ArrayList<>();

        while (!messages.isEmpty()){
            LocalDateTime minTime = LocalDateTime.now();
            int minIndex = 0;
            for (int i = 0; i < messages.size(); i++){
                if (minTime.isAfter(messages.get(i).getTimeStamp())){
                    minIndex = i;
                    minTime = messages.get(i).getTimeStamp();
                }
            }
            orderedMessages.add(messages.remove(minIndex));
        }

    }

    public void readAccountsFromFile() throws IOException {
        String directory = System.getProperty("user.dir");
        directory = (directory + "/src/FileData/Accounts.ser");
        System.out.println("DIRECTORY " + directory);

        System.out.println("deserializing theData");
        FileInputStream inputStream = new FileInputStream(directory);
        ObjectInputStream objectStream = new ObjectInputStream(inputStream);
        Object object = new Object();
        ArrayList<Account> accounts = new ArrayList<>();
        while (true){
            try {
                object = objectStream.readObject();
                accounts.add((Account)object);
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
        for (Account account : accounts){
            if (!users.containsValue(account.getUser())){
                users.put(account.getUser().getUsername(), account.getUser());
            }
        }
    }

    public synchronized boolean addNewUser(String username, String password) {
        System.out.println("Adding new user");
        if (users.get(username) != null) {
            System.out.println("User already exists");
            return false;
        }
        User newUser = new User(username, password);
        users.put(username, newUser);
        System.out.println("New user added");
        return true;
    }

    public boolean transfer(Account sender, Account recipient, double amount) {
        if (amount > sender.getBalance()) {
            System.out.println("Amount is greater than the balance");
            return false;
        }

        Transaction transaction = new Transaction(sender, recipient, amount, TransactionType.TRANSFER);
        sender.deductBalance(amount);
        recipient.addBalance(amount);
        return true;
    }

//    public boolean deposit(Account recipient, double amount) {
//        Transaction
//    }

    public static void main(String[] args) {
        try {
            Server server = new Server(9000, 10);
            User user1 = new User("Jack", "password1");
            User user2 = new User("Fraser", "12345");
            User user3 = new User("Scott", "CS351");

            user1.addAccount(new Account(user1));
            user2.addAccount(new Account(user2));
            user3.addAccount(new Account(user3));

            server.users.put(user1.getUsername(), user1);
            server.users.put(user2.getUsername(), user2);
            server.users.put(user3.getUsername(), user3);

            server.writeAccountsToFile();
            server.readAccountsFromFile();
            server.run();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
