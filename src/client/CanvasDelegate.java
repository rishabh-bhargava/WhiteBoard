package client;

import java.awt.*;

/**
 * Delegate used by the GUI to indicate that GUI events occurred.
 */
public interface CanvasDelegate {
    /**
     * Called when the user draws a line on the canvas
     * @param colour The colour of the line drawn
     * @param strokeWidth The width of the line drawn, in pixels
     * @param x1 Start point x coordinate
     * @param y1 Start point y coordinate
     * @param x2 End point x coordinate
     * @param y2 End point y coordinate
     */
    public void drewLine(Color colour, float strokeWidth, int x1, int y1, int x2, int y2);

    /**
     * Called when the user requests a change to a different whiteboard
     * @param newWhiteboard The name of the requested whiteboard
     */
    public void requestedWhiteboardChange(String newWhiteboard);

    /**
     * Called when the user has requested that a whiteboard be created.
     * @param name The name of the new whiteboard.
     */
	public void requestedWhiteboardCreation(String name);
}
