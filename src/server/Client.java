package server;
import java.awt.Color;
import java.io.*;
import java.util.*;
import java.net.*;

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
                    output = handleRequest(line);
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
//            server.removeClient(this);
        }
    }
    
    public synchronized void sendMessage(String message) {
        out.println(message);
    }
    
    public String handleRequest(String line) throws ClientException {
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
    
    private String handleHello(String args[]) throws ClientException {
        if(username != null) {
            throw new ClientException("Already said hello.");
        }
        if(args.length < 1) {
            throw new ClientException("Must provide a username to HELLO.");
        }
        username = args[0];
        return "HELLO " + strJoin(manager.getWhiteboardNames(), " ");
    }
    
    private String handleQuit(String args[]) {
        connected = false;
        return "GOODBYE";
    }
    
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
            whiteboard = manager.getWhiteboard(whiteboardName);
            whiteboard.addUser(this);
            return "WHITEBOARD " + whiteboard.getSerializedImage() + " " + strJoin(whiteboard.getUserNames(), " ");
        }
    }
    
    private String handleCreate(String args[]) throws ClientException {
        checkRegistered();
        if(args.length < 1) {
            throw new ClientException("Must specify a whiteboard name.");
        }
        synchronized(manager) {
            Whiteboard oldWhiteboard = whiteboard;
            whiteboard = manager.createWhiteboard(args[0]);
            whiteboard.addUser(this);
            if(oldWhiteboard != null) {
                whiteboard.removeUser(this);
            }
        }
        return "WHITEBOARD " + whiteboard.getSerializedImage() + " " + strJoin(whiteboard.getUserNames(), " ");
    }
    
    private String handleDraw(String[] args) throws ClientException {
        if(args.length < 2) {
            throw new ClientException("Must specify a colour and stroke size.");
        }
        if(args.length % 4 != 2) {
            throw new ClientException("Must specify a set of start/end coordinate pairs");
        }
        List<LineSegment> segments = new ArrayList<>();
        for(int i = 2; i < args.length; i += 4) {
            segments.add(new LineSegment(Integer.parseInt(args[i]), Integer.parseInt(args[i+1]), Integer.parseInt(args[i+2]), Integer.parseInt(args[i+3])));
        }
        Color colour = Color.decode(args[0]);
        float strokeSize = Float.parseFloat(args[1]);
        whiteboard.draw(colour, strokeSize, segments, this);
        return "";
    }
    
    private String handleUnknownCommand(String command) {
        return "ERROR " + command + " not recognised.";
    }
    
    // Why can't Java do this?
    public static String strJoin(String[] aArr, String sSep) {
        StringBuilder sbStr = new StringBuilder();
        for (int i = 0, il = aArr.length; i < il; i++) {
            if (i > 0)
                sbStr.append(sSep);
            sbStr.append(aArr[i]);
        }
        return sbStr.toString();
    }
    
    private void checkRegistered() throws ClientException {
        if(username == null) {
            throw new ClientException("Must register before using command.");
        }
    }
    
    
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