import javax.net.ssl.SSLSocket;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Scanner;

public class ClientHandler implements Runnable {
    private SSLSocket socket;
    private BufferedReader br;
    private PrintWriter out;
    private Server server;
    private String clientId;
    private Scanner scanner;

    public ClientHandler(SSLSocket socket, Server server) {
        this.socket = socket;
        this.server = server;
        try {
            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            this.clientId = br.readLine(); // Get client ID from the first message
            System.out.println("Client " + clientId + " connected.");
            server.addClient(clientId, this);
            scanner =new Scanner(System.in);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            out.println("Type Your Message"); 
            String inputLine; 
            String[] parts = null;

            // Continue receiving messages from the client 
            while ((inputLine = br.readLine()) != null) { 
                parts = inputLine.split(" ", 2);

                System.out.println("[" + parts[0] + "]: " + parts[1]); // Use Username consistently
                System.out.print("Enter a message: ");
                String msgwrite = scanner.nextLine();
                this.sendMessage(msgwrite);
            } 

            // Remove the client handler from the list 
            server.removeClient(parts[0]);

            // Close the input and output streams and the client socket 
            br.close(); 
            out.close(); 
            socket.close();
            scanner.close();
        } catch (Exception e) {
            System.out.println("Connection with client " + clientId + " is closed!");
        }
    }

    public void broadcastMessage(String message) {
        for (ClientHandler client : server.getClients().values()) {
            client.sendMessage(message);
        }
    }    

    public void sendMessage(String message) {
        out.println(message);
        out.flush();
    }
}
