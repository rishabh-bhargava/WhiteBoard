package client;

import shared.LineSegment;

import javax.xml.bind.DatatypeConverter;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.*;
import java.util.List;

/**
 * Handles communication with the server. Runs in its own thread to avoid blocking anything.
 */
public class WhiteboardClient extends Thread {
    private final String server; // server conncetion string
    private final String username; // client username
    private final Socket socket; // server socket
    private final WhiteboardClientDelegate delegate; // delegate to call methods on
    private PrintWriter out; // Thing to write to to get messages to the server
    private int drawingSequenceNumber = 0; // local sequence number for drawing operations.

    private SortedSet<String> whiteboards = new TreeSet<>(); // Set of known whiteboards
    private SortedSet<String> users = new TreeSet<>(); // Set of users on the current whiteboard

    private boolean connected = false; // Whether we *should* be connected to the server.

    /**
     * Creates the whiteboard client. The client will attempt to connect immediately upon creation,
     * but creation is non-blocking.
     * @param delegate The delegate to fire events to. Cannot be null.
     * @param server The server connection string (can be server, server:port, :port, or blank). Not null.
     * @param username The user's proposed username.
     */
    public WhiteboardClient(WhiteboardClientDelegate delegate, String server, String username) {
        super();
        this.username = username;
        this.server = server;
        this.delegate = delegate;
        this.socket = new Socket();
        this.start();
    }

    /**
     * @return The user's username.
     */
    public String getUsername() {
        return username;
    }

