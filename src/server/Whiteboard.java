package server;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.*;

import javax.xml.bind.DatatypeConverter;

public class Whiteboard {
    private final String name;
    private Set<Client> users = new TreeSet<Client>();
    private BufferedImage image;
    
    public Whiteboard(String name) {
        this.name = name;
    }
    
    public synchronized String getSerializedImage() {
        DataBufferByte buffer = (DataBufferByte)image.getRaster().getDataBuffer();
        byte data[] = buffer.getData();
        return DatatypeConverter.printBase64Binary(data);
    }
    
    public String getName() {
        return name;
    }
    
    public synchronized void addUser(Client client) {
        users.add(client);
    }
    
    public synchronized void removeUser(Client client) {
        users.remove(client);
    }
    
    public synchronized Client[] getUsers() {
        return (Client[])users.toArray();
    }
    
    public synchronized String[] getUserNames() {
        List<String> list = new LinkedList<>();
        for(Client user : users) {
            list.add(user.getName());
        }
        return list.toArray(new String[0]);
    }
}
