package client;

import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class WhiteboardPicker extends JFrame implements ActionListener {
    private final WhiteboardPickerDelegate delegate;

    private JList<String> list;
    private JScrollPane listScroller;
    private GroupLayout layout;
    private JButton joinButton;
    private JButton createButton;

    public WhiteboardPicker(WhiteboardPickerDelegate delegate, String[] whiteboards) {
        this.delegate = delegate;

        setTitle("Choose a whiteboard");

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        list = new JList<>(whiteboards);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setLayoutOrientation(JList.VERTICAL);
        if(whiteboards.length > 0) {
            list.setSelectedIndex(0);
        }

        listScroller = new JScrollPane(list);
        listScroller.setPreferredSize(new Dimension(200, 400));
        listScroller.setMinimumSize(new Dimension(50, 50));

        joinButton = new JButton("Join");
        createButton = new JButton("Create new board");

        layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);
        layout.setHorizontalGroup(
                layout.createParallelGroup()
                        .addComponent(listScroller)
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(createButton)
                                .addComponent(joinButton)
                        )
        );
        layout.setVerticalGroup(
                layout.createSequentialGroup()
                        .addComponent(listScroller)
                        .addGroup(layout.createParallelGroup()
                                .addComponent(createButton)
                                .addComponent(joinButton)
                        )
        );

        joinButton.addActionListener(this);
        createButton.addActionListener(this);

        setMinimumSize(new Dimension(300, 300));
        setVisible(true);

        if(whiteboards.length > 0) {
            joinButton.requestFocus();
        } else {
            createButton.requestFocus();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == joinButton) {
            String value = list.getSelectedValue();
            if(value != null) {
                delegate.whiteboardPicked(value);
                close();
            }
        } else if(e.getSource() == createButton) {
            // Prompt for a name
            String name = JOptionPane.showInputDialog(this, "Create a new whiteboard:");
            if(name.contains(" ")) {
                JOptionPane.showMessageDialog(this, "Error", "Whiteboard names cannot contain spaces", JOptionPane.ERROR_MESSAGE);
            } else {
                delegate.whiteboardCreated(name);
                close();
            }
        }
    }

    private void close() {
        setVisible(false);
        dispose();
    }
}
