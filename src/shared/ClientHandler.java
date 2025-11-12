package shared;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private Socket socket;
    private Server server;
    private User user;

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
        User user = server.authenticateUser(splitPayload[0], splitPayload[1]);
        if (user != null) {
            user.setOnline(true);
            this.user = user;
            return new Message(MessageType.LOGIN_SUCCESS, "Login Successfull");
        }
        return new Message(MessageType.LOGIN_FAILED, "Login failed. Invalid Credentials");
    }

    private Message handleCreateAccountPayload(String payload) {
        String[] splitPayload = payload.split(":");
        System.out.println("User is trying to create account");
        if (splitPayload.length != 2) {
            System.out.println("Login Failed");
            return new Message(MessageType.LOGIN_FAILED, "Message payload not in correct form.");
        }
        System.out.println("Attempting to add new user");
        boolean result = server.addNewUser(splitPayload[0], splitPayload[1]);
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
        System.out.println("User " + this.user.getUsername() + " is logging out");
        user.setOnline(false);
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//    private Message handleDeposit(String payload) {
//        String[] splitPayload = payload.split(":");
//        if (splitPayload.length != 2) {
//            return new Message(MessageType.FAILED, "Could not recognise message");
//        }
//
//        float amount = 0f;
//        try {
//            amount = Float.parseFloat(splitPayload[0]);
//        } catch (NumberFormatException e) {
//            return new Message(MessageType.FAILED, "Amount must be an int.");
//        }
//        if (amount <= 0) {
//            return new Message(MessageType.FAILED, "Amount must be greater than £0");
//        }
//
//
//    }

    private Message routeMessage(Message message) {
        MessageType type = message.getType();
        String payload = message.getPayload();
        String[] splitPayload = payload.split(":");
        System.out.println("Handling message");
        switch (type) {
            case MessageType.LOGIN:
                return handleLoginPayload(message.getPayload());
            case MessageType.CREATE_ACCOUNT:
                return handleCreateAccountPayload(message.getPayload());
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
