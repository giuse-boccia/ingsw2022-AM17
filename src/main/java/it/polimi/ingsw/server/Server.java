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
    private Controller controller;

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

    private void startServer() {
        ExecutorService executor = Executors.newCachedThreadPool();

        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            gracefulTermination("The selected port is not available at the moment");
        }
        System.out.println("Server ready");
        while (true) {
            System.out.println("Waiting...");
            try {
                Socket socket = serverSocket.accept();
                System.out.println("Accepted from " + socket.getRemoteSocketAddress());
                ClientHandler ch = new ClientHandler(socket, controller);
                controller.addClientHandler(ch);
                executor.submit(ch);
            } catch (IOException e) {
                break;
            }
        }
        executor.shutdown();
    }

    private static void gracefulTermination(String message) {
        System.out.println(message);
        System.out.println("Application will now close...");
        System.exit(-1);
    }

}
