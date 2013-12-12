package server;

import java.util.*;

/**
 * Manages the lifecycle of all whiteboards on the server.
 * Thread safety: Methods are synchronised to prevent threading issues.
 */
public class WhiteboardManager {
    private Map<String, Whiteboard> whiteboards = new HashMap<>();
    private Set<Client> clients = new HashSet<>();

    /**
     * @return An array of current whiteboard names, sorted in alphabetical order.
     */
    public synchronized String[] getWhiteboardNames() {
        Set<String> var = new TreeSet<>(whiteboards.keySet());
        return var.toArray(new String[var.size()]);
    }

    /**
     * @param name The name of a whiteboard to get
     * @return The whiteboard object, or null if there is no such whiteboard.
     */
    public synchronized Whiteboard getWhiteboard(String name) {
        return whiteboards.get(name);
    }

    /**
     * @param name The whiteboard being tested
     * @return True if the whiteboard existed, false otherwise.
     */
    public synchronized boolean hasWhiteboard(String name) {
        return whiteboards.containsKey(name);
    }

    /**
     * Creates a new whiteboard and informs all connected users of its creation.
     * The new whiteboard is stored in the manager's set of whiteboards and also
     * returned to the caller.
     * @param name The name of the new whiteboard
     * @return The newly-created whiteboard object.
     * @throws ClientException
     */
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

    /**
     * Adds a client to the client set, or throws an exception if it already exists.
     * @param client The client to add
     * @throws ClientException
     */
    public void addClient(Client client) throws ClientException {
        synchronized (clients) {
            if(clients.contains(client)) {
                throw new ClientException("Duplicate username.");
            }
            clients.add(client);
        }
    }

    /**
     * Removes a client from the client set
     * @param client The client to remove.
     */
    public void removeClient(Client client) {
        synchronized (clients) {
            clients.remove(client);
        }
    }
    
    /**
     * Gets the names of the clients currently connected to the manager.
     * Used primarily for testing
     * @return List<String> Names of clients
     */
    public List<String> getClientNames()
    {
    	synchronized (clients)
    	{
    		List<String> names = new ArrayList<String>();
    		for(Client c : clients)
    		{
    			names.add(c.username);
    		}
    		return names;
    	}
    }
}
