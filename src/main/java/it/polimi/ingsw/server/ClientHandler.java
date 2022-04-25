package it.polimi.ingsw.server;

import it.polimi.ingsw.controller.Controller;
import it.polimi.ingsw.exceptions.GameLoginException;

import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ClientHandler implements Runnable {

    private final Socket socket;
    private final Controller controller;

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
    public void sendResponse(String res) throws IOException {
        PrintWriter out = new PrintWriter(socket.getOutputStream());
        out.println(res);
        out.flush();
    }

    @Override
    public void run() {
        try {
            Scanner in = new Scanner(socket.getInputStream());
            PrintWriter out = new PrintWriter(socket.getOutputStream());

            controller.sendWelcomeMessage(this);

            while (true) {
                String message = in.nextLine();
                controller.handleMessage(message, this);
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());

        } catch (GameLoginException e) {
            System.err.println("Game full, kicking client " + socket.getRemoteSocketAddress());
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
