package server;

import java.util.*;

public class WhiteboardManager {
    private Map<String, Whiteboard> whiteboards = new HashMap<>();
    
    public synchronized String[] getWhiteboardNames() {
        return whiteboards.keySet().toArray(new String[0]);
    }
    
    public synchronized Whiteboard getWhiteboard(String name) {
        return whiteboards.get(name);
    }
    
    public synchronized boolean hasWhiteboard(String name) {
        return whiteboards.containsKey(name);
    }
    
    public synchronized Whiteboard createWhiteboard(String name) {
        Whiteboard whiteboard = new Whiteboard(name);
        whiteboards.put(name, whiteboard);
        return whiteboard;
    }
}
