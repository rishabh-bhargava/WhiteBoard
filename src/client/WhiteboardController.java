package client;

import shared.LineSegment;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.List;

public class WhiteboardController implements WhiteboardClientDelegate, WhiteboardPickerDelegate, CanvasDelegate {
    private WhiteboardClient client;
    private WhiteboardPicker picker;
    private ClientGUI gui = null;
    private boolean ready = false;

    public WhiteboardController() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                driveWhiteboard();
            }
        });
    }

    private void driveWhiteboard() {
        String server = JOptionPane.showInputDialog("Give the server address", "localhost:6005");
        String username = JOptionPane.showInputDialog("Enter a username");
        client = new WhiteboardClient(this, server, username);
    }

    @Override
    public void serverConnectionFailed(IOException e) {
        JOptionPane.showMessageDialog(null, "Unable to connect to server: " + e.getMessage(), "Server connection failed", JOptionPane.ERROR_MESSAGE);
    }

    @Override
    public void whiteboardListUpdated(String[] boards) {
        if(!ready && gui == null) {
            System.out.println("Got some boards");
            picker = new WhiteboardPicker(this, boards);
        }
    }

    @Override
    public void joinedWhiteboard(String whiteboard, byte[] bitmap, String[] usernames) {
        if(!ready && gui == null) {
            ready = true;
            gui = new ClientGUI();
            gui.setVisible(true);
            gui.setCanvasBitmap(bitmap);
            gui.setCanvasDelegate(this);
            gui.setWhiteboardName(whiteboard);
            List<String> list = new ArrayList<>();
            Collections.addAll(list, client.getUsers());
            gui.setUserList(list);
        }
    }

    @Override
    public void userListChanged(String[] userList) {
        if(gui != null) {
            List<String> list = new ArrayList<>();
            Collections.addAll(list, userList);
            gui.setUserList(list);
        }
    }

    @Override
    public void serverDrew(Color colour, float strokeWidth, List<LineSegment> segments) {
        if(gui == null) return;
        gui.drawLines(colour, strokeWidth, segments);
    }

    @Override
    public void serverError(String message) {
        JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
        if(!ready) {
            System.exit(1);
        }
    }

    @Override
    public void serverACK() {
        gui.clearDrawingCanvas();
    }

    @Override
    public void whiteboardPicked(String name) {
        client.joinWhiteboard(name);
    }

    @Override
    public void whiteboardCreated(String name) {
        client.createWhiteboard(name);
    }

    @Override
    public void drewLine(Color colour, float strokeWidth, int x1, int y1, int x2, int y2) {
        client.sendLine(colour, strokeWidth, x1, y1, x2, y2);
    }
}

