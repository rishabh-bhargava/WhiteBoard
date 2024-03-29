package client;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.ParallelGroup;
import javax.swing.GroupLayout.SequentialGroup;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTable;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableModel;


/**
 * Handles the tools down the side of the user interface.
 */
public class Toolbar extends JPanel
{
	private static final long serialVersionUID = 1L;
	
	private final JButton paintButton;
    private final JButton eraseButton;
    private final JSlider brushThickness;
    private static final int MIN_THICKNESS = 0;
    private static final int MAX_THICKNESS = 20;
    
    private final JLabel brushThicknessLabel;
    private final JLabel pickColourLabel;
    
    private final JButton colourButton;
    
    private final JTable userTable;
    
    private final ClientGUI client;    
    
    /**
     * The Toolbar Constructor
     */
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
        
        userTable = new JTable(new UserTableModel());
        userTable.setRowSelectionAllowed(false);
        userTable.setFocusable(false);
        JScrollPane userTableScrollpane = new JScrollPane(userTable);
        
        this.setLayout(layout);
        
        //Adding the colour buttons to the layout
        SequentialGroup coloursTopS = layout.createSequentialGroup();
        ParallelGroup coloursTopP = layout.createParallelGroup(GroupLayout.Alignment.LEADING);
        SequentialGroup coloursBottomS = layout.createSequentialGroup();
        ParallelGroup coloursBottomP = layout.createParallelGroup(GroupLayout.Alignment.LEADING);
        //The primary colours added as buttons
        Color[] mainColours = {Color.black, Color.blue, Color.green, Color.magenta, Color.red, Color.yellow, Color.orange, Color.gray};
        JButton[] colourButtons = new JButton[8];
        
        /*
         * Listeners on the colour buttons
         */
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
        
        //Initialising the colour buttons
        for(int i = 0; i<8; i++)
        {
        	colourButtons[i] = new JButton();
        	colourButtons[i].setActionCommand(Integer.toString(mainColours[i].getRGB()));
        	colourButtons[i].setBackground(mainColours[i]);
        	colourButtons[i].setOpaque(true);
        	colourButtons[i].setBorderPainted(false);
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
        	.addComponent(userTableScrollpane)
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
        	.addComponent(colourButton).addGap(20)
        	.addComponent(userTableScrollpane)
        	);
        
        /*
         * Listener on the paint button
         */
        paintButton.addActionListener(new ActionListener()
        {
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				client.setErasing(false);
			}
        	
        });
        
        /*
         * Listener on the erase button
         */
        eraseButton.addActionListener(new ActionListener()
        {
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				client.setErasing(true);
				
			}
        	
        });
        
       /*
        * Listener on the brush thickness slider 
        */
       brushThickness.addChangeListener(new ChangeListener()
       {
    	   @Override
    	   public void stateChanged(ChangeEvent arg0) 
    	   {
    		   client.setBrushStroke(brushThickness.getValue() + 1);
    	   }
       });
       
       /*
        * Listener on the colour picker button
        */
       colourButton.addActionListener(new ActionListener()
       {
    	   @Override
    	   public void actionPerformed(ActionEvent e) 
    	   {
    		   //Colour chooser dialog box opens up
    		   Color newColour = JColorChooser.showDialog(null, "Choose a colour!", Color.black);
    		   if(newColour == null)
    			   return;
    		   else
    			   client.setColor(newColour);
    	   }
		});       
    }
    
    /*
     * Used by ClientGUI to update list of currently connected users
     */
    public void setUserList(List<String> users) {
        Object[][] data = new Object[users.size()][1];
        for (int i = 0; i < users.size(); i++) {
            data[i][0] = users.get(i);
        }
        userTable.setModel(new UserTableModel(data));
    }
    
    /**
     * Custom table model for JTable to display list of connected users for
     * current whiteboard
     */
    private class UserTableModel extends DefaultTableModel {
        private static final long serialVersionUID = 1L;
        public UserTableModel() {
            super(new Object[][] {}, new Object[] {"Current users"});
        }
        public UserTableModel(Object[][] data) {
            super(data, new Object[] {"Current users"});
        }
        @Override
        public boolean isCellEditable(int row, int col) {
            return false;
        }
    }
    
}
