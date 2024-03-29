package server;

import java.io.*;
import java.util.*;
import java.net.*;

/**
 * Main class for the whiteboard server.
 * Thread safety: there is only one thread here.
 */
public class WhiteboardServer {
    private final ServerSocket serverSocket;
    private final WhiteboardManager manager = new WhiteboardManager();
    
    public WhiteboardServer(int port) throws IOException {
        serverSocket = new ServerSocket(port);
    }
    
    public void serve() throws IOException {
        //noinspection InfiniteLoopStatement
        while(true) {
            Socket socket = serverSocket.accept();
            Client client = new Client(manager, socket);
            client.start();
        }
    }
    
    public static void main(String[] args) {
        try {
            WhiteboardServer server = new WhiteboardServer(6005);
            server.serve();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
