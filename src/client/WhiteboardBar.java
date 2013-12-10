package client;

import java.awt.Color;
import java.awt.Font;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class WhiteboardBar extends JPanel 
{
	private static final long serialVersionUID = 1L;
	
	private final JTextField whiteboardName;
    private final JComboBox<String> userList;
    private final JComboBox<String> whiteboardsList;
    
    /**
     * WhiteboardBar constructor
     */
    public WhiteboardBar() 
    {
        whiteboardName = new JTextField(20);
        whiteboardName.setFont(new Font("SansSerif", Font.BOLD, 20));
        whiteboardName.setForeground(Color.GRAY);
        whiteboardName.setEnabled(false);
        
        userList = new JComboBox<String>();
        userList.setName("userList");
        userList.setToolTipText("Click to see a list of users connected to this server");
        
        whiteboardsList = new JComboBox<String>();
        whiteboardsList.setName("whiteboardsList");
        whiteboardsList.setToolTipText("Click to view and select a list of whiteboards hosted on the server");
        
        this.add(whiteboardName);
        this.add(userList);
        this.add(whiteboardsList);
        
    }
    
    /**
     * Set the name of the whiteboard
     * @param name : String name of whiteboard
     */
    public void setWhiteboardName(String name) 
    {
        whiteboardName.setText(name);
    }
    
    /**
     * Sets the userList by adding the items to the combo box
     * @param users
     */
    public void setUserList(List<String> users)
    {
    	userList.removeAll();
    	for(String user : users)
    	{
    		userList.addItem(user);
    	}
    	this.repaint();
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
