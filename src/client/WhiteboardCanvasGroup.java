package client;

import shared.LineSegment;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class WhiteboardCanvasGroup extends JLayeredPane {
    private final Canvas backingCanvas;
    private final Canvas drawingCanvas;

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

    public void setColour(Color colour) {
        drawingCanvas.setColour(colour);
    }

    public void setErasing(boolean erasing) {
        drawingCanvas.isErasing(erasing);
    }

    public void setBrushStroke(int num) {
        drawingCanvas.setBrushStroke(num);
    }

    public void setDelegate(CanvasDelegate delegate) {
        drawingCanvas.setDelegate(delegate);
    }

    public void drawLines(Color colour, float strokeWidth, List<LineSegment> segments) {
        backingCanvas.drawLines(colour, strokeWidth, segments);
    }

    public void setBitmap(byte[] bitmap) {
        backingCanvas.setBitmap(bitmap);
        drawingCanvas.clear();
    }

    public void clearDrawingCanvas() {
        drawingCanvas.clear();
    }
}
