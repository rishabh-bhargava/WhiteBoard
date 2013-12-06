package client;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.ParallelGroup;
import javax.swing.GroupLayout.SequentialGroup;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JLabel;
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
    
    private final JLabel brushThicknessLabel;
    private final JLabel pickColourLabel;
    
    private final JButton colourButton;
    
    private final ClientGUI client;    
    
    public Toolbar(ClientGUI clientGUI) 
    {
    	this.client = clientGUI;
    	this.setPreferredSize(new Dimension(220,500));
    	
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
        brushThickness.setName("brushThickness");
        
        brushThicknessLabel = new JLabel();
        brushThicknessLabel.setText("Set Brush Thickness:");
        brushThicknessLabel.setName("brushThicknessLabel");
        
        pickColourLabel = new JLabel("Pick Colour!");
        pickColourLabel.setName("pickColourLabel");
        
        colourButton = new JButton("Choose funky colours!");
        colourButton.setName("clourButton");
        
        this.setLayout(layout);
        
        SequentialGroup coloursTopS = layout.createSequentialGroup();
        ParallelGroup coloursTopP = layout.createParallelGroup(GroupLayout.Alignment.LEADING);
        SequentialGroup coloursBottomS = layout.createSequentialGroup();
        ParallelGroup coloursBottomP = layout.createParallelGroup(GroupLayout.Alignment.LEADING);
        Color[] mainColours = {Color.black, Color.blue, Color.green, Color.magenta, Color.red, Color.yellow, Color.orange, Color.gray};
        JButton[] colourButtons = new JButton[8];
        
        ActionListener colourListeners = new ActionListener()
        {
     	   @Override
     	   public void actionPerformed(ActionEvent e) 
     	   {
     		   String action = e.getActionCommand();
     		   System.out.println(action);
     	       client.setColor(new Color(new Integer(action)));
     	   }
        };
        
        for(int i = 0; i<8; i++)
        {
        	colourButtons[i] = new JButton();
        	colourButtons[i].setActionCommand(Integer.toString(mainColours[i].getRGB()));
        	colourButtons[i].setBackground(mainColours[i]);
        	colourButtons[i].setOpaque(true);
        	colourButtons[i].addActionListener(colourListeners);

        	if(i<4)
        	{
        		coloursTopS.addComponent(colourButtons[i], 30, 30, 30).addGap(2);
        		coloursTopP.addComponent(colourButtons[i], 30, 30, 30).addGap(2);
        	}
        	else
        	{
        		coloursBottomS.addComponent(colourButtons[i], 30, 30, 30).addGap(2);
        		coloursBottomP.addComponent(colourButtons[i], 30, 30, 30).addGap(2);
        	}
        }
        
        layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
        	//TODO: Add a gap properly here
        	.addGroup(layout.createSequentialGroup()
        		.addComponent(paintButton).addGap(10)
        		.addComponent(eraseButton))
        	.addComponent(brushThicknessLabel)
        	.addComponent(brushThickness)
        	.addComponent(pickColourLabel)
        	.addGroup(coloursTopS)
        	.addGroup(coloursBottomS).addGap(2)
        	.addComponent(colourButton)
        	);
        
        layout.setVerticalGroup(layout.createSequentialGroup().addGap(10)
        	.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
        		.addComponent(paintButton)
        		.addComponent(eraseButton)).addGap(20)
        	.addComponent(brushThicknessLabel).addGap(5)
        	.addComponent(brushThickness).addGap(20)
        	.addComponent(pickColourLabel).addGap(5)
        	.addGroup(coloursTopP).addGap(2)
        	.addGroup(coloursBottomP).addGap(2)
        	.addComponent(colourButton)
        	);
        
        paintButton.addActionListener(new ActionListener()
        {
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				client.setErasing(false);
			}
        	
        });
        
        eraseButton.addActionListener(new ActionListener()
        {
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				client.setErasing(true);
				
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
       
       colourButton.addActionListener(new ActionListener()
       {

    	   @Override
    	   public void actionPerformed(ActionEvent e) 
    	   {
    		   Color newColour = JColorChooser.showDialog(null, "Choose a colour!", Color.black);
    		   if(newColour == null)
    			   return;
    		   else
    			   client.setColor(newColour);
    	   }
		});
       
    }
    
    
    
    
}
