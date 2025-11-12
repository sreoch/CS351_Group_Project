package shared;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

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
            return new Message(MessageType.LOGIN_SUCCESS, "Login Successfull");
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
            return new Message(MessageType.SUCCESS, "Account successfully created");
        }
        else {
            System.out.println("failed");
            return new Message(MessageType.FAILED, "Could not create account. User may already exist.");
        }
    }

    private void handleLogout() {
        System.out.println("User " + this.account.getUsername() + " is logging out");
        account.setOnline(false);
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    private Message routeMessage(Message message) {
        MessageType type = message.getType();
        String payload = message.getPayload();
        System.out.println("Handling message");
        switch (type) {
            case MessageType.LOGIN:
                return handleLoginPayload(message.getPayload());
            case MessageType.CREATE_ACCOUNT:
                return handleCreateAccountPayload(message.getPayload());
            case MessageType.DEPOSIT:
                return handleDeposit(message.getPayload());
        }
        System.out.println("Default");
        return new Message(MessageType.FAILED, "Message Type not recognised");
    }

    @Override
    public void run() {
        try {
            ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
            ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());

            try {
                while (true) {
                    System.out.println("Listening for an object");
                    Message message = (Message) input.readObject();
                    System.out.println("Recieved message: " + message.getPayload());
                    Message reply = routeMessage(message);
                    System.out.println("Sending reply: " + reply.getPayload());
                    output.writeObject(reply);
                    output.flush();
                }


            } catch (ClassNotFoundException e) {
                Message reply = new Message(MessageType.FAILED, "Could not recognise message");
                output.writeObject(reply);
                output.flush();
            }


        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
