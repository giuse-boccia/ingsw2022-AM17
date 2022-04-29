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
import java.util.Arrays;

public class CLI {

    private static int serverPort;
    private static String serverAddress;
    private final BufferedReader stdIn;
    private Socket server;
    private BufferedReader in;
    private PrintWriter out;
    private String username;
    private boolean isLobbyCompleted;

    public CLI() {
        stdIn = new BufferedReader(new InputStreamReader(System.in));
        isLobbyCompleted = false;
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

        CLI cli = new CLI();

        try {
            cli.login();
        } catch (IOException e) {
            gracefulTermination("Error connecting to server...");
        }
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
     * Performs all the operations needed before the game can start
     */
    public void login() throws IOException {
        ServerLoginMessage message;
        boolean isErrorBeenPrinted = false;
        do {
            if (isErrorBeenPrinted) {
                System.out.println("Username already taken");
            }
            askForUsername();
            connectToServer();
            String messageJson = in.readLine();
            message = ServerLoginMessage.getMessageFromJSON(messageJson);
            isErrorBeenPrinted = true;
        } while (message.getError() == 2);

        if (message.getError() != 0) {
            gracefulTermination(message.getMessage());
        }

        if (message.getAction() != null && message.getAction().equals("CREATE_GAME")) {
            int numPlayers = askForNumberOfPlayers();
            sendNumPlayers(numPlayers);
        } else {
            checkIfGameReady(message);
        }

        // TODO this message could be L.B.1 and L.B.2 and it's not caught!

        waitForOtherPlayers();
    }

    /**
     * Sends a message to the server containing the desired number of players
     *
     * @param numPlayers the desired number of players to be included in the message
     */
    private void sendNumPlayers(int numPlayers) {
        ClientLoginMessage msg = new ClientLoginMessage();
        msg.setAction("CREATE_GAME");
        msg.setNumPlayers(numPlayers);
        out.println(msg.toJson());
    }

    /**
     * Asks the user for his username and saves it in this.username
     */
    private void askForUsername() throws IOException {
        System.out.print("Insert username: ");
        username = stdIn.readLine();
    }

    /**
     * Asks the user for the desired number of players until it is correct
     *
     * @return the number of players chosen by the user
     */
    private int askForNumberOfPlayers() throws IOException {
        int res;

        do {
            System.out.print("Insert desired number of players (2, 3 or 4): ");
            String string = stdIn.readLine();
            try {
                res = Integer.parseInt(string);
            } catch (NumberFormatException e) {
                res = -1;
                System.out.println("Number of players must be a number!");
            }
        } while (res < 2 || res > 4);

        return res;
    }

    /**
     * Connects to server and listens for broadcast messages
     */
    private void connectToServer() {
        try {
            server = new Socket(serverAddress, serverPort);
            in = new BufferedReader(new InputStreamReader(server.getInputStream()));
            out = new PrintWriter(server.getOutputStream(), true);

            ClientLoginMessage usernameMessage = new ClientLoginMessage();
            usernameMessage.setAction("SET_USERNAME");
            usernameMessage.setUsername(username);
            out.println(usernameMessage.toJson());

            System.out.println("Connecting to server...");

        } catch (IOException e) {
            gracefulTermination("Cannot connect to server, check server_address argument");
        }
    }


    private void waitForOtherPlayers() throws IOException {
        while (!isLobbyCompleted) {
            String jsonMessage = in.readLine();
            ServerLoginMessage loginMessage = ServerLoginMessage.getMessageFromJSON(jsonMessage);
            checkIfGameReady(loginMessage);
        }

        System.out.println("Now I'm waiting for the game to start");

    }

    private void checkIfGameReady(ServerLoginMessage loginMessage) {
        clearCommandWindow();
        if (loginMessage.getError() != 0) {
            gracefulTermination(loginMessage.getMessage());
        }
        System.out.println(loginMessage.getMessage());
        GameLobby lobby = loginMessage.getGameLobby();
        printCurrentLobby(lobby);
        isLobbyCompleted = lobby.getPlayers().length == lobby.getNumPlayers();
    }

    @Deprecated
    private void OLD_login() {
        try {

            String messageJson = in.readLine();
            ServerLoginMessage message = ServerLoginMessage.getMessageFromJSON(messageJson);
            if (message.getError() != 0) {
                switch (message.getError()) {
                    case 1, 3 -> gracefulTermination(message.getMessage());
                    case 2 -> {

                    }
                }
            }

            System.out.println("Successfully connected to server!");

            String welcomeMessage = in.readLine();
            message = ServerLoginMessage.getMessageFromJSON(welcomeMessage);


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
                default ->
                        gracefulTermination(message.getError() != 0 ? message.getMessage() : "Error connecting to server");
            }

            waitForOtherPlayers();
        } catch (IOException e) {
            gracefulTermination("Connection to server lost");
        } catch (JsonSyntaxException e) {
            gracefulTermination("Error parsing message from server");
        }

    }

    @Deprecated
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
                                case "game created", "player has joined", "Lobby completed. A new game is starting..." -> printCurrentLobby(loginMessage.getGameLobby());
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
                    e.printStackTrace();
                }
            }
        }).start();

    }

    /**
     * Prints a line containing only "-" characters
     */
    private void clearCommandWindow() {
        System.out.println("------------------------------------------------------------");
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

    @Deprecated
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
            out.println(message.toJson());
        } catch (IOException e) {
            gracefulTermination("Invalid username");
        } catch (NumberFormatException e) {
            gracefulTermination("Invalid number of players");
        }
        System.out.println("Game created, waiting for other players...");
    }

    @Deprecated
    private void joinGame() {
        System.out.print("Insert username: ");
        try {
            String username = stdIn.readLine();
            ClientLoginMessage message = new ClientLoginMessage();
            message.setUsername(username);
            message.setAction("join game");
            out.println(message.toJson());
        } catch (IOException e) {
            gracefulTermination("Invalid username");
        }
    }

}
