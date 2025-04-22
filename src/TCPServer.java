import java.io.*;
import java.net.*;
import java.util.*;
import java.text.*;

class TCPServer {

    public static void main(String argv[]) throws Exception {
        String clientSentence;
        String response;
        ServerSocket welcomeSocket = new ServerSocket(6789);
        System.out.println("Server is UP and running!");

        while (true) {
            // Accept incoming client connections
            Socket connectionSocket = welcomeSocket.accept();
            // Create input/output streams
            BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
            DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());

            // Log client connection details
            String clientAddress = connectionSocket.getInetAddress().getHostName();
            Date connectionTime = new Date();
            logClientConnection(clientAddress, connectionTime);

            // Handle client request in a new thread
            new Thread(new ClientHandler(connectionSocket, inFromClient, outToClient, clientAddress, connectionTime)).start();
        }
    }

    // Log client connection
    public static void logClientConnection(String clientAddress, Date connectionTime) throws IOException {
        BufferedWriter logWriter = new BufferedWriter(new FileWriter("logs/client_logs.txt", true));
        logWriter.write("Client " + clientAddress + " connected at " + connectionTime + "\n");
        logWriter.close();
    }
}

// ClientHandler class to handle each client's request
class ClientHandler implements Runnable {
    private Socket clientSocket;
    private BufferedReader inFromClient;
    private DataOutputStream outToClient;
    private String clientAddress;
    private Date connectionTime;

    public ClientHandler(Socket clientSocket, BufferedReader inFromClient, DataOutputStream outToClient, String clientAddress, Date connectionTime) {
        this.clientSocket = clientSocket;
        this.inFromClient = inFromClient;
        this.outToClient = outToClient;
        this.clientAddress = clientAddress;
        this.connectionTime = connectionTime;
    }

    public void run() {
        try {
            // Read client request
            String clientRequest = inFromClient.readLine();
            System.out.println("Server received message from " + clientAddress + ": " + clientRequest);

            // Process math request
            String response = processMathRequest(clientRequest);

            // Send response back to client
            outToClient.writeBytes(response + "\n");

            // Log the client activity
            Date disconnectionTime = new Date();
            logClientActivity(clientAddress, clientRequest, response, disconnectionTime);

            // Close the connection
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Log client activity
    public void logClientActivity(String clientAddress, String request, String response, Date disconnectionTime) throws IOException {
        BufferedWriter logWriter = new BufferedWriter(new FileWriter("logs/client_logs.txt", true));
        logWriter.write("Client " + clientAddress + " requested: " + request + " -> Response: " + response + " at " + disconnectionTime + "\n");
        logWriter.close();
    }

    // Method to process math calculations
    public String processMathRequest(String request) {
        String[] parts = request.split(" ");
        String operation = parts[0];
        int num1 = Integer.parseInt(parts[1]);
        int num2 = Integer.parseInt(parts[2]);

        switch (operation) {
            case "ADD":
                return Integer.toString(num1 + num2);
            case "SUB":
                return Integer.toString(num1 - num2);
            case "MUL":
                return Integer.toString(num1 * num2);
            case "DIV":
                return num2 != 0 ? Integer.toString(num1 / num2) : "Error: Division by zero";
            default:
                return "Error: Invalid operation";
        }
    }
}
