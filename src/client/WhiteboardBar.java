package client;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.*;

public class WhiteboardBar extends JPanel 
{
	private static final long serialVersionUID = 1L;
	
	private final JLabel nameLabel;
	private final JTextField whiteboardName;
	private final JLabel listLabel;
    private final JComboBox<String> whiteboardsList;
    private final JLabel newLabel;
    private final JButton newBoardButton;
    
    private CanvasDelegate delegate = null;
    
    /**
     * WhiteboardBar constructor
     */
    public WhiteboardBar() 
    {
    	nameLabel = new JLabel("Name of Whiteboard:");
    	
        whiteboardName = new JTextField(20);
        whiteboardName.setFont(new Font("SansSerif", Font.BOLD, 20));
        whiteboardName.setForeground(Color.GRAY);
        whiteboardName.setEnabled(false);
        
        listLabel = new JLabel("Available whiteboards");
        
        whiteboardsList = new JComboBox<String>();
        whiteboardsList.setName("whiteboardsList");
        whiteboardsList.setToolTipText("Click to view and select a list of whiteboards hosted on the server");
        //whiteboardsList.addItem("           ");
        
        newLabel = new JLabel("Create white board:");
        newBoardButton = new JButton("NEW");;
        
        this.add(nameLabel);
        this.add(whiteboardName);
        this.add(listLabel);
        this.add(whiteboardsList);
        this.add(newLabel);
        this.add(newBoardButton);
        
        whiteboardsList.addActionListener(new ActionListener()
        {	
        	@Override
			public void actionPerformed(ActionEvent e) 
        	{
                if(delegate != null && whiteboardsList.getSelectedItem() != null
                        && !whiteboardsList.getSelectedItem().toString().equals(whiteboardName.getText()))
				    delegate.requestedWhiteboardChange(whiteboardsList.getSelectedItem().toString());
			}
        	
        });
        
        newBoardButton.addActionListener(new ActionListener()
        {	
        	@Override
			public void actionPerformed(ActionEvent e) 
        	{
        		
                
			}
        	
        });
        
    }
    
    public void setDelegate(CanvasDelegate del)
    {
    	this.delegate = del;
    }
    
    /**
     * Set the name of the whiteboard
     * @param name : String name of whiteboard
     */
    public void setWhiteboardName(String name) 
    {
        whiteboardName.setText(name);
    }
    
    public void setWhiteboardsList(final String[] whiteboards)
    {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                whiteboardsList.removeAllItems();
                for (String whiteboard : whiteboards) {
                    System.out.println(whiteboard);
                    whiteboardsList.addItem(whiteboard);
                    if(whiteboard.equals(whiteboardName.getText())) {
                        whiteboardsList.setSelectedItem(whiteboard);
                    }
                }
            }
        });
    }
}
