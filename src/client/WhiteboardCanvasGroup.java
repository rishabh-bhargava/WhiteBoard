package client;

import shared.LineSegment;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class WhiteboardCanvasGroup extends JLayeredPane 
{
    private final Canvas backingCanvas;
    private final Canvas drawingCanvas;
    
    /**
     * Constructor for creating the WhiteboardCanvasGroup
     */
    WhiteboardCanvasGroup() {
        backingCanvas = new Canvas(800, 600, true);
        drawingCanvas = new Canvas(800, 600, false);

        add(drawingCanvas, 2);
        add(backingCanvas, 1);

        Insets insets = getInsets();
        backingCanvas.setBounds(insets.left, insets.top, 800, 600);
        drawingCanvas.setBounds(0, 0, 800, 600);
        setSize(800, 600);
        setPreferredSize(new Dimension(800, 600));
        setMaximumSize(new Dimension(800, 600));
        setMinimumSize(new Dimension(800, 600));
        setOpaque(false);
    }
    
    /**
     * Sets the colour of the drawing canvas as the input colour
     * @param colour
     */
    public void setColour(Color colour) {
        drawingCanvas.setColour(colour);
    }
    
    /**
     * Sets the drawing canvas to erasing mode
     * @param erasing
     */
    public void setErasing(boolean erasing) {
        drawingCanvas.isErasing(erasing);
    }
    
    /**
     * Sets the brushstroke of the drawing canvas size with the input integer
     * @param num
     */
    public void setBrushStroke(int num) {
        drawingCanvas.setBrushStroke(num);
    }
    
    /**
     * Sets the delegate of the drawing canvas as the input CanvasDelegate
     * @param delegate
     */
    public void setDelegate(CanvasDelegate delegate) {
        drawingCanvas.setDelegate(delegate);
    }
    
    /**
     * Draws lines on the backing canvas with the required colour, stroke width and list of line segments
     * @param colour
     * @param strokeWidth
     * @param segments
     */
    public void drawLines(Color colour, float strokeWidth, List<LineSegment> segments) {
        backingCanvas.drawLines(colour, strokeWidth, segments);
    }
    
    /**
     * Sets the backing canvas with the input bitmap and clears the drawing canvas
     * @param bitmap
     */
    public void setBitmap(byte[] bitmap) {
        backingCanvas.setBitmap(bitmap);
        drawingCanvas.clear();
    }
    
    /**
     * Clears the drawing canvas
     */
    public void clearDrawingCanvas() {
        drawingCanvas.clear();
    }
}
