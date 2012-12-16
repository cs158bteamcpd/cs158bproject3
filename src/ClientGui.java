import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Hashtable;

import javax.swing.*;

/** NOT YET IMPLEMENTED WITH OTHER CLASSES, JUST THE GUI
 * CS158B Project 3
 * 
 * Client side gui that will interact with the tcp server and network
 * elements
 * 
 * @author TeamCPD 
 *
 */
public class ClientGui extends JPanel implements ActionListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = 880349725426665681L;
	private Socket s;
	private ObjectOutputStream oos;
	private ObjectInputStream ois;
	
	protected JTextField textFieldHost;
	protected JTextField textFieldPort;
	protected JTextField textFieldOID;
	protected JTextField textFieldCommStr;
    protected JTextArea textArea;
    protected JTextArea textAreaAlarm;
    protected JComboBox comboBoxMethods;
    protected static JTextField textFieldSet;
    private SNMP snmp = null;
    
    private final static String newline = "\n";
	
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) 
	{
		//Schedule a job for the event dispatch thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
            	createAndShowGUI();
            }
        });
	}

	/**
	 * Create the application.
	 */
	public ClientGui() {
		super(new GridBagLayout());
		 
		//created new textfield
        textFieldHost = new JTextField(20);
        textFieldHost.setToolTipText("HostID goes here!");
		
        //created new textfield
        textFieldPort = new JTextField(20);
        textFieldPort.setToolTipText("Port goes here!");
		
		//created new textfield
        textFieldOID = new JTextField(20);
        textFieldOID.setToolTipText("OID goes here!");
        //textField.addActionListener(this); //attach listener
 
        //created new textfield
        textFieldCommStr = new JTextField(20);
        textFieldCommStr.setToolTipText("Community String goes here!");
        
        //create new button
        JButton b = new JButton("Submit");
        b.setToolTipText("Click to submit");
        b.addActionListener(this); //add action listener to this button
        
        //create button alarm
        JButton getAlarmButton = new JButton("Get Alarms");
        getAlarmButton.setToolTipText("Press to get RMON Alarms"); //To be honest this should be setting the trap and then the alarms come in...
        getAlarmButton.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent evt)
            {
				try {
					Socket s = new Socket(textFieldHost.getText(), Integer.parseInt(textFieldPort.getText()));// host, port
					/*if (s == null) 
					{
						//s = new Socket("localhost", 9999);// host, port
						s = new Socket(textFieldHost.getText(), Integer.parseInt(textFieldPort.getText()));// host, port

					}*/

					Hashtable<String, String> ht = new Hashtable<String, String>();

					ht.put(textFieldOID.getText(), textFieldOID.getText());

					snmp = new SNMP("1", textFieldCommStr.getText(), "1",
							"GET", ht);
					
					//setting the flag to true to get events
					snmp.setFlag();
					
					//create the OutputStream to write
					ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());
					//write the SNMP object to server
					oos.writeObject(snmp);

					// Now Wait for response
					ObjectInputStream ois = new ObjectInputStream(s.getInputStream());
					
					// get the RMONevent response
					ArrayList<RMONEvent> response = (ArrayList<RMONEvent>) ois.readObject();

					for (int i = 0; i < response.size(); i++)
					{
						//System.out.println(response.vBinding.get(OID));
						textAreaAlarm.append(response.get(i).toString() + newline);
						textAreaAlarm.setCaretPosition(textAreaAlarm.getDocument().getLength());
					}
			        

				} catch (UnknownHostException e) {
					// TODO Auto-generated catch block
					// System.out.println("Unknown Host");
					textAreaAlarm.append("Unknown Host" + newline);
					textAreaAlarm.setCaretPosition(textAreaAlarm.getDocument()
							.getLength());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					// System.out.println("Error retreving data");
					textAreaAlarm.append("Error retreving data" + newline);
					textAreaAlarm.setCaretPosition(textAreaAlarm.getDocument()
							.getLength());
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		});

        //create button Enable/Disable SNMP
        JButton snmpEnableDisableButton = new JButton("Enable/Disable SNMP Agent");
        snmpEnableDisableButton.setToolTipText("Press to enable/disable SNMP agent (Default: SNMP agent enabled)");        
        snmpEnableDisableButton.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent evt)
            {

				// System.out.println(response.vBinding.get(OID));
				// textAreaAlarm.append(response.get(i).toString() + newline);
        		textArea.append("SNMP ENABLED/DISABLED MESSAGE HERE" + newline);
				textArea.setCaretPosition(textArea.getDocument().getLength());

			}
		});
        
        //create new text area
        textArea = new JTextArea(5, 20);
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
 
        //create new text area for alarms
        textAreaAlarm = new JTextArea(5,20);
        textArea.setEditable(false);
        JScrollPane scrollPaneAlarm = new JScrollPane(textAreaAlarm);
        
        /*Tabbed Output Area*/
        JTabbedPane tabbedPane = new JTabbedPane();
         
        //JComponent panel1 = makeTextPanel("Output Console");
        tabbedPane.addTab("Output Console", null, scrollPane,
                "Output Console");
        tabbedPane.setMnemonicAt(0, KeyEvent.VK_1);
         
        //JComponent panel2 = makeTextPanel("Alarms");
        tabbedPane.addTab("Alarms", null, scrollPaneAlarm,
                "Output Alarm Console");
        tabbedPane.setMnemonicAt(1, KeyEvent.VK_2);
         
        //The following line enables to use scrolling tabs.
        tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        
        
        //create combobox, for get and set
        String[] methodStr = { "GET", "SET" };
        //Create the combo box, select the item at index 4.
        //Indices start at 0, so 4 specifies the pig.
        comboBoxMethods = new JComboBox(methodStr);
        comboBoxMethods.setToolTipText("Choose GET or SET method");
        comboBoxMethods.setSelectedIndex(0);
        //add listener to combobox
        comboBoxMethods.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e)
            {
                if ( ((String) comboBoxMethods.getSelectedItem()).equalsIgnoreCase("set") )
                {
                	createAndShowPopUp();
                }
            }
        });
        
        //Add Components to this panel.
        GridBagConstraints c = new GridBagConstraints();
        c.gridwidth = GridBagConstraints.REMAINDER;
 
        //Host
        add(new JLabel("Host ")); //add label for OID
        c.fill = GridBagConstraints.HORIZONTAL;
        add(textFieldHost, c); //Host textfield
        
        //Port
        add(new JLabel("Port ")); //add label for OID
        c.fill = GridBagConstraints.HORIZONTAL;
        add(textFieldPort, c); //Port textfield
        
        //OID
        add(new JLabel("OID ")); //add label for OID
        c.fill = GridBagConstraints.HORIZONTAL;
        add(textFieldOID, c); //OID textfield
        
        //Community String
        add(new JLabel("Community String ")); //add label for community string
        c.fill = GridBagConstraints.HORIZONTAL;
        add(textFieldCommStr, c); //Community String community string
        
        //GET and SET method
        add(new JLabel("GET or SET ")); //add label for methods
        c.fill = GridBagConstraints.HORIZONTAL;
        add(comboBoxMethods, c); //combo box selection
                
        //JPanel for buttons
        JPanel buttonPane = new JPanel();
        buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.LINE_AXIS));
        buttonPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        buttonPane.add(Box.createHorizontalGlue());
        buttonPane.add(b);
        buttonPane.add(Box.createRigidArea(new Dimension(10, 0)));
        buttonPane.add(getAlarmButton);
        buttonPane.add(Box.createRigidArea(new Dimension(10, 0)));
        buttonPane.add(snmpEnableDisableButton);
        //add the button panel
        add(buttonPane, c);
        
        //add buttons, submit, get alarm, snmp EnableDisable
        //add(b, c);
        //add(getAlarmButton, c);
        //add(snmpEnableDisableButton, c);
        
        
        //add Output scrollPane
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1.0;
        c.weighty = 1.0;
        //add(scrollPane, c);
        
        //add(new JLabel("Tabbed Pane"));
        //c.fill = GridBagConstraints.HORIZONTAL;
        //Add the tabbed pane to this panel.
        add(tabbedPane, c);
	}

	/**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event dispatch thread.
     */
    private static void createAndShowGUI() 
    {
        //Create and set up the window.
        JFrame frame = new JFrame("Client Gui");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        //Add contents to the window.
        frame.add(new ClientGui());
 
        //Display the window.
        //frame.pack(); // this packs all the components in the frame
        frame.setSize(600, 480);
        frame.setLocationRelativeTo(null); //this makes the window appear at the center
        frame.setVisible(true);
    }
    
    /**
     * Create the GUI for the Set method of the combo box
     */
    private static void createAndShowPopUp() 
    {
    	//Create and set up the window.
        final JFrame frame = new JFrame("SET Method");
        frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        
        
        //got my jpanel
    	JPanel methodGUI = new JPanel(new GridBagLayout());
    	
    	//created new textfield
        textFieldSet = new JTextField(20);
        textFieldSet.setToolTipText("Please enter in a value!");
    	
        //create new button
        JButton b = new JButton("Submit");
        b.setToolTipText("Click to submit");
        b.addActionListener(new ActionListener() {
 
            public void actionPerformed(ActionEvent e)
            {
                frame.dispose();
            }
        });      
        
        //Add Components to this panel.
        GridBagConstraints c = new GridBagConstraints();
        c.gridwidth = GridBagConstraints.REMAINDER;
 
        //SET
        methodGUI.add(new JLabel("SET value ")); //add label for OID
        c.fill = GridBagConstraints.HORIZONTAL;
        methodGUI.add(textFieldSet, c); //SET value
                
        c.fill = GridBagConstraints.HORIZONTAL;
        methodGUI.add(b, c); //SET value
        
        //Add contents to the window.
        frame.add(methodGUI);
 
        
        //Display the window.
        frame.pack(); // this packs all the components in the frame
        //frame.setSize(400, 400);
        frame.setLocationRelativeTo(null); //this makes the window appear at the center
        frame.setVisible(true);

    }

    /**
     * 
     * @param text
     * @return
     */
    public JComponent makeTextPanel(String text) {
        JPanel panel = new JPanel(false);
        JLabel filler = new JLabel(text);
        filler.setHorizontalAlignment(JLabel.CENTER);
        panel.setLayout(new GridLayout(1, 1));
        panel.add(filler);
        return panel;
    }
    
    
	@Override
	public void actionPerformed(ActionEvent evt) {
		String OID = textFieldOID.getText();
		String CommStr = textFieldCommStr.getText();
		String methodGetOrSet = (String) comboBoxMethods.getSelectedItem();
		
		try {
			
			Socket s = new Socket(textFieldHost.getText(), Integer.parseInt(textFieldPort.getText()));// host, port
			/*if (s == null)
			{
				//s = new Socket("localhost", 9999);// host, port
				s = new Socket(textFieldHost.getText(), Integer.parseInt(textFieldPort.getText()));// host, port
			}*/
			
			
			if(methodGetOrSet.equalsIgnoreCase("get"))
			{
				Hashtable<String,String> ht = new Hashtable<String,String>();
				
				ht.put(OID, OID);
				
				snmp = new SNMP("1",CommStr,"1","GET", ht);
			} 
			else 
			{
				
				Hashtable<String,String> ht = new Hashtable<String,String>();
				
				ht.put(OID, textFieldSet.getText());
				
				snmp = new SNMP("1",CommStr,"1","SET", ht);
				
			}
			
			
			/*if (oos == null) //if it's not open
			{
				// create the OutputStream to write
				oos = new ObjectOutputStream(s.getOutputStream());
			}*/
			ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());

			// write the SNMP object to server
			oos.writeObject(snmp);
			oos.flush();
			/*if (ois == null) //if it's not open
			{
				// Now Wait for response
				ois = new ObjectInputStream(s.getInputStream());
			}*/
			
			ObjectInputStream ois = new ObjectInputStream(s.getInputStream());

			
			// get the SNMP response
			SNMP response = (SNMP) ois.readObject();

			//System.out.println(response.vBinding.get(OID));
			textArea.append(response.vBinding.get(OID) + newline);
			
	        textArea.setCaretPosition(textArea.getDocument().getLength());

		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			//System.out.println("Unknown Host");
			textArea.append("Unknown Host" + newline);
	        textArea.setCaretPosition(textArea.getDocument().getLength());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//System.out.println("Error retreving data");
			textArea.append("Error retreving data" + newline);
	        textArea.setCaretPosition(textArea.getDocument().getLength());
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
		
		/*
		textArea.append("OID: " + OID + newline);
		textArea.append("Community String: " + CommStr + newline);
		textArea.append("Method: " + method + newline);

        textArea.setCaretPosition(textArea.getDocument().getLength());
		*/
		/*
		String text = textFieldOID.getText();
        textArea.append(text + newline);
        textFieldOID.selectAll();
 
        //Make sure the new text is visible, even if there
        //was a selection in the text area.
        textArea.setCaretPosition(textArea.getDocument().getLength());
		*/
	}

}
