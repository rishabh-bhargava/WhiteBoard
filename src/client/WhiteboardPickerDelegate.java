package client;

/**
 * Delegate for the whiteboard picker.
 */
public interface WhiteboardPickerDelegate {
    /**
     * Called when the user picks an existing whiteboard from the list
     * @param name The name of the chosen whiteboard.
     */
    public void whiteboardPicked(String name);

    /**
     * Called when the user creates a new whiteboard. Note that no such whiteboard
     * is actually created by the picker; that is the delegate's responsibility.
     * @param name The name of the new whiteboard.
     */
    public void whiteboardCreated(String name);
}
