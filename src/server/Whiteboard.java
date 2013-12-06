package server;

import shared.LineSegment;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.*;
import java.util.List;

import javax.xml.bind.DatatypeConverter;

public class Whiteboard {
    private final String name;
    private Set<Client> users = new TreeSet<>();
    private BufferedImage image = new BufferedImage(800, 600, BufferedImage.TYPE_3BYTE_BGR);
    private Graphics2D graphics;
    
    public Whiteboard(String name) {
        this.name = name;
        System.out.println(getName());
        graphics = image.createGraphics();
        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, 0, image.getWidth(), image.getHeight());
    }
    
    public synchronized String getSerializedImage() {
        DataBufferByte buffer = (DataBufferByte)image.getRaster().getDataBuffer();
        byte data[] = buffer.getData();
        return DatatypeConverter.printBase64Binary(data);
    }
    
    public String getName() {
        return name;
    }
    
    public synchronized void draw(Color colour, float strokeSize, List<LineSegment> segments, Client sender) {
        StringBuilder message = new StringBuilder();
        message.append("DRAW ");
        message.append(colour.getRGB());
        message.append(" ");
        message.append(Float.toString(strokeSize));
        graphics.setColor(colour);
        graphics.setStroke(new BasicStroke(strokeSize));
        for(LineSegment segment : segments) {
            graphics.drawLine(segment.x1, segment.y1, segment.x2, segment.y2);
            message.append(" ");
            message.append(segment.x1);
            message.append(" ");
            message.append(segment.y1);
            message.append(" ");
            message.append(segment.x2);
            message.append(" ");
            message.append(segment.y2);
        }
        String stringMessage = message.toString();
        for(Client client : users) {
            if(client == sender) continue;
            client.sendMessage(stringMessage);
        }
    }
    
    public synchronized void addUser(Client client) {
        for(Client user : users) {
            user.sendMessage("JOIN " + client.getUsername());
        }
        users.add(client);
    }
    
    public synchronized void removeUser(Client client) {
        users.remove(client);
        for(Client user : users) {
            user.sendMessage("PART " + client.getUsername());
        }
    }
    
    public synchronized Client[] getUsers() {
        return (Client[])users.toArray();
    }
    
    public synchronized String[] getUserNames() {
        List<String> list = new LinkedList<>();
        for(Client user : users) {
            list.add(user.getUsername());
        }
        return list.toArray(new String[list.size()]);
    }
}