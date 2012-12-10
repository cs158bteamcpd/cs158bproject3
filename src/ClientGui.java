import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
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

	//protected JTextField textField;
	protected JTextField textFieldOID;
	protected JTextField textFieldCommStr;
    protected JTextArea textArea;
    protected JComboBox comboBoxMethods;
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
        
        //create new text area
        textArea = new JTextArea(5, 20);
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
 
        //create combobox, for get and set
        String[] methodStr = { "GET", "SET" };
        //Create the combo box, select the item at index 4.
        //Indices start at 0, so 4 specifies the pig.
        comboBoxMethods = new JComboBox(methodStr);
        comboBoxMethods.setToolTipText("Choose GET or SET method");
        comboBoxMethods.setSelectedIndex(0);
        
        
        //Add Components to this panel.
        GridBagConstraints c = new GridBagConstraints();
        c.gridwidth = GridBagConstraints.REMAINDER;
 
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
        
        
        //add button, submit
        add(b, c);
 
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1.0;
        c.weighty = 1.0;
        add(scrollPane, c);
	}

	/**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event dispatch thread.
     */
    private static void createAndShowGUI() {
        //Create and set up the window.
        JFrame frame = new JFrame("Client Gui");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        //Add contents to the window.
        frame.add(new ClientGui());
 
        //Display the window.
        //frame.pack(); // this packs all the components in the frame
        frame.setSize(400, 400);
        frame.setLocationRelativeTo(null); //this makes the window appear at the center
        frame.setVisible(true);
    }

	@Override
	public void actionPerformed(ActionEvent evt) {
		String OID = textFieldOID.getText();
		String CommStr = textFieldCommStr.getText();
		String methodGetOrSet = (String) comboBoxMethods.getSelectedItem();
		
		try {
			Socket s = new Socket("localhost", 9999);// host, port

			// create new hashtable
			Hashtable<String, String> ht = new Hashtable<String, String>();
			// put OID in hashtable
			ht.put(OID, OID);
			// set new SNMP object
			snmp = new SNMP("1", CommStr, "1", methodGetOrSet, ht);

			//create the OutputStream to write
			ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());
			//write the SNMP object to server
			oos.writeObject(snmp);

			// Now Wait for response
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
