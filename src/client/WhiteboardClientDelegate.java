package client;

import shared.LineSegment;

import java.awt.*;
import java.io.IOException;
import java.util.List;

public interface WhiteboardClientDelegate {
    public void serverConnectionFailed(IOException e);
    public void whiteboardListUpdated(String[] boards);
    public void joinedWhiteboard(String whiteboard, byte bitmap[], String usernames[], List<String> whiteboards);
    public void serverDrew(Color colour, float strokeWidth, List<LineSegment> segments);
    public void serverError(String message);
    public void serverACK();
    public void userListChanged(String[] list);
}
