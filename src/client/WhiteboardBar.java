package client;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class WhiteboardBar extends JPanel 
{
	private static final long serialVersionUID = 1L;
	
	private final JTextField whiteboardName;
    private final JComboBox<String> whiteboardsList;
    
    private CanvasDelegate delegate = null;
    
    /**
     * WhiteboardBar constructor
     */
    public WhiteboardBar() 
    {
        whiteboardName = new JTextField(20);
        whiteboardName.setFont(new Font("SansSerif", Font.BOLD, 20));
        whiteboardName.setForeground(Color.GRAY);
        whiteboardName.setEnabled(false);
        
        whiteboardsList = new JComboBox<String>();
        whiteboardsList.setName("whiteboardsList");
        whiteboardsList.setToolTipText("Click to view and select a list of whiteboards hosted on the server");
        
        this.add(whiteboardName);
        this.add(whiteboardsList);
        
        whiteboardsList.addActionListener(new ActionListener()
        {	
        	@Override
			public void actionPerformed(ActionEvent e) 
        	{
				delegate.requestedWhiteboardChange(whiteboardsList.getSelectedItem().toString());			
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
    
    public void setWhiteboardsList(List<String> whiteboards)
    {
    	whiteboardsList.removeAll();
    	for (String whiteboard : whiteboards)
    	{
    		whiteboardsList.addItem(whiteboard);
    	}
    	this.repaint();
    }
}
