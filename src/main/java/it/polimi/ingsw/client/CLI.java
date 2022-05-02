package it.polimi.ingsw.client;

import it.polimi.ingsw.Settings;
import it.polimi.ingsw.messages.Message;
import it.polimi.ingsw.messages.login.ClientLoginMessage;
import it.polimi.ingsw.messages.login.GameLobby;
import it.polimi.ingsw.messages.login.ServerLoginMessage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.util.Locale;
import java.util.Objects;

public class CLI {

    private static int serverPort;
    private static String serverAddress;
    private final BufferedReader stdIn;
    private Socket server;
    private BufferedReader in;
    private PrintWriter out;
    private String username;
    private boolean isLobbyCompleted;
    private boolean isFirstPlayer;
    private boolean isWaitingForStdin;
    private String lastMessageOfThread;

    public CLI() {
        stdIn = new BufferedReader(new InputStreamReader(System.in));
        isLobbyCompleted = false;
        isFirstPlayer = false;
        isWaitingForStdin = false;
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
            cli.startNewGame();
        } catch (IOException e) {
            gracefulTermination("Error connecting to server...");
        }
    }

    private void startNewGame() {
        clearCommandWindow();
    }

    // TODO use this code for ping-pong
    private boolean isPing(Message message) {
        if (Objects.equals(message.getStatus(), "PING")) {
            Message pong = new Message();
            pong.setStatus("PONG");
            out.println(pong.toJson());
            return true;
        }
        return false;
    }

    /**
     * Closes the connection printing the provided message
     *
     * @param message the {@code String} to be printed
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
     * Performs all the operations needed before the game can start
     */
    public void login() throws IOException {
        ServerLoginMessage message;
        Message msg;
        String messageJson;
        boolean isErrorBeenPrinted = false;
        do {
            if (isErrorBeenPrinted) {
                System.out.println("Username already taken");
            }
            askForUsername();
            connectToServer();
            do {
                messageJson = in.readLine();
                msg = Message.fromJson(messageJson);
            } while (isPing(msg));
            message = ServerLoginMessage.fromJson(messageJson);
            isErrorBeenPrinted = true;
        } while (message.getError() == 2);

        if (message.getError() != 0) {
            gracefulTermination(message.getDisplayText());
        }

        if (message.getAction() != null && message.getAction().equals("CREATE_GAME")) {
            isFirstPlayer = true;

            Thread thread = createNewPingThread();
            thread.start();

            System.out.println(message.getDisplayText());

            int numPlayers = askForNumberOfPlayers();
            boolean isExpert = askForExpert();
            sendNumPlayers(numPlayers, isExpert);

            try {
                thread.join();
            } catch (InterruptedException e) {
                gracefulTermination("Error while connecting to server");
            }

        } else {
            checkIfGameReady(message);
        }

        waitForOtherPlayers();
    }

    /**
     * Sends a message to the server containing the desired number of players
     *
     * @param numPlayers the desired number of players to be included in the message
     */
    private void sendNumPlayers(int numPlayers, boolean isExpert) {
        ClientLoginMessage msg = new ClientLoginMessage();
        msg.setAction("CREATE_GAME");
        msg.setNumPlayers(numPlayers);
        msg.setExpert(isExpert);
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

        isWaitingForStdin = true;

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
     * Asks the user for the desired playing mode - expert or not - until it is correct
     *
     * @return the number of players chosen by the user
     */
    private boolean askForExpert() throws IOException {

        String res;

        do {
            System.out.print("Do you want to play in expert mode? [Y/N] ");
            res = stdIn.readLine();
            res = res.toLowerCase(Locale.ROOT);
        } while (!res.equals("y") && !res.equals("n"));

        isWaitingForStdin = false;

        return res.equals("y");
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

    /**
     * Waits until the lobby is full and a game is ready to start
     */
    private void waitForOtherPlayers() throws IOException {
        String jsonMessage;
        Message msg;
        ServerLoginMessage loginMessage;

        if (isFirstPlayer) {
            loginMessage = ServerLoginMessage.fromJson(lastMessageOfThread);
            checkIfGameReady(loginMessage);
        }

        while (!isLobbyCompleted) {
            do {
                jsonMessage = in.readLine();
                msg = Message.fromJson(jsonMessage);
            } while (isPing(msg));
            loginMessage = ServerLoginMessage.fromJson(jsonMessage);
            checkIfGameReady(loginMessage);
        }

        System.out.println("Now I'm waiting for the game to start");

    }

    /**
     * Checks if a match is ready to start analyzing the provided {@code ServerLoginMessage}
     *
     * @param loginMessage the {@code ServerLoginMessage} to be checked
     */
    private void checkIfGameReady(ServerLoginMessage loginMessage) {
        clearCommandWindow();
        if (loginMessage.getError() != 0) {
            gracefulTermination(loginMessage.getDisplayText());
        }
        System.out.println(loginMessage.getDisplayText());
        GameLobby lobby = loginMessage.getGameLobby();
        printCurrentLobby(lobby);
        isLobbyCompleted = lobby.getPlayers().length == lobby.getNumPlayers();
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

    private Thread createNewPingThread() {
        return new Thread(() -> {
            while (true) {
                try {
                    String json = in.readLine();
                    Message message = Message.fromJson(json);
                    if (message.getError() != 0) {
                        System.out.println("");
                        ServerLoginMessage loginMsg = ServerLoginMessage.fromJson(json);
                        clearCommandWindow();
                        gracefulTermination(loginMsg.getDisplayText());
                    }
                    if (!isPing(message) && !isWaitingForStdin) {
                        lastMessageOfThread = json;
                        break;
                    }
                } catch (SocketException e) {
                    gracefulTermination("Error");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

}
