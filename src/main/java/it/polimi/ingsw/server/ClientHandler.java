package it.polimi.ingsw.server;

import it.polimi.ingsw.controller.Controller;
import it.polimi.ingsw.exceptions.GameLoginException;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ClientHandler implements Runnable {

    private final Socket socket;
    private final Controller controller;
    private Scanner in;
    private PrintWriter out;

    public ClientHandler(Socket socket, Controller controller) {
        this.socket = socket;
        this.controller = controller;
    }

    /**
     * Sends a response to the client
     *
     * @param res the response to send to the client
     * @throws IOException if the exception is thrown by the Socket
     */
    public void sendMessageToClient(String res) throws IOException {
        out.println(res);
        out.flush();
    }

    @Override
    public void run() {
        try {
            in = new Scanner(socket.getInputStream());
            out = new PrintWriter(socket.getOutputStream());

            while (true) {
                String message = in.nextLine();
                controller.handleMessage(message, this);
            }
        } catch (IOException e) {
            // Server has crashed
            System.err.println("Connection to server lost, the program will now close...");
            System.exit(-1);
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
