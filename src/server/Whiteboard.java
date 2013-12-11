package server;

import shared.LineSegment;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.*;
import java.util.List;

import javax.xml.bind.DatatypeConverter;

/**
 * Represents a whiteboard, as seen by the server. Tracks its name, a user list and the current state of the canvas.
 */
public class Whiteboard {
    private final String name;
    private Set<Client> users = new TreeSet<>();
    private BufferedImage image = new BufferedImage(800, 600, BufferedImage.TYPE_4BYTE_ABGR);
    private Graphics2D graphics;

    /**
     * Creates a whiteboard with the given name.
     * @param name Whiteboard name.
     */
    public Whiteboard(String name) {
        this.name = name;
        System.out.println(getName());
        graphics = image.createGraphics();
        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, 0, image.getWidth(), image.getHeight());
    }

    /**
     * Cretes a base64-encoded version of the bitmap that is currently on this whiteboard.
     * Suitable for sending to clients.
     * @return The base64-encoded bitmap.
     */
    public synchronized String getSerializedImage() {
        DataBufferByte buffer = (DataBufferByte)image.getRaster().getDataBuffer();
        byte data[] = buffer.getData();
        return DatatypeConverter.printBase64Binary(data);
    }

    /**
     * @return The name of the whiteboard.
     */
    public String getName() {
        return name;
    }

    /**
     * Draws a line on the whiteboard. This both updates the representation of the whiteboard held in this object
     * and also transmits the new line to every client (including the client the command was presumably received from)
     * @param colour The colour to draw in
     * @param strokeSize The width of the stroke in pixels
     * @param segments The sequence of lines to draw.
     */
    public synchronized void draw(Color colour, float strokeSize, List<LineSegment> segments) {
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
            client.sendMessage(stringMessage);
        }
    }

    /**
     * Adds a client to the whiteboard, causing them to appear in the user list and receive messages
     * relevant to this whiteboard.
     * @param client The client to add
     */
    public synchronized void addUser(Client client) {
        for(Client user : users) {
            user.sendMessage("JOIN " + client.getUsername());
        }
        users.add(client);
    }

    /**
     * Removes a client from this whiteboard.
     * @param client The client to remove.
     */
    public synchronized void removeUser(Client client) {
        users.remove(client);
        for(Client user : users) {
            user.sendMessage("PART " + client.getUsername());
        }
    }

    /**
     * @return An array of Clients attached to this whiteboard.
     */
    public synchronized Client[] getUsers() {
        return users.toArray(new Client[users.size()]);
    }

    /**
     * @return An array of usernames of clients attached to this whiteboard.
     */
    public synchronized String[] getUserNames() {
        List<String> list = new LinkedList<>();
        for(Client user : users) {
            list.add(user.getUsername());
        }
        return list.toArray(new String[list.size()]);
    }
}
