package server;

import java.util.*;

public class WhiteboardManager {
    private Map<String, Whiteboard> whiteboards = new HashMap<>();
    private Set<Client> clients = new HashSet<>();
    
    public synchronized String[] getWhiteboardNames() {
        Set<String> var = whiteboards.keySet();
        return var.toArray(new String[var.size()]);
    }
    
    public synchronized Whiteboard getWhiteboard(String name) {
        return whiteboards.get(name);
    }
    
    public synchronized boolean hasWhiteboard(String name) {
        return whiteboards.containsKey(name);
    }
    
    public synchronized Whiteboard createWhiteboard(String name) throws ClientException {
        if(whiteboards.containsKey(name)) {
            throw new ClientException("Duplicate whiteboard name.");
        }
        Whiteboard whiteboard = new Whiteboard(name);
        whiteboards.put(name, whiteboard);
        for(Whiteboard board : whiteboards.values()) {
            for(Client user : board.getUsers()) {
                user.sendMessage("CREATED " + whiteboard.getName());
            }
        }
        return whiteboard;
    }

    public synchronized void addClient(Client client) throws ClientException {
        if(clients.contains(client)) {
            throw new ClientException("Duplicate username.");
        }
        clients.add(client);
    }

    public synchronized  void removeClient(Client client) {
        clients.remove(client);
    }
}
