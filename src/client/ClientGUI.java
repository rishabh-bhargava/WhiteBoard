package client;

import shared.LineSegment;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.util.List;

import javax.swing.*;

public class ClientGUI extends JFrame 
{   

    private WhiteboardCanvasGroup canvas;
    private Toolbar toolbar;
    private WhiteboardBar topbar;
    
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
    
    public void setColor(Color c) {
        canvas.setColour(c);
    }
    public void setErasing(boolean b) {
        canvas.setErasing(b);
    }
    
    public void setBrushStroke(int num) {
        canvas.setBrushStroke(num);
    }

    public void setCanvasBitmap(byte[] bitmap) {
        canvas.setBitmap(bitmap);
    }

    public void setCanvasDelegate(CanvasDelegate delegate) {
        canvas.setDelegate(delegate);
    }

    public void drawLines(Color colour, float strokeWidth, List<LineSegment> segments) {
        canvas.drawLines(colour, strokeWidth, segments);
    }

    public void setWhiteboardName(String name) {
        topbar.setName(name);
    }

    public void clearDrawingCanvas() {
        canvas.clearDrawingCanvas();
    }
    
    /*
     * Instantiate GUI JFrame in a separate thread from main.
     */
    public static void main(final String[] args) {
        new WhiteboardController();
    }
    
    
}

