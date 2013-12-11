package server;
import shared.LineSegment;

import java.awt.Color;
import java.io.*;
import java.util.*;
import java.net.*;

/**
 * Represents a client from the server's perspective, and handles all
 * network communication with it.
 * Thread safety:
 *   - All incoming messages are on a single thread.
 *   - Sending outgoing messages is protected by a mutex to prevent mingled messages.
 *   - There is no externally mutable state aside from message sending.
 */
public class Client extends Thread implements Comparable<Client> {
    private final Socket socket;
    private final WhiteboardManager manager;
    private boolean connected = true; // The connection dies when this becomes false.
    private String username = null;
    private Whiteboard whiteboard;
    private PrintWriter out = null;

    public Client(WhiteboardManager manager, Socket socket) {
        this.socket = socket;
        this.manager = manager;
    }

    @Override
    public void run() {
        BufferedReader in = null;
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            for (String line = in.readLine(); line != null; line = in.readLine()) {
                String output = null;
                try {
                    output = handleMessage(line);
                } catch(ClientException e) {
                    out.println("ERROR " + e.getMessage());
                }
                if (output != null) {
                    sendMessage(output);
                }
                if(!connected) {
                    break;
                }
            }
        } catch(IOException e) {
            
        } finally {
            manager.removeClient(this);
            if(whiteboard != null){
                whiteboard.removeUser(this);
            }
            if(out != null) out.close();
            if(in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Sends a message to the client
     * @param message The message to send.
     */
    public void sendMessage(String message) {
        synchronized(out) {
            out.println(message);
        }
    }

    /**
     * Handles a message from the client.
     * @param line The message received.
     * @return The response to send.
     * @throws ClientException
     */
    private String handleMessage(String line) throws ClientException {
        if(line == null) {
            // Why would this ever happen?
            return null;
        }
        if(line.equals("")) {
            // Ignore blank lines.
            return null;
        }
        String components[] = line.split("\\s+");
        String command = components[0];
        String args[] = new String[0];
        if(components.length > 1) {
            args = Arrays.copyOfRange(components, 1, components.length);
        }
        
        switch(command.toLowerCase()) {
        case "hello":
            return handleHello(args);
        case "quit":
            return handleQuit(args);
        case "join":
            return handleJoin(args);
        case "create":
            return handleCreate(args);
        case "draw":
            return handleDraw(args);
        default:
            return handleUnknownCommand(command);
        }
    }

    /**
     * Handles a HELLO message from the client. Such messages may only be received once.
     * @param args The client's username.
     * @return A message indicating the available whiteboards.
     * @throws ClientException
     */
    private String handleHello(String args[]) throws ClientException {
        if(username != null) {
            throw new ClientException("Already said hello.");
        }
        if(args.length < 1) {
            throw new ClientException("Must provide a username to HELLO.");
        }
        username = args[0];
        manager.addClient(this);
        return "HELLO " + strJoin(manager.getWhiteboardNames());
    }

    /**
     * Handles a client indicating it wishes to disconnect
     * @param args Ignored.
     * @return "GOODBYE"
     */
    private String handleQuit(String args[]) {
        connected = false;
        return "GOODBYE";
    }

    /**
     * Handles a JOIN message from the client.
     * @param args One entry: The name of the whiteboard the client wishes to join
     * @return The whiteboard's name, bitmap, and membership.
     * @throws ClientException
     */
    private String handleJoin(String args[]) throws ClientException {
        checkRegistered();
        if(args.length < 1) {
            throw new ClientException("Must provide whiteboard to join.");
        }
        String whiteboardName = args[0];
        synchronized(manager) {
            if(!manager.hasWhiteboard(whiteboardName)) {
                throw new ClientException("No such whiteboard.");
            }
            if(whiteboard != null) {
                whiteboard.removeUser(this);
            }
            whiteboard = manager.getWhiteboard(whiteboardName);
            whiteboard.addUser(this);
            return "WHITEBOARD " + whiteboard.getName() + " " + whiteboard.getSerializedImage() + " " + strJoin(whiteboard.getUserNames());
        }
    }

    /**
     * Handles a CREATE message from the client.
     * @param args One entry: the name of a whiteboard to create
     * @return The whiteboard's name, bitmap, and membership.
     * @throws ClientException
     */
    private String handleCreate(String args[]) throws ClientException {
        checkRegistered();
        if(args.length < 1) {
            throw new ClientException("Must specify a whiteboard name.");
        }
        synchronized(manager) {
            Whiteboard oldWhiteboard = whiteboard;
            whiteboard = manager.createWhiteboard(args[0]);
            if(oldWhiteboard != null) {
                oldWhiteboard.removeUser(this);
            }
            whiteboard.addUser(this);
        }
        return "WHITEBOARD " + whiteboard.getName() + " " + whiteboard.getSerializedImage() + " " + strJoin(whiteboard.getUserNames());
    }

    /**
     * Handles a DRAW message from the client.
     * @param args DRAW arguments; see protocol spec.
     * @return ACK containing the sequence number indicated in the client's message.
     * @throws ClientException
     */
    private String handleDraw(String[] args) throws ClientException {
        if(args.length < 3) {
            throw new ClientException("Must specify a colour, stroke size and sequence number.");
        }
        if(args.length % 4 != 3) {
            throw new ClientException("Must specify a set of start/end coordinate pairs");
        }
        List<LineSegment> segments = new ArrayList<>();
        for(int i = 3; i < args.length; i += 4) {
            segments.add(new LineSegment(Integer.parseInt(args[i]), Integer.parseInt(args[i+1]), Integer.parseInt(args[i+2]), Integer.parseInt(args[i+3])));
        }
        Color colour = Color.decode(args[1]);
        float strokeSize = Float.parseFloat(args[2]);
        whiteboard.draw(colour, strokeSize, segments);
        return "ACK " + args[0];
    }

    /**
     * Handles unknown commands and responds with errors
     * @param command The unknown command
     * @return An error message.
     */
    private String handleUnknownCommand(String command) {
        return "ERROR " + command + " not recognised.";
    }
    
    // Why can't Java do this?

    /**
     * Joins an array with spaces.
     * @param aArr The array
     * @return The string joined with spaces.
     */
    private static String strJoin(String[] aArr) {
        StringBuilder sbStr = new StringBuilder();
        for (int i = 0, il = aArr.length; i < il; i++) {
            if (i > 0)
                sbStr.append(" ");
            sbStr.append(aArr[i]);
        }
        return sbStr.toString();
    }

    /**
     * Checks if this client has registered with a successful HELLO message, and throws a ClientException
     * if it has not.
     * @throws ClientException
     */
    private void checkRegistered() throws ClientException {
        if(username == null) {
            throw new ClientException("Must register before using command.");
        }
    }

    /**
     * @return The client's unique username.
     */
    public String getUsername() {
        return username;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((username == null) ? 0 : username.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Client other = (Client) obj;
        if (username == null) {
            if (other.username != null) {
                return false;
            }
        } else if (!username.equals(other.username)) {
            return false;
        }
        return true;
    }

    @Override
    public int compareTo(Client o) {
        return username.compareTo(o.username);
    }
}