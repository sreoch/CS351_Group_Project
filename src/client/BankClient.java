package client;

import shared.Message;
import shared.MessageType;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.sql.SQLOutput;
import java.util.Scanner;

public class BankClient {
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private Scanner scanner;
    private String username;

    public static void main(String[] args) {
        BankClient client = new BankClient();
        try {
            client.connect("localhost", 9000);
            client.login();

            client.showMenu();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    private void connect(String host, int port) throws IOException {
        socket = new Socket(host, port);
        out = new ObjectOutputStream(socket.getOutputStream());
        in = new ObjectInputStream(socket.getInputStream());
        scanner = new Scanner(System.in);
        System.out.println("Connected to the server");
    }

    private void login() throws IOException, ClassNotFoundException {
        System.out.println("\n--- Banking System ---");
        System.out.println("1. Login");
        System.out.println("2. Create Account");
        System.out.println("Choice: ");

        int choice = scanner.nextInt();
        scanner.nextLine();

        System.out.println("Username: ");
        username = scanner.nextLine();
        System.out.println("Password: ");
        String password = scanner.nextLine();

        MessageType type = (choice == 1) ? MessageType.LOGIN : MessageType.CREATE_ACCOUNT;
        Message msg = new Message(type, username + ":" + password);
        out.writeObject(msg);

        Message response = (Message) in.readObject();

        if (response.getType() == MessageType.LOGIN_SUCCESS) {
            System.out.println("Success! Balance: £" + response.getPayload());
        } else {
            System.out.println("x " + response.getPayload());
            System.exit(0);
        }
    }

    private void showMenu() throws IOException, ClassNotFoundException {
        while (true) {
            System.out.println("\n--- Main Menu ---");
            System.out.println("1. Transfer");
            System.out.println("2. Deposit");
            System.out.println("3. Withdraw");
            System.out.println("4. View Transactions");
            System.out.println("5. Logout");
            System.out.print("Choice: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1: handleTransfer(); break;
                case 2: handleDeposit(); break;
                case 3: handleWithdraw(); break;
                case 4: handleViewTransactions(); break;
                case 5: logout(); break;
                default:
                    System.out.println("Invalid choice");
            }
        }
    }

    private void handleTransfer() throws IOException, ClassNotFoundException {
        System.out.println("Transfer to (username): ");
        String toUser = scanner.nextLine();

        System.out.println("Amount: ");
        double amount = scanner.nextDouble();

        Message msg = new Message(MessageType.TRANSFER, toUser + ":" + amount);
        out.writeObject(msg);

        Message response = (Message) in.readObject();
        System.out.println(response.getPayload());
    }

    private void handleDeposit() throws IOException, ClassNotFoundException {
        System.out.println("Enter the amount you want to deposit: £");
        double amount = scanner.nextDouble();

        Message msg = new Message(MessageType.DEPOSIT, String.valueOf(amount));
        out.writeObject(msg);

        Message response = (Message) in.readObject();
        System.out.println(response.getPayload());
    }

    private void handleWithdraw() throws IOException, ClassNotFoundException {
        System.out.println("Enter the amount you want to withdraw: £");
        double amount = scanner.nextDouble();

        Message msg = new Message(MessageType.WITHDRAW, String.valueOf(amount));
        out.writeObject(msg);

        Message response  = (Message) in.readObject();
        System.out.println(response.getPayload());
    }

    private void handleViewTransactions() throws IOException, ClassNotFoundException {
        Message msg = new Message(MessageType.VIEW_TRANSACTIONS, "");
        out.writeObject(msg);

        Message response = (Message) in.readObject();
        System.out.println("\n--- Transaction History ---");
        System.out.println(response.getPayload());
    }

    private void logout() throws IOException {
        Message msg = new Message(MessageType.LOGOUT, "");
        out.writeObject(msg);
        socket.close();
        System.out.println("Logged out");
    }
}
