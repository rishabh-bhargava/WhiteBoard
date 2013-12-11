package client;

import shared.LineSegment;

import java.awt.*;
import java.io.IOException;
import java.util.List;

/**
 * Delegate used by the WhiteboardClient to notify an interested party of changes.
 */
public interface WhiteboardClientDelegate {
    /**
     * Called when a connection to the server fails.
     * @param e The exception thrown on failure.
     */
    public void serverConnectionFailed(IOException e);

    /**
     * Called when the list of available whiteboards is updated. Note that this method may be called
     * without the set of whiteboards having actually changed.
     * @param boards Array of the names of all boards that exist.
     */
    public void whiteboardListUpdated(String[] boards);

    /**
     * Called when the client joins a new whiteboard (and, implicitly, left one it was already on).
     * @param whiteboard The name of the whiteboard joined
     * @param bitmap A bitmap representing the current content of the whiteboard, in 4-byte ARGB format.
     * @param usernames An array of usernames on the current board.
     * @param whiteboards A list of whiteboards that exist.
     */
    public void joinedWhiteboard(String whiteboard, byte bitmap[], String usernames[], List<String> whiteboards);

    /**
     * Called when the server indicates that someone drew on the whiteboard
     * @param colour The colour of the line
     * @param strokeWidth The width of the line, in pixels
     * @param segments A list of line segments that were drawn.
     */
    public void serverDrew(Color colour, float strokeWidth, List<LineSegment> segments);

    /**
     * Called when the server flags an error. Such errors are usually recoverable, in principle.
     * @param message The error message provided by the server.
     */
    public void serverError(String message);

    /**
     * Called when (and only when) an ACK message from the server indicates that the server's view of our
     * drawing is current. Not called if outdated ACKs are received.
     */
    public void serverACK();

    /**
     * Called when the set of users on the current whiteboard changes.
     * @param list
     */
    public void userListChanged(String[] list);
}
