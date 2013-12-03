package client;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JPanel;


public class Toolbar extends JPanel
{
    private final JButton paintButton;
    private final JButton eraseButton;
    
    private final ClientGUI client;
    
    
    public Toolbar(ClientGUI clientGUI) 
    {
    	client = clientGUI;
    	
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
        
        paintButton.addActionListener(new ActionListener()
        {
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				client.isErasing(false);
			}
        	
        });
        
        eraseButton.addActionListener(new ActionListener()
        {

			@Override
			public void actionPerformed(ActionEvent e) 
			{
				client.isErasing(true);
				
			}
        	
        });
        
    }
    
    
    
    
}