    @Override
    public void run() {
        connect();
        hello();
        BufferedReader in = null;
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            connected = false;
            delegate.serverConnectionFailed(e);
        }
        while(connected) {
            String message = null;
            try {
                message = in.readLine();
            } catch (IOException e) {
                connected = false;
            }
            if(message != null) {
                handleMessage(message);
            }
        }
        try {
            socket.close();
        } catch(IOException e) 
        {
            e.printStackTrace();
        }
    }

    /**
     * Handles received messages from the server, in raw form.
     * @param message The line from the server.
     */
    private void handleMessage(String message) {
        if(message == null) return;
        if(message.isEmpty()) return;
        String components[] = message.split("\\s+");
        String command = components[0];
        String args[] = new String[0];
        if(components.length > 1) {
            args = Arrays.copyOfRange(components, 1, components.length);
        }
        switch(command.toLowerCase()) {
            case "hello":
                handleHello(args);
                break;
            case "whiteboard":
                handleWhiteboard(args);
                break;
            case "error":
                handleError(args);
                break;
            case "draw":
                handleDraw(args);
                break;
            case "ack":
                handleACK(args);
                break;
            case "created":
                handleCreated(args);
                break;
            case "join":
                handleJoin(args);
                break;
            case "part":
                handlePart(args);
                break;
        }
    }

    /**
     * Handles received HELLO messages
     * @param args An array of whiteboard names.
     */
    private void handleHello(String[] args) {
        // args is a list of available whiteboards.
        whiteboards.clear();
        Collections.addAll(whiteboards, args);
        delegate.whiteboardListUpdated(whiteboards.toArray(new String[whiteboards.size()]));
        
    }

    /**
     * Handles ACK messages in response to PAINT messages.
     * @param args One element containing the sequence number being ACKed, as a string.
     */
    private void handleACK(String[] args) {
        if(drawingSequenceNumber == Integer.parseInt(args[0])) {
            delegate.serverACK();
        }
    }

    /**
     * Handles WHITEBOARD messages on joining a new whiteboard
     * @param args In order: the whiteboard's name, a base64-encoded bitmap, and a list of all other users on it.
     */
    private void handleWhiteboard(String[] args) {
        String name = args[0];
        byte[] bitmap = DatatypeConverter.parseBase64Binary(args[1]);
        String others[] = Arrays.copyOfRange(args, 2, args.length);
        whiteboards.add(args[0]); // This is harmless if it's already there; sets have no duplicates.
        users.clear();
        Collections.addAll(users, others);
        String[] whiteboardarray = getWhiteboards();
        List<String> whiteboardlist = new ArrayList<String>(Arrays.asList(whiteboardarray)); 
        delegate.joinedWhiteboard(name, bitmap, others, whiteboardlist);
    }

    /**
     * Handles JOIN messages indicating a user joined the current whiteboard.
     * @param args One element: the name of the user who joined.
     */
    private void handleJoin(String[] args) {
        users.add(args[0]);
        delegate.userListChanged(users.toArray(new String[users.size()]));
    }

    /**
     * Handles PART messages indicating a user left the current whiteboard.
     * @param args One element: the name of the user who left.
     */
    private void handlePart(String[] args) {
        users.remove(args[0]);
        delegate.userListChanged(users.toArray(new String[users.size()]));
    }

    /**
     * Handled CREATED messages indicating a new whiteboard was created.
     * @param args One element: the name of the created whiteboard.
     */
    private void handleCreated(String[] args) {
        whiteboards.add(args[0]);
        delegate.whiteboardListUpdated(whiteboards.toArray(new String[whiteboards.size()]));
    }

    /**
     * Handles ERROR messages, sent when the client tries to perform some erroneous operation.
     * ERRORs are generally recoverable.
     * @param args An array containing the space-separated components of the error message.
     */
    private void handleError(String[] args) {
        StringBuilder parts = new StringBuilder();
        for(String arg : args) {
            parts.append(arg);
            parts.append(" ");
        }
        delegate.serverError(parts.toString().trim());
    }

    /**
     * Handles DRAW messages, sent when any client (including this one) has drawn on the whiteboard.
     * @param args In order: a numeric representation of the colour, the stroke width in pixels, then a list of points to
     *             connect in the format (x1, y1, x2, y2, ...). At least two coordinate pairs should be included.
     */
    private void handleDraw(String[] args) {
        java.util.List<LineSegment> segments = new ArrayList<>();
        for(int i = 2; i < args.length; i += 4) {
            segments.add(new LineSegment(Integer.parseInt(args[i]), Integer.parseInt(args[i+1]), Integer.parseInt(args[i+2]), Integer.parseInt(args[i+3])));
        }
        Color colour = Color.decode(args[0]);
        float strokeSize = Float.parseFloat(args[1]);
        delegate.serverDrew(colour, strokeSize, segments);
    }

    /**
     * Makes the client join the specified whiteboard, implicitly parting the current one, if any.
     * Relevant methods will be called on the delegate when the operation is processed on the server.
     * @param name The name of the whiteboard to join.
     */
    public void joinWhiteboard(String name) {
        sendMessage("JOIN", name);
    }

    /**
     * Requests the creation of the specified whiteboard, implicitly joining it and parting the current one, if any.
     * Relevant methods will be called on the delegate when the operation is processed on the server.
     * @param name The name of the whiteboard to create.
     */
    public void createWhiteboard(String name) {
        sendMessage("CREATE", name);
    }

    /**
     * Send a drawn line to the server.
     * @param colour The colour of the line
     * @param strokeWidth The width of the line, in pixels
     * @param x1 x-coordinate of the start point
     * @param y1 y-coordinate of the start point
     * @param x2 x-coordinate of the end point
     * @param y2 y-coordinate of the end point
     */
    public void sendLine(Color colour, float strokeWidth, int x1, int y1, int x2, int y2) {
        sendMessage("DRAW", Integer.toString(++drawingSequenceNumber), Integer.toString(colour.getRGB()), Float.toString(strokeWidth),
                Integer.toString(x1),  Integer.toString(y1),  Integer.toString(x2),  Integer.toString(y2));
    }

    /**
     * @return Returns an array of the usernames of users in the current whiteboard.
     */
    public String[] getUsers() {
        return users.toArray(new String[users.size()]);
    }

    /**
     * @return Returns an array of the names of all known whiteboards.
     */
    public String[] getWhiteboards() {
        return whiteboards.toArray(new String[whiteboards.size()]);
    }

    /**
     * Connects to the server.
     */
    private void connect() {
        String hostname = "localhost";
        int port = 6005;
        if(server.startsWith(":") && server.length() > 1) {
            port = Integer.parseInt(server.substring(1));
        } else if(!server.isEmpty()) {
            String[] parts = server.split(":");
            hostname = parts[0];
            if(parts.length > 1) {
                port = Integer.parseInt(parts[1]);
            }
        }
        SocketAddress address = new InetSocketAddress(hostname, port);
        try {
            socket.connect(address);
            try {
                out = new PrintWriter(socket.getOutputStream(), true);
                connected = true;
            } catch (IOException e) {
                connected = false;
                delegate.serverConnectionFailed(e);
            }
        } catch (IOException e) {
            connected = false;
            delegate.serverConnectionFailed(e);
        }
    }

    /**
     * Sends HELLO message to the server, thereby setting the username.
     */
    private void hello() {
        sendMessage("HELLO", this.username);
    }

    /**
     * Sends a message to the server
     * @param command The command to send
     * @param args Arguments to attach to the command, if any.
     */
    private void sendMessage(String command, String... args) {
        sendMessageWithArgs(command, args);
    }

    /**
     * Sends a message to the server
     * @param command The command to send
     * @param args Aarguments to attach to the command, if any.
     */
    private void sendMessageWithArgs(String command, String[] args) {
        out.print(command);
        for(String arg : args) {
            out.print(" " + arg);
        }
        out.println();
    }
}
