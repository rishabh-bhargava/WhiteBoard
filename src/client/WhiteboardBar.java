package client;

import java.awt.Color;
import java.awt.Font;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class WhiteboardBar extends JPanel 
{
    private final JTextField whiteboardName;
    private final JComboBox<String> userList;
    
    public WhiteboardBar() 
    {
        whiteboardName = new JTextField(20);
        whiteboardName.setFont(new Font("SansSerif", Font.BOLD, 20));
        whiteboardName.setForeground(Color.GRAY);
        whiteboardName.setEnabled(false);
        
        userList = new JComboBox<String>();
        userList.setName("userList");
        userList.setToolTipText("Click to see a list of users connected to this server");
        
        this.add(whiteboardName);
        this.add(userList);
        
    }

    public void setWhiteboardName(String name) 
    {
        whiteboardName.setText(name);
    }
    
    public void setUserList(List<String> users)
    {
    	for(String user : users)
    	{
    		userList.addItem(user);
    	}
    }
}
