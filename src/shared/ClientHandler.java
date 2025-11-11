package shared;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private Socket socket;

    public ClientHandler(Socket socket) {
        System.out.println("Initialising client handler");
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
            ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());

            try {
                System.out.println("Listening for an object");
                Message message = (Message) input.readObject();
                System.out.println(message.getPayload());
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
