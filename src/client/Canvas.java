package client;

import shared.LineSegment;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.*;
import java.util.List;

import javax.swing.JPanel;

/**
 * Canvas represents a drawing surface that allows the user to draw
 * on it freehand, with the mouse.
 */
public class Canvas extends JPanel {
    // image where the user's drawing is stored
    private BufferedImage drawingBuffer;
    private Graphics2D drawingGraphics;
    private Color colour = Color.red;
    private boolean isErasing = false;
    private boolean opaque = false;
    private CanvasDelegate delegate = null;
    BasicStroke brushStroke = new BasicStroke(1);

    /**
     * Make a canvas.
     * @param width width in pixels
     * @param height height in pixels
     * @param opaque opaque or not
     */
    public Canvas(int width, int height, boolean opaque) {
        this.setPreferredSize(new Dimension(width, height));
        this.setMinimumSize(new Dimension(width, height));
        this.setMaximumSize(new Dimension(width, height));
        this.setSize(width, height);
        this.opaque = opaque;
        setOpaque(opaque);
        addDrawingController();
        setBackground(new Color(255, 0, 0, 0));
        
        //This changes the cursor to a crosshair cursor--do we want to?
        //this.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));

        // note: we can't call makeDrawingBuffer here, because it only
        // works *after* this canvas has been added to a window.  Have to
        // wait until paintComponent() is first called.
    }

    public void setDelegate(CanvasDelegate delegate) {
        this.delegate = delegate;
    }
    
    /**
     * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
     */
    @Override
    public void paintComponent(Graphics g) {
        // If this is the first time paintComponent() is being called,
        // make our drawing buffer.
        if (drawingBuffer == null) {
            makeDrawingBuffer();
        }
        Graphics2D graphics = (Graphics2D)g.create();
        graphics.setComposite(AlphaComposite.Src);
        graphics.setColor(new Color(255, 0, 255, 0));
        graphics.setBackground(new Color(0, 255, 0, 0));
        super.paintComponent(g);
        if(opaque) { // get rid of this if to enable local echo. Flickers.
//            graphics.fillRect(0, 0, getWidth(), getHeight());
            graphics.drawImage(drawingBuffer, 0, 0, new Color(0, 255, 0, 0), null);
        // Copy the drawing buffer to the screen.
        }

    }
    
    /*
     * Make the drawing buffer and draw some starting content for it.
     */
    private void makeDrawingBuffer() {
        drawingBuffer = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
        drawingGraphics = drawingBuffer.createGraphics();
        if(opaque)
            fillWithWhite();
    }
    
    /*
     * Make the drawing buffer entirely white.
     */
    private void fillWithWhite() {
        drawingGraphics.setColor(Color.WHITE);
        drawingGraphics.fillRect(0,  0,  getWidth(), getHeight());
        
        // IMPORTANT!  every time we draw on the internal drawing buffer, we
        // have to notify Swing to repaint this component on the screen.
        this.repaint();
    }
    
    /*
     * Draw a line between two points (x1, y1) and (x2, y2), specified in
     * pixels relative to the upper-left corner of the drawing buffer.
     */
    private synchronized void drawLineSegment(int x1, int y1, int x2, int y2) {
        drawingGraphics.setBackground(new Color(0, 255, 0, 0));
        drawingGraphics.setStroke(brushStroke);
        drawingGraphics.setComposite(AlphaComposite.SrcOver);
        Color ourColour = new Color(colour.getRed(), colour.getGreen(), colour.getBlue(), 255);
        
        if (!isErasing) {
            drawingGraphics.setColor(ourColour);
        } else {
            drawingGraphics.setColor(Color.WHITE);
//            g.setStroke(brushStroke.get);
        }
        drawingGraphics.drawLine(x1, y1, x2, y2);
        
        // IMPORTANT!  every time we draw on the internal drawing buffer, we
        // have to notify Swing to repaint this component on the screen.
        this.repaint();
    }

    public synchronized void drawLines(Color colour, float strokeWidth, List<LineSegment> segments) {
        drawingGraphics.setStroke(new BasicStroke(strokeWidth));
        drawingGraphics.setColor(colour);
        for(LineSegment segment : segments) {
            drawingGraphics.drawLine(segment.x1, segment.y1, segment.x2, segment.y2);
        }
        this.repaint();
    }
    
    /*
     * Add the mouse listener that supports the user's freehand drawing.
     */
    private void addDrawingController() {
        DrawingController controller = new DrawingController();
        addMouseListener(controller);
        addMouseMotionListener(controller);
    }
    
    public void setColour(Color c)
    {
    	this.colour = c;
    }
    
    public void isErasing(boolean b) {
        this.isErasing = b;
    }
    
    public void setBrushStroke(int num) {
        this.brushStroke = new BasicStroke(num);
    }

    public void setBitmap(byte[] bitmap) {
        // Because this can get called very early.
        if (drawingBuffer == null) {
            makeDrawingBuffer();
        }
        DataBufferByte buffer = new DataBufferByte(bitmap, bitmap.length);
        SampleModel model = new PixelInterleavedSampleModel(buffer.getDataType(), getWidth(), getHeight(), 4, 4 * getWidth(), new int[]{3,2,1,0});
        Raster raster = Raster.createRaster(model, buffer, new Point(0, 0));
        drawingBuffer.setData(raster);
        repaint();
    }

    public void clear() {
        if(drawingBuffer == null) return;
        drawingGraphics.setComposite(AlphaComposite.Src);
        drawingGraphics.setColor(new Color(0,0,0,0));
        drawingGraphics.fillRect(0, 0, getWidth(), getHeight());
        repaint();
    }
    
    /*
     * DrawingController handles the user's freehand drawing.
     */
    private class DrawingController implements MouseListener, MouseMotionListener {
        // store the coordinates of the last mouse event, so we can
        // draw a line segment from that last point to the point of the next mouse event.
        private int lastX, lastY; 

        /*
         * When mouse button is pressed down, start drawing.
         */
        public void mousePressed(MouseEvent e) {
            lastX = e.getX();
            lastY = e.getY();
        }

        /*
         * When mouse moves while a button is pressed down,
         * draw a line segment.
         */
        public void mouseDragged(MouseEvent e) {
            int x = e.getX();
            int y = e.getY();
            drawLineSegment(lastX, lastY, x, y);
            if(delegate != null) {
                delegate.drewLine(isErasing ? Color.WHITE : colour, brushStroke.getLineWidth(), lastX, lastY, x, y);
            }
            lastX = x;
            lastY = y;
        }

        // Ignore all these other mouse events.
        public void mouseMoved(MouseEvent e) {}
        public void mouseClicked(MouseEvent e) {
            int x = e.getX();
            int y = e.getY();
            drawLineSegment(lastX, lastY, x, y);
            if(delegate != null) {
                delegate.drewLine(isErasing ? Color.WHITE : colour, brushStroke.getLineWidth(), lastX, lastY, x, y);
            }
            lastX = x;
            lastY = y;
        }
        public void mouseReleased(MouseEvent e) { }
        public void mouseEntered(MouseEvent e) { }
        public void mouseExited(MouseEvent e) { }
    }
}
