package it.polimi.ingsw.server;

import it.polimi.ingsw.Settings;
import it.polimi.ingsw.controller.Controller;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private static int port;
    private final Controller controller;

    private Server() {
        this.controller = new Controller();
    }

    public static void main(String[] args) {
        if (args.length == 0) {
            try {
                Settings settings = Settings.readPrefsFromFile();
                port = settings.getPort();
            } catch (IOException e) {
                gracefulTermination("File settings.json not found. Please check documentation");
            } catch (NumberFormatException e) {
                gracefulTermination("Invalid server_port argument in settings.json");
            }
        } else {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                gracefulTermination("Invalid server_port");
            }
        }

        if (port < 1024 || port > 65535) {
            gracefulTermination("Invalid server_port argument. The port number has to be between 1024 and 65535");
        }

        Server server = new Server();
        server.startServer();
    }

    /**
     * Closes the application showing an input message
     *
     * @param message the input message
     */
    private static void gracefulTermination(String message) {
        System.out.println(message);
        System.out.println("Application will now close...");
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.exit(-1);
    }

    /**
     * Starts the server and listens for incoming connections
     */
    private void startServer() {
        ExecutorService executor = Executors.newCachedThreadPool();

        controller.startPingPong();

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server ready, waiting for clients...");
            while (true) {
                try {
                    Socket socket = serverSocket.accept();
                    System.out.println("New socket: " + socket.getRemoteSocketAddress());
                    ClientHandler ch = new ClientHandler(socket, controller);
                    executor.submit(ch);
                } catch (IOException e) {
                    break;
                }
            }
            executor.shutdown();
        } catch (IOException e) {
            gracefulTermination("The selected port is not available at the moment");
        }
    }

}
