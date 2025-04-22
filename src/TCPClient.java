import java.io.*;
import java.net.*;
import java.util.*;

class TCPClient {

    public static void main(String argv[]) throws Exception {
        String sentence;
        String modifiedSentence;
        Scanner scanner = new Scanner(System.in);
        Socket clientSocket = new Socket("127.0.0.1", 6789);

        // Create input/output streams
        BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());

        // Get client name
        System.out.println("Enter your name:");
        String name = scanner.nextLine();

        // Send client name to server for logging
        outToServer.writeBytes("JOIN " + name + "\n");

        // Send multiple requests to the server
        for (int i = 0; i < 3; i++) {
            System.out.println("Enter math operation (ADD 2 3, SUB 5 3, MUL 2 4, DIV 8 2):");
            sentence = scanner.nextLine();
            outToServer.writeBytes(sentence + "\n");

            // Receive and print the server response
            modifiedSentence = inFromServer.readLine();
            System.out.println("FROM SERVER: " + modifiedSentence);
        }

        // Close connection after requests are done
        outToServer.writeBytes("CLOSE " + name + "\n");
        clientSocket.close();
    }
}
