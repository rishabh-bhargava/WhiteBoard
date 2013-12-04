package client;

import java.awt.Color;
import java.awt.Font;

import javax.swing.JPanel;
import javax.swing.JTextField;

public class WhiteboardBar extends JPanel {
    private final JTextField whiteboardName;
    public WhiteboardBar() {
        whiteboardName = new JTextField(20);
        whiteboardName.setFont(new Font("SansSerif", Font.BOLD, 20));
        whiteboardName.setForeground(Color.GRAY);
        whiteboardName.setEnabled(false);
        
        this.add(whiteboardName);
        
    }

    public void setName(String name) {
        whiteboardName.setText(name);
    }
}
