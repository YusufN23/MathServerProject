import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class TCPServer {
    public static BlockingQueue<Request> requestQueue = new LinkedBlockingQueue<>();

    public static void main(String[] args) throws Exception {
        ServerSocket welcomeSocket = new ServerSocket(6789);
        System.out.println("Server is UP and running on port 6789!");

        new Thread(new Dispatcher()).start();

        while (true) {
            Socket conn = welcomeSocket.accept();
            String addr = conn.getInetAddress().getHostName();
            Date connectTime = new Date();
            logClientConnection(addr, connectTime);

            BufferedReader in  = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            DataOutputStream out = new DataOutputStream(conn.getOutputStream());

            ClientHandler handler = new ClientHandler(conn, in, out, addr, connectTime);
            new Thread(handler).start();
        }
    }

    public static void logClientConnection(String addr, Date when) {
        try (BufferedWriter w = new BufferedWriter(new FileWriter("logs/client_logs.txt", true))) {
            w.write("Client " + addr + " connected at " + when + "\n");
        } catch (IOException e) { e.printStackTrace(); }
    }

    public static void logClientActivity(String addr, String req, String rsp, Date when) {
        try (BufferedWriter w = new BufferedWriter(new FileWriter("logs/client_logs.txt", true))) {
            w.write("Client " + addr
                    + " requested: \"" + req + "\""
                    + " -> Response: \"" + rsp + "\""
                    + " at " + when + "\n");
        } catch (IOException e) { e.printStackTrace(); }
    }

    public static void logClientDisconnection(String addr, Date connTime, Date discTime) {
        long ms      = discTime.getTime() - connTime.getTime();
        long seconds = (ms / 1000) % 60;
        long minutes = (ms / (60 * 1000));
        try (BufferedWriter w = new BufferedWriter(new FileWriter("logs/client_logs.txt", true))) {
            w.write("Client " + addr
                    + " disconnected at " + discTime
                    + " (connected for " + minutes + "m " + seconds + "s)\n");
        } catch (IOException e) { e.printStackTrace(); }
    }
}

class Request {
    final ClientHandler handler;
    final String request;
    final Date timestamp;
    public Request(ClientHandler h, String r, Date t) {
        handler   = h;
        request   = r;
        timestamp = t;
    }
}

class Dispatcher implements Runnable {
    public void run() {
        while (true) {
            try {
                Request req = TCPServer.requestQueue.take();
                String addr     = req.handler.getClientAddress();
                String request  = req.request;
                String response = req.handler.processMathRequest(request);

                req.handler.sendResponse(response);
                TCPServer.logClientActivity(addr, request, response, new Date());

                if ("EXIT".equalsIgnoreCase(request.trim())) {
                    req.handler.closeConnection();
                    TCPServer.logClientDisconnection(
                            addr,
                            req.handler.getConnectionTime(),
                            new Date()
                    );
                }
            } catch (Exception e) { e.printStackTrace(); }
        }
    }
}

class ClientHandler implements Runnable {
    private final Socket clientSocket;
    private final BufferedReader in;
    private final DataOutputStream out;
    private final String clientAddress;
    private final Date connectionTime;
    private String clientName = "UNKNOWN";

    public ClientHandler(
            Socket sock,
            BufferedReader in,
            DataOutputStream out,
            String addr,
            Date connTime
    ) {
        this.clientSocket   = sock;
        this.in             = in;
        this.out            = out;
        this.clientAddress  = addr;
        this.connectionTime = connTime;
    }

    public void run() {
        try {
            String line;
            while ((line = in.readLine()) != null) {
                if (line.toUpperCase().startsWith("JOIN ")) {
                    clientName = line.substring(5).trim();
                    String welcome = "WELCOME " + clientName;
                    out.writeBytes(welcome + "\n");
                    TCPServer.logClientActivity(clientAddress, line, welcome, new Date());
                    continue;
                }
                TCPServer.requestQueue.put(new Request(this, line, new Date()));
                if ("EXIT".equalsIgnoreCase(line.trim())) break;
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    public String getClientAddress() {
        return clientAddress + "/" + clientName;
    }

    public Date getConnectionTime() {
        return connectionTime;
    }

    public void sendResponse(String resp) throws IOException {
        out.writeBytes(resp + "\n");
    }

    public void closeConnection() throws IOException {
        clientSocket.close();
    }

    public String processMathRequest(String req) {
        String[] parts = req.trim().split("\\s+");
        if (parts.length < 3) return "Error: Invalid request format";
        String op = parts[0].toUpperCase();
        int a, b;
        try { a = Integer.parseInt(parts[1]); b = Integer.parseInt(parts[2]); }
        catch (NumberFormatException e) { return "Error: Non-integer operands"; }

        switch (op) {
            case "ADD": return Integer.toString(a + b);
            case "SUB": return Integer.toString(a - b);
            case "MUL": return Integer.toString(a * b);
            case "DIV": return b != 0 ? Integer.toString(a / b)
                    : "Error: Division by zero";
            case "EXIT": return "GOODBYE " + clientName;
            default:     return "Error: Invalid operation";
        }
    }
}
