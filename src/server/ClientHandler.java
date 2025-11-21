package server;

import shared.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.List;

public class ClientHandler implements Runnable {
    private Socket socket;
    private Server server;
    private Account account;

    public ClientHandler(Socket socket, Server server) {
        System.out.println("Initialising client handler");
        this.socket = socket;
        this.server = server;
    }

    private Message handleLoginPayload(String payload) {
        String[] splitPayload = payload.split(":");
        if (splitPayload.length != 2) {
            return new Message(MessageType.LOGIN_FAILED, "Message payload not in correct form.");
        }
        Account account = server.authenticateAccount(splitPayload[0], splitPayload[1]);
        if (account != null) {
            account.setOnline(true);
            this.account = account;
            return new Message(MessageType.LOGIN_SUCCESS, String.valueOf(account.getBalance()));
        }
        return new Message(MessageType.LOGIN_FAILED, "Login failed. Invalid Credentials");
    }

    private Message handleCreateAccountPayload(String payload) {
        String[] splitPayload = payload.split(":");
        System.out.println("User is trying to create account");
        if (splitPayload.length != 2) {
            return new Message(MessageType.FAILED, "Message payload not in correct form.");
        }
        System.out.println("Attempting to add new user");
        boolean result = server.addNewAccount(splitPayload[0], splitPayload[1]);
        System.out.println("Added new user");

        if (result) {
            System.out.println("Success");
            Account newAccount = server.authenticateAccount(splitPayload[0], splitPayload[1]);
            this.account = newAccount;
            newAccount.setOnline(true);

            return new Message(MessageType.ACCOUNT_CREATED, String.valueOf(newAccount.getBalance()));
        } else {
            System.out.println("failed");
            return new Message(MessageType.FAILED, "Could not create account. User may already exist.");
        }
    }

    private Message handleLogout(String payload) {
        System.out.println("User " + this.account.getUsername() + " is logging out");
        account.setOnline(false);
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new Message(MessageType.SUCCESS, "Logged out successfully");
    }

    private Message handleDeposit(String payload) {
        String[] splitPayload = payload.split(":");
        if (splitPayload.length != 1) {
            return new Message(MessageType.FAILED, "Could not recognise message");
        }

        double amount = 0f;
        try {
            amount = Double.parseDouble(splitPayload[0]);
        } catch (NumberFormatException e) {
            return new Message(MessageType.FAILED, "Amount must be a double.");
        }
        if (amount <= 0) {
            return new Message(MessageType.FAILED, "Amount must be greater than £0");
        }

        boolean result = server.deposit(account, amount);
        if (result == true) {
            return new Message(MessageType.SUCCESS, "Successfully deposited");
        } else {
            return new Message(MessageType.FAILED, "An error occurred while making deposit");
        }
    }

    private Message handleViewTransactions() {
        TransactionLedger ledger = server.getLedger();
        List<Transaction> transactions = ledger.getAllTransactionsForUser(account.getUsername());
        String transactionsString = "";
        for (Transaction transaction : transactions) {
            try {
                transactionsString = transactionsString + "\n" + transaction.toString();
            } catch (Exception e) {
                System.out.println("Error!!: " + e.toString());
            }

        }
        return new Message(MessageType.SUCCESS, transactionsString);
    }

    private Message handleWithdraw(String payload) {
        double amount;
        try {
            amount = Double.parseDouble(payload);
        } catch (NumberFormatException e) {
            return new Message(MessageType.FAILED, "Amount must be a valid number");
        }

        if (amount > account.getBalance()) {
            return new Message(MessageType.FAILED, "Insufficient funds.");
        }

        boolean result = server.withdraw(account, amount);
        if (result) {
            return new Message(MessageType.SUCCESS, "Withdraw successful");
        }
        return new Message(MessageType.FAILED, "There was an error while withdrawing funds.");
    }

    private Message handleTransfer(String payload) {
        String[] splitPayload = payload.split(":");
        if (splitPayload.length != 2) {
            return new Message(MessageType.FAILED, "Unable to recognise message");
        }

        try {
            double amount = Double.parseDouble(splitPayload[1]);
            Account recipient = server.getAccount(splitPayload[0]);
            if (recipient == null) {
                return new Message(MessageType.FAILED, "Could not find recipient account");
            }
            boolean result = server.transfer(account, recipient, amount);
            if (!result) {
                return new Message(MessageType.FAILED, "There was an error while transferring");
            }
            return new Message(MessageType.SUCCESS, "Successfully transfered");
        } catch (NumberFormatException e) {
            return new Message(MessageType.FAILED, "Amount must be a valid number");
        }
    }

    private Message routeMessage(Message message) {
        MessageType type = message.getType();
        String payload = message.getPayload();
        switch (message.getType()) {
            case MessageType.LOGIN:
                return handleLoginPayload(message.getPayload());
            case MessageType.CREATE_ACCOUNT:
                return handleCreateAccountPayload(message.getPayload());
            case MessageType.DEPOSIT:
                return handleDeposit(message.getPayload());
            case MessageType.VIEW_TRANSACTIONS:
                return handleViewTransactions();
            case MessageType.WITHDRAW:
                return handleWithdraw(message.getPayload());
            case MessageType.TRANSFER:
                return handleTransfer(message.getPayload());
            case MessageType.LOGOUT:
                return handleLogout(message.getPayload());
        }
        System.out.println("Default");
        return new Message(MessageType.FAILED, "Message Type not recognised");
    }

    @Override
    public void run() {
        try (ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream input = new ObjectInputStream(socket.getInputStream())) {

            while (true) {
                System.out.println("Listening for an object");
                try {
                    Message message = (Message) input.readObject();
                    System.out.println("Recieved message: " + message.getPayload());
                    Message reply = routeMessage(message);
                    System.out.println("Sending reply: " + reply.getPayload());
                    output.writeObject(reply);
                    output.flush();
                } catch (SocketException e) {
                    System.out.println("Client Disconnected");
                    break;
                } catch (ClassNotFoundException e) {
                    Message reply = new Message(MessageType.FAILED, "Could not recognise message");
                    output.writeObject(reply);
                    output.flush();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (account != null) {
                account.setOnline(false);
            }
        }
    }
}
