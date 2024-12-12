/**
 * A simple chat application that allows multiple users to communicate with
 * each other.
 *
 * @author <your name>
 */
import java.io.*;
import java.net.*;
import java.util.*;

/**
 * Main class of the chat application.
 */
public class ChatApplication {
    private static final int PORT = 12345;
    private static HashMap<String, ClientHandler> clients = new HashMap<>();

    /**
     * Entry point of the application. Allows the user to select whether to
     * start the server or the client.
     *
     * @param args Command line arguments.
     * @throws IOException Thrown if there is an error with the network.
     */
    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Select mode: 1 for Server, 2 for Client");
        int choice = scanner.nextInt();
        scanner.nextLine(); // Consume the newline

        if (choice == 1) {
            startServer();
        } else if (choice == 2) {
            startClient();
        } else {
            System.out.println("Invalid choice. Exiting.");
        }
    }

    /**
     * Starts the chat server.
     *
     * @throws IOException Thrown if there is an error with the network.
     */
    public static void startServer() throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Chat Server started on port " + PORT);

            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("New client connected.");
                new Thread(new ClientHandler(socket)).start();
            }
        } catch (IOException e) {
            System.out.println("Error in server: " + e.getMessage());
        }
    }

    /**
     * Starts the chat client.
     */
    public static void startClient() {
        try (Socket socket = new Socket("localhost", PORT);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            Scanner scanner = new Scanner(System.in);
            System.out.println("Connected to the chat server.");

            // Start a thread to read messages from the server
            Thread readerThread = new Thread(() -> {
                try {
                    String message;
                    while ((message = in.readLine()) != null) {
                        System.out.println(message);
                    }
                } catch (IOException e) {
                    System.out.println("Disconnected from the server.");
                }
            });
            readerThread.start();

            // Main thread sends messages
            while (true) {
                String message = scanner.nextLine();
                out.println(message);

                if (message.equalsIgnoreCase("exit")) {
                    break;
                }
            }
        } catch (IOException e) {
            System.out.println("Error connecting to the server: " + e.getMessage());
        }
    }

    /**
     * Handles communication with a single client.
     */
    static class ClientHandler implements Runnable {
        private Socket socket;
        private String username;
        private PrintWriter out;
        private BufferedReader in;

        /**
         * Creates a new ClientHandler.
         *
         * @param socket The socket to communicate with the client.
         */
        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        /**
         * Registers the client and starts listening for messages.
         */
        @Override
        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

                // Register username
                out.println("Enter your username:");
                username = in.readLine();
                synchronized (clients) {
                    clients.put(username, this);
                }
                broadcast("Server", username + " has joined the chat!");

                String message;
                while ((message = in.readLine()) != null) {
                    if (message.startsWith("/pm")) {
                        privateMessage(message);
                    } else if (message.startsWith("/file")) {
                        receiveFile(message);
                    } else {
                        broadcast(username, message);
                    }
                }
            } catch (IOException e) {
                System.out.println("Client disconnected: " + username);
            } finally {
                closeConnection();
            }
        }

        /**
         * Sends a private message to another user.
         *
         * @param message The message to send. Should be in the format "/pm <username> <message>".
         */
        private void privateMessage(String message) {
            String[] parts = message.split(" ", 3);
            if (parts.length >= 3) {
                String targetUser = parts[1];
                String privateMsg = parts[2];
                synchronized (clients) {
                    if (clients.containsKey(targetUser)) {
                        clients.get(targetUser).out.println("[Private] " + username + ": " + privateMsg);
                        out.println("[Private to " + targetUser + "] " + privateMsg);
                    } else {
                        out.println("User " + targetUser + " not found.");
                    }
                }
            } else {
                out.println("Invalid private message format. Use /pm <username> <message>");
            }
        }

        /**
         * Receives a file from another user.
         *
         * @param message The message to send. Should be in the format "/file <username> <filename>".
         * @throws IOException Thrown if there is an error with the network.
         */
        private void receiveFile(String message) throws IOException {
            String[] parts = message.split(" ", 3);
            if (parts.length >= 3) {
                String targetUser = parts[1];
                String fileName = parts[2];

                out.println("Send file content:");
                String fileContent = in.readLine();

                synchronized (clients) {
                    if (clients.containsKey(targetUser)) {
                        clients.get(targetUser).out.println("[File] " + username + " sent you a file: " + fileName);
                        clients.get(targetUser).out.println(fileContent);
                    } else {
                        out.println("User " + targetUser + " not found.");
                    }
                }
            } else {
                out.println("Invalid file sharing format. Use /file <username> <filename>");
            }
        }

        /**
         * Broadcasts a message to all users.
         *
         * @param sender The user who sent the message.
         * @param message The message to broadcast.
         */
        private void broadcast(String sender, String message) {
            synchronized (clients) {
                for (ClientHandler client : clients.values()) {
                    if (!client.username.equals(sender)) {
                        client.out.println(sender + ": " + message);
                    }
                }
            }
        }

        /**
         * Closes the connection with the client.
         */
        private void closeConnection() {
            try {
                synchronized (clients) {
                    clients.remove(username);
                }
                socket.close();
                broadcast("Server", username + " has left the chat.");
            } catch (IOException e) {
                System.out.println("Error closing connection for " + username);
            }
        }
    }
}

