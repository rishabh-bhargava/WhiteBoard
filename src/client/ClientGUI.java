package client;

import shared.LineSegment;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.util.List;

import javax.swing.*;

/**
 * Provides the main GUI window.
 * Thread safety: This has no state to directly manipulate, so cannot be unsafe.
 */
public class ClientGUI extends JFrame 
{   

    private WhiteboardCanvasGroup canvas;
    private Toolbar toolbar;
    private WhiteboardBar topbar;
    private CanvasDelegate delegate;
    
    public ClientGUI() {
        super("Whiteboard");
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        Container cp = this.getContentPane();
        BorderLayout layout = new BorderLayout();
        cp.setLayout(layout);
        
        canvas = new WhiteboardCanvasGroup();
        this.add(canvas, BorderLayout.CENTER);

        toolbar = new Toolbar(this);
        this.add(toolbar, BorderLayout.EAST);

        topbar = new WhiteboardBar();
        this.add(topbar, BorderLayout.NORTH);
        
        this.pack();
    }
    
    /**
     * Sets the canvas colour as the input colour
     * @param c
     */
    public void setColor(Color c) {
        canvas.setColour(c);
    }
    
    /**
     * Sets the canvas to erasing mode
     * @param b
     */
    public void setErasing(boolean b) {
        canvas.setErasing(b);
    }
    
    /**
     * Sets the brush stroke size for the canvas with the input integer
     * @param num
     */
    public void setBrushStroke(int num) {
        canvas.setBrushStroke(num);
    }
    
    /**
     * Sets the canvas bitmap with the input bitmap
     * @param bitmap
     */
    public void setCanvasBitmap(byte[] bitmap) {
        canvas.setBitmap(bitmap);
    }
    
    /**
     * Sets the canvas delegate with the input CanvasDelegate
     * @param delegate
     */
    public void setCanvasDelegate(CanvasDelegate delegate) {
        this.delegate = delegate;
        canvas.setDelegate(delegate);
        topbar.setDelegate(delegate);
    }
    
    /**
     * Draws lines on the canvas with the input colour, strokesize and list of line segments
     * @param colour
     * @param strokeWidth
     * @param segments
     */
    public void drawLines(Color colour, float strokeWidth, List<LineSegment> segments) 
    {
        canvas.drawLines(colour, strokeWidth, segments);
    }
    
    /**
     * Sets the whiteboard name in the whiteboard bar as the input string
     * @param name
     */
    public void setWhiteboardName(String name) 
    {
        topbar.setWhiteboardName(name);
    }
    
    /**
     * Clears the drawing canvas
     */
    public void clearDrawingCanvas() 
    {
        canvas.clearDrawingCanvas();
    }
    
    /**
     * Sets the list of current users in the topbar
     * @param users : List of current users
     */
    public void setUserList(List<String> users)
    {
    	toolbar.setUserList(users);
    }
    
    public void setWhiteboardsList(String[] whiteboards)
    {
    	topbar.setWhiteboardsList(whiteboards);
    }
    
    /*
     * Instantiate GUI JFrame in a separate thread from main.
     */
    public static void main(final String[] args) 
    {
        new WhiteboardController();
    }
    
    
}

