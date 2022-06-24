package it.polimi.ingsw.server;

import it.polimi.ingsw.Settings;
import it.polimi.ingsw.constants.Constants;
import it.polimi.ingsw.controller.Controller;
import it.polimi.ingsw.languages.MessageResourceBundle;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
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
                gracefulTermination(MessageResourceBundle.getMessage("json_not_found"));
            } catch (NumberFormatException e) {
                gracefulTermination(MessageResourceBundle.getMessage("invalid_server_port"));
            }
        } else {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                gracefulTermination(MessageResourceBundle.getMessage("invalid_server_port"));
            }
        }

        if (port < Constants.LOWER_BOUND_SERVER_PORT || port > Constants.UPPER_BOUND_SERVER_PORT) {
            gracefulTermination(MessageResourceBundle.getMessage("invalid_server_port"));
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
        System.out.println(MessageResourceBundle.getMessage("application_closing"));
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
            System.out.println(MessageResourceBundle.getMessage("server_ready"));
            while (true) {
                try {
                    Socket socket = serverSocket.accept();
                    System.out.println(MessageResourceBundle.getMessage("new_socket") + socket.getRemoteSocketAddress());
                    ClientHandler ch = new ClientHandler(socket, controller);
                    executor.submit(ch);
                } catch (IOException e) {
                    break;
                }
            }
            executor.shutdown();
        } catch (IOException e) {
            gracefulTermination(MessageResourceBundle.getMessage("port_not_available"));
        }
    }

}
