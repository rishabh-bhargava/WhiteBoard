package client;

import java.awt.BorderLayout;
import java.awt.Container;

import javax.swing.GroupLayout;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import canvas.Canvas;


public class ClientGUI extends JFrame {
    
    
    
    public ClientGUI() {
        super("Whiteboard");
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        Container cp = this.getContentPane();
        BorderLayout layout = new BorderLayout();
        this.setSize(350, 450);
        cp.setLayout(layout);
//        layout.setAutoCreateGaps(true);
//        layout.setAutoCreateContainerGaps(true);
        
        Canvas canvas = new Canvas(800, 600);
        this.add(canvas, BorderLayout.CENTER);
        this.pack();

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

