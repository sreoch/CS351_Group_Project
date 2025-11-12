package shared;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private Socket socket;
    private Server server;

    public ClientHandler(Socket socket, Server server) {
        System.out.println("Initialising client handler");
        this.socket = socket;
        this.server = server;
    }

    private Message handleMessage(Message message) {
        MessageType type = message.getType();
        String payload = message.getPayload();
        String[] splitPayload = payload.split(":");
        System.out.println("Handling message");
        switch (type) {
            case MessageType.LOGIN:
                if (splitPayload.length != 2) {
                    return new Message(MessageType.LOGIN_FAILED, "Message payload not in correct form.");
                }
                User user = server.authenticateUser(splitPayload[0], splitPayload[1]);
                if (user != null) {
                    return new Message(MessageType.LOGIN_SUCCESS, "Login Successfull");
                }
                return new Message(MessageType.LOGIN_FAILED, "Login failed. Invalid Credentials");
            case MessageType.CREATE_ACCOUNT:
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
        System.out.println("Default");
        return new Message(MessageType.FAILED, "Message Type not recognised");
    }

    @Override
    public void run() {
        try {
            ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
            ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());

            try {
                System.out.println("Listening for an object");
                Message message = (Message) input.readObject();
                System.out.println("Recieved message: " + message.getPayload());
                Message reply = handleMessage(message);
                System.out.println("Sending reply: " + reply.getPayload());
                output.writeObject(reply);
                output.flush();

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
