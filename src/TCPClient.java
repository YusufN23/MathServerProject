import java.io.*;
import java.net.*;
import java.util.*;

class TCPClient {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        try (
                Socket clientSocket = new Socket("127.0.0.1", 6789);
                BufferedReader inFromServer = new BufferedReader(
                        new InputStreamReader(clientSocket.getInputStream())
                );
                DataOutputStream outToServer = new DataOutputStream(
                        clientSocket.getOutputStream()
                );
        ) {
            // 1) JOIN handshake
            System.out.print("Enter your name: ");
            String name = scanner.nextLine().trim();
            outToServer.writeBytes("JOIN " + name + "\n");
            String welcome = inFromServer.readLine();
            System.out.println(welcome);           // prints "WELCOME <name>"

            // 2) Send 3 math operations
            for (int i = 0; i < 3; i++) {
                System.out.print("Enter math operation (ADD 2 3, SUB 5 3, ...): ");
                String op = scanner.nextLine().trim();
                outToServer.writeBytes(op + "\n");

                // read and print only the numeric (or error) result
                String result = inFromServer.readLine();
                System.out.println("Result: " + result);
            }

            // 3) Tell server we’re done
            outToServer.writeBytes("EXIT\n");
            String goodbye = inFromServer.readLine();
            System.out.println(goodbye);           // prints "GOODBYE <name>"

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            scanner.close();
        }

        System.out.println("Client terminated.");
    }
}
