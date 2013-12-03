package client;

import java.awt.Color;
import java.awt.Font;

import javax.swing.JPanel;
import javax.swing.JTextField;

public class WhiteboardBar extends JPanel {
    public WhiteboardBar() {
        JTextField whiteboardName = new JTextField(20);
        whiteboardName.setFont(new Font("SansSerif", Font.BOLD, 20));
        whiteboardName.setForeground(Color.GRAY);
        whiteboardName.setText("whiteboard2345");
        
        this.add(whiteboardName);
        
    }
}
