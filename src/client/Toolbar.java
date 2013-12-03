package client;

import javax.swing.*;


public class Toolbar extends JPanel
{
    private final JButton paintButton;
    private final JButton eraseButton;
    
    
    public Toolbar() 
    {
    	GroupLayout layout = new GroupLayout(this.getTopLevelAncestor());
    	
        paintButton = new JButton("Paint");
        paintButton.setName("paintButton");
        
        eraseButton = new JButton("Erase");
        eraseButton.setName("eraseButton");
        
        layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
        	.addGroup(layout.createSequentialGroup()
        		.addComponent(paintButton)
        		.addComponent(eraseButton)));
        
        layout.setVerticalGroup(layout.createSequentialGroup()
        	.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
        		.addComponent(paintButton)
        		.addComponent(eraseButton)));
        
    }
    
    
    
    
}
