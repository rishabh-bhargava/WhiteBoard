package client;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


public class Toolbar extends JPanel
{
    private final JButton paintButton;
    private final JButton eraseButton;
    private final JSlider brushThickness;
    private static final int MIN_THICKNESS = 0;
    private static final int MAX_THICKNESS = 20;
    
    private final ClientGUI client;
    
    
    public Toolbar(ClientGUI clientGUI) 
    {
    	client = clientGUI;
    	
    	GroupLayout layout = new GroupLayout(this);
    	
        paintButton = new JButton("Paint");
        paintButton.setName("paintButton");
        
        eraseButton = new JButton("Erase");
        eraseButton.setName("eraseButton");
        
        brushThickness = new JSlider(JSlider.HORIZONTAL, MIN_THICKNESS, MAX_THICKNESS, 0);
        brushThickness.setMajorTickSpacing(5);
        brushThickness.setMinorTickSpacing(1);
        brushThickness.setPaintLabels(true);
        brushThickness.setPaintLabels(true);
        
        this.setLayout(layout);
        
        layout.setHorizontalGroup(layout.createSequentialGroup()
        	.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
        		.addComponent(paintButton)
        		.addComponent(brushThickness))        	
        	.addComponent(eraseButton));
        
        layout.setVerticalGroup(layout.createSequentialGroup()
        	.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
        		.addComponent(paintButton)
        		.addComponent(eraseButton))
        	.addComponent(brushThickness));
        
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
        
       brushThickness.addChangeListener(new ChangeListener()
       {

		@Override
		public void stateChanged(ChangeEvent arg0) 
		{
			client.setBrushStroke(brushThickness.getValue() + 1);
		}
    	   
       });        
    }
    
    
    
    
}
