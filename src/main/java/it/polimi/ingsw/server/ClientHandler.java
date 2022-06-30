package it.polimi.ingsw.server;

import it.polimi.ingsw.controller.Controller;
import it.polimi.ingsw.exceptions.GameEndedException;
import it.polimi.ingsw.languages.Messages;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.util.Scanner;

public class ClientHandler implements Runnable, Communicable {

    private final Socket socket;
    private final Controller controller;
    private Scanner in;
    private PrintWriter out;

    private boolean stop;

    public ClientHandler(Socket socket, Controller controller) {
        this.socket = socket;
        this.controller = controller;
        this.stop = false;
    }

    /**
     * Closes the socket connection with the client
     */
    public void closeConnection() throws IOException {
        stop = true;
        socket.close();
    }

    @Override
    public void sendMessageToClient(String res) {
        out.println(res);
        out.flush();
    }

    @Override
    public void run() {
        try {
            in = new Scanner(socket.getInputStream());
            out = new PrintWriter(socket.getOutputStream());

            while (Thread.currentThread().isAlive()) {
                String message = in.nextLine();
                controller.handleMessage(message, this);
            }
        } catch (SocketException e) {
            // Socket closed by another method
        } catch (IOException e) {
            // Client connection error
            System.err.println(Messages.getMessage("alerting_other_clients"));
        } catch (GameEndedException e) {
            // Game is ended, clientHandler will close socket
            try {
                // Maybe Thread.sleep, then close socket
                socket.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}
