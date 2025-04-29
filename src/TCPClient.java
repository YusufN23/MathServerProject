import java.io.*;
import java.net.*;
import java.util.*;

class TCPClient {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        try (
                Socket clientSocket = new Socket("127.0.0.1", 6789);
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(clientSocket.getInputStream())
                );
                DataOutputStream out = new DataOutputStream(
                        clientSocket.getOutputStream()
                );
        ) {
            // JOIN handshake
            System.out.print("Enter your name: ");
            String name = scanner.nextLine().trim();
            out.writeBytes("JOIN " + name + "\n");
            System.out.println(in.readLine());  // WELCOME <name>

            // Three math operations
            for (int i = 0; i < 3; i++) {
                System.out.print("Enter math operation like 'add 2 2': ");
                String op = scanner.nextLine().trim();
                out.writeBytes(op + "\n");
                String result = in.readLine();
                System.out.println("Result: " + result);
            }

            // EXIT and goodbye
            out.writeBytes("EXIT\n");
            System.out.println(in.readLine());  // GOODBYE <name>
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            scanner.close();
        }
        System.out.println("Client terminated.");
    }
}
