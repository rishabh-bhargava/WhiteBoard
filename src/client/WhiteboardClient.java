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

public class WhiteboardClient extends Thread {
    private final String server;
    private final String username;
    private final Socket socket;
    private final WhiteboardClientDelegate delegate;
    private PrintWriter out;
    private int drawingSequenceNumber = 0;

    private SortedSet<String> whiteboards = new TreeSet<>();
    private SortedSet<String> users = new TreeSet<>();

    private boolean connected = false;

    public WhiteboardClient(WhiteboardClientDelegate delegate, String server, String username) {
        super();
        this.username = username;
        this.server = server;
        this.delegate = delegate;
        this.socket = new Socket();
        this.start();
    }

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

    private void handleHello(String[] args) {
        // args is a list of available whiteboards.
        whiteboards.clear();
        Collections.addAll(whiteboards, args);
        delegate.whiteboardListUpdated(whiteboards.toArray(new String[whiteboards.size()]));
    }

    private void handleACK(String[] args) {
        if(drawingSequenceNumber == Integer.parseInt(args[0])) {
            delegate.serverACK();
        }
    }

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

    private void handleJoin(String[] args) {
        users.add(args[0]);
        delegate.userListChanged(users.toArray(new String[users.size()]));
    }

    private void handlePart(String[] args) {
        users.remove(args[0]);
        delegate.userListChanged(users.toArray(new String[users.size()]));
    }

    private void handleCreated(String[] args) {
        whiteboards.add(args[0]);
        delegate.whiteboardListUpdated(whiteboards.toArray(new String[whiteboards.size()]));
    }

    private void handleError(String[] args) {
        StringBuilder parts = new StringBuilder();
        for(String arg : args) {
            parts.append(arg);
            parts.append(" ");
        }
        delegate.serverError(parts.toString().trim());
    }

    private String handleDraw(String[] args) {
        java.util.List<LineSegment> segments = new ArrayList<>();
        for(int i = 2; i < args.length; i += 4) {
            segments.add(new LineSegment(Integer.parseInt(args[i]), Integer.parseInt(args[i+1]), Integer.parseInt(args[i+2]), Integer.parseInt(args[i+3])));
        }
        Color colour = Color.decode(args[0]);
        float strokeSize = Float.parseFloat(args[1]);
        delegate.serverDrew(colour, strokeSize, segments);
        return "";
    }

    public void joinWhiteboard(String name) {
        sendMessage("JOIN", name);
    }

    public void createWhiteboard(String name) {
        sendMessage("CREATE", name);
    }

    public void sendLine(Color colour, float strokeWidth, int x1, int y1, int x2, int y2) {
        sendMessage("DRAW", Integer.toString(++drawingSequenceNumber), Integer.toString(colour.getRGB()), Float.toString(strokeWidth),
                Integer.toString(x1),  Integer.toString(y1),  Integer.toString(x2),  Integer.toString(y2));
    }

    public String[] getUsers() {
        return users.toArray(new String[users.size()]);
    }

    public String[] getWhiteboards() {
        return whiteboards.toArray(new String[whiteboards.size()]);
    }

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

    private void hello() {
        sendMessage("HELLO", this.username);
    }

    private void sendMessage(String command, String... args) {
        sendMessageWithArgs(command, args);
    }

    private void sendMessageWithArgs(String command, String[] args) {
        out.print(command);
        for(String arg : args) {
            out.print(" " + arg);
        }
        out.println();
    }
}
