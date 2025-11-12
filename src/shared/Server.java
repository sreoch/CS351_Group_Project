package shared;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server implements Runnable {
    private HashMap<String, Account> accounts;
    private HashMap<String, User> users;
    private ServerSocket serverSocket;
    private int interestPeriod;
    private float interestRate;
    private ExecutorService threadPool;
    private Ledger ledger;
    private int port;

    public Server(int port, int threadCount) throws IOException {
        this.accounts = new HashMap<>();
        this.users = new HashMap<>();
        this.serverSocket = new ServerSocket(port);
        this.threadPool = Executors.newFixedThreadPool(threadCount);
        this.ledger = new Ledger();
    }

    public void createLedgerMessage(Transaction transaction) {
        ledger.addToLedger(transaction);
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
            if (password == user.getPassword()) {
                return user;
            }
        }
        return null;
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

    public static void main(String[] args) {
        try {
            Server server = new Server(9000, 10);
            server.run();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
