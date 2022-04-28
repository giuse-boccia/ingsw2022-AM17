package it.polimi.ingsw.client;

import com.google.gson.JsonSyntaxException;
import it.polimi.ingsw.Settings;
import it.polimi.ingsw.messages.login.ClientLoginMessage;
import it.polimi.ingsw.messages.login.GameLobby;
import it.polimi.ingsw.messages.login.ServerLoginMessage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class CLI {

    private static int serverPort;
    private static String serverAddress;
    private Socket server;
    private BufferedReader stdIn;
    private BufferedReader in;
    private PrintWriter out;
    private String username;

    private CLI(String username) {
        this.username = username;
    }

    public static void main(String[] args) {
        if (args.length < 2) {
            try {
                Settings settings = Settings.readPrefsFromFile();
                if (settings.getAddress() == null) {
                    gracefulTermination("Invalid server_address argument in settings.json");
                }
                serverPort = settings.getPort();
                serverAddress = settings.getAddress();
            } catch (IOException e) {
                gracefulTermination("File settings.json not found. Please check documentation");
            } catch (NumberFormatException e) {
                gracefulTermination("Invalid server_port argument in settings.json");
            }
        } else {
            try {
                serverPort = Integer.parseInt(args[0]);
                serverAddress = args[1];
            } catch (NumberFormatException e) {
                gracefulTermination("Invalid server_port argument in settings.json");
            }
        }

        if (serverPort < 1024 || serverPort > 65535) {
            gracefulTermination("Invalid server_port argument. The port number has to be between 1024 and 65535");
        }

        Scanner in = new Scanner(System.in);
        System.out.print("Insert username: ");
        String username = in.nextLine();

        CLI cli = new CLI(username);
        cli.connectToServer();      // TODO: we changed some messages, this needs some changes

    }

    /**
     * Closes the connection printing the provided message
     *
     * @param message the {@code String} to be printed
     */
    private static void gracefulTermination(String message) {
        System.out.println(message);
        System.out.println("Application will now close...");
        System.exit(-1);
    }

    /**
     * Connects the client to the server
     */
    private void connectToServer() {
        // ClientSocket now will try to connect to serverAddress:serverPort
        try {
            server = new Socket(serverAddress, serverPort);
            stdIn = new BufferedReader(new InputStreamReader(System.in));
            in = new BufferedReader(new InputStreamReader(server.getInputStream()));
            out = new PrintWriter(server.getOutputStream(), true);

            ClientLoginMessage usernameMessage = new ClientLoginMessage();
            usernameMessage.setUsername(username);
            usernameMessage.setAction("set username");
            out.println(usernameMessage.getJSON());

            System.out.println("Connecting to server...");
            String welcomeMessage = in.readLine();
            ServerLoginMessage message = ServerLoginMessage.getMessageFromJSON(welcomeMessage);
            System.out.println("Successfully connected to server!");

            listenToBroadcastMessages();

            if (message.getError() == 1) {
                gracefulTermination(message.getMessage());
            }

            switch (message.getAction()) {
                case "join game" -> {
                    printCurrentLobby(message.getGameLobby());
                    joinGame();
                }
                case "create game" -> createGame();
                default -> gracefulTermination(message.getError() != 0 ? message.getMessage() : "Error connecting to server");
            }

            waitForOtherPlayers();

        } catch (IOException e) {
            gracefulTermination("Invalid server_address argument");
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }
    }

    // TODO ping all clients to detect if they're alive
    // Delete client from map when CTRL+C is pressed
    private void listenToBroadcastMessages() {
        new Thread(() -> {
            while (true) {
                try {
                    String message = in.readLine();
                    ServerLoginMessage loginMessage = ServerLoginMessage.getMessageFromJSON(message);
                    if (loginMessage != null && "login".equals(loginMessage.getStatus())) {
                        int error = loginMessage.getError();
                        if (error != 0) {
                            switch (error) {
                                case 1, 3 -> gracefulTermination(loginMessage.getMessage());
                                case 2 -> {
                                    System.out.println(loginMessage.getMessage());
                                    clearCommandWindow();
                                    joinGame();
                                }
                            }
                        }
                        String description = loginMessage.getMessage();
                        if (description != null && error != 2) {
                            switch (description) {
                                case "game created", "player has joined", "Lobby completed. A new game is starting..." -> {
                                    printCurrentLobby(loginMessage.getGameLobby());
                                }
                                case "a new game is starting" -> {
                                    System.out.println("A new game is starting, you will be logged out...");
                                    System.exit(-1);
                                }
                            }
                            clearCommandWindow();
                            System.out.println(description);
                        }
                    }
                } catch (IOException e) {
                    handleIOException();
                }
            }
        }).start();

    }

    private void clearCommandWindow() {
        for (int i = 0; i < 1; i++) {
            System.out.println("------------------------------------------------------------");
        }
    }

    private void waitForOtherPlayers() {

    }

    /**
     * Prints the current state of the {@code GameLobby}
     *
     * @param lobby the {@code GameLobby} to print
     */
    private void printCurrentLobby(GameLobby lobby) {
        String message = "GAME: " + lobby.getPlayers().length;
        if (lobby.getNumPlayers() != -1) {
            message += "/" + lobby.getNumPlayers();
        }
        message += " players";
        System.out.println(message);
        for (String name : lobby.getPlayers()) {
            System.out.println("  - " + name);
        }
        System.out.println("");
    }

    private void createGame() {
        System.out.print("Insert username: ");
        try {
            String username = stdIn.readLine();
            ClientLoginMessage message = new ClientLoginMessage();
            message.setUsername(username);
            System.out.print("Insert desired number of players: ");
            int numberOfPlayers = Integer.parseInt(stdIn.readLine());
            message.setNumPlayers(numberOfPlayers);
            message.setAction("create game");
            out.println(message.getJSON());
        } catch (IOException e) {
            gracefulTermination("Invalid username");
        } catch (NumberFormatException e) {
            gracefulTermination("Invalid number of players");
        }
        System.out.println("Game created, waiting for other players...");
    }

    private void joinGame() {
        System.out.print("Insert username: ");
        try {
            String username = stdIn.readLine();
            ClientLoginMessage message = new ClientLoginMessage();
            message.setUsername(username);
            message.setAction("join game");
            out.println(message.getJSON());
        } catch (IOException e) {
            gracefulTermination("Invalid username");
        }
    }

    private void handleIOException() {
        System.err.println("Couldn't get I/O, connection will be closed...");
        System.exit(-1);
    }

}
