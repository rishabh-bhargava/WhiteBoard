package client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class ClientGUI extends JFrame 
{   

    private Canvas canvas;
    private Toolbar toolbar;
    
    public ClientGUI() {
        super("Whiteboard");
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        Container cp = this.getContentPane();
        BorderLayout layout = new BorderLayout();
        cp.setLayout(layout);
        
        canvas = new Canvas(800, 600);
        this.add(canvas, BorderLayout.CENTER);

        toolbar = new Toolbar(this);
        JPanel leftAlignedToolbar = new JPanel(new BorderLayout());
        leftAlignedToolbar.add(toolbar, BorderLayout.WEST);
        
        this.add(leftAlignedToolbar, BorderLayout.EAST);
        
        this.pack();        

    }
    
    public void setColor(Color c) {
        canvas.setColour(c);
    }
    public void isErasing(boolean b) {
        canvas.isErasing(b);
    }
    
    public void setBrushStroke(int num) {
        canvas.setBrushStroke(num);
    }
    
    /*
     * Instantiate GUI JFrame in a separate thread from main.
     */
    public static void main(final String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                ClientGUI main = new ClientGUI();
                main.setVisible(true);
            }
        });
    }
    
    
}

