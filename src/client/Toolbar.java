package client;

import java.awt.FlowLayout;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JPanel;


public class Toolbar extends JPanel
{
    private final JButton paintButton;
    private final JButton eraseButton;
    
    
    public Toolbar() 
    {
    	GroupLayout layout = new GroupLayout(this);
    	
        paintButton = new JButton("Paint");
        paintButton.setName("paintButton");
        
        eraseButton = new JButton("Erase");
        eraseButton.setName("eraseButton");
        
        this.setLayout(layout);
        
        layout.setHorizontalGroup(layout.createSequentialGroup()
        	.addComponent(paintButton)
        	.addComponent(eraseButton));
        
        layout.setVerticalGroup(layout.createSequentialGroup()
        	.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
        		.addComponent(paintButton)
        		.addComponent(eraseButton)));
        
    }
    
    
    
    
}
