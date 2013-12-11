package client;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

/**
 * Provides the bar across the top of the whiteboard.
 * Thread safety: manipulation always happens on the Swing thread, and is thus necessarily consistent.
 */
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

    private boolean ignoreWhateverSillyActionThingsHappen = false; // This flag is true while generating spurious events.
    
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
                if(ignoreWhateverSillyActionThingsHappen) return;
                if(delegate != null && whiteboardsList.getSelectedItem() != null)
				    delegate.requestedWhiteboardChange(whiteboardsList.getSelectedItem().toString());
			}
        	
        });
        
        newBoardButton.addActionListener(new ActionListener()
        {	
        	@Override
			public void actionPerformed(ActionEvent e) 
        	{
        		String name = JOptionPane.showInputDialog((Component)e.getSource(), "Enter a name for the new whiteboard", "New whiteboard", JOptionPane.QUESTION_MESSAGE);
                if(name != null) {
                    delegate.requestedWhiteboardCreation(name);
                }
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
    public void setWhiteboardName(final String name)
    {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                whiteboardName.setText(name);
                ignoreWhateverSillyActionThingsHappen = true;
                whiteboardsList.setSelectedItem(name);
                ignoreWhateverSillyActionThingsHappen = false;
            }
        });
    }
    
    public void setWhiteboardsList(final String[] whiteboards)
    {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                ignoreWhateverSillyActionThingsHappen = true;
                whiteboardsList.removeAllItems();
                for (String whiteboard : whiteboards) {
                    System.out.println(whiteboard);
                    whiteboardsList.addItem(whiteboard);
                    if(whiteboard.equals(whiteboardName.getText())) {
                        whiteboardsList.setSelectedItem(whiteboard);
                    }
                }
                ignoreWhateverSillyActionThingsHappen = false;
            }
        });
    }
}
