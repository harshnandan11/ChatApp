import javax.net.ssl.*;
import java.io.*;
import java.security.KeyStore;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class Server {
    private SSLServerSocket serverSocket;
    private ConcurrentMap<String, ClientHandler> clients = new ConcurrentHashMap<>();

    public Server() {
        try {
            // Load the KeyStore
            KeyStore keyStore = KeyStore.getInstance("JKS");
            keyStore.load(new FileInputStream("keystore.jks"), "hrSH8282".toCharArray());

            // Initialize KeyManagerFactory
            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            keyManagerFactory.init(keyStore, "hrSH8282".toCharArray());

            // Initialize SSLContext
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(keyManagerFactory.getKeyManagers(), null, null);

            // Create SSLServerSocket
            SSLServerSocketFactory sslServerSocketFactory = sslContext.getServerSocketFactory();
            serverSocket = (SSLServerSocket) sslServerSocketFactory.createServerSocket(7777);

            System.out.println("SSL server is ready to accept connections");
            System.out.println("Waiting...");

            while (true) {
                SSLSocket socket = (SSLSocket) serverSocket.accept();
                ClientHandler clientHandler = new ClientHandler(socket, this);
                new Thread(clientHandler).start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addClient(String clientId, ClientHandler clientHandler) {
        clients.put(clientId, clientHandler);
    }

    public void removeClient(String clientId) {
        clients.remove(clientId);
    }

    public ClientHandler getClientHandler(String clientId) {
        return clients.get(clientId);
    }

    public ConcurrentMap<String, ClientHandler> getClients() {
        return clients;
    }

    public static void main(String[] args) {
        System.out.println("This is the server...going to start the server");
        new Server();
    }
}
