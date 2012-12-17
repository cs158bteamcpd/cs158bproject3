import java.awt.*;
import java.util.ArrayList;
import java.util.Hashtable;

import javax.swing.*;
import javax.swing.tree.*;
import javax.swing.event.*;

public class MibBrowser extends JPanel {//implements TreeSelectionListener {
	
	private JTree tree;
	private JTextField currentSelectionField;
	private Hashtable<String, String> snmpViewRecord = new Hashtable<String,String>(); //isoAll, view all MIB
								   //isoNone, view non of the MIB
								   //include, include this OID in view
								   //exclude, exclude this OID in view
	private ArrayList<String> tcpMibArrayList;
	
	
	public MibBrowser() 
	{		
		super(new GridBagLayout());
		
		DefaultMutableTreeNode root = new DefaultMutableTreeNode("mib2 - 1.3.6.1.2.1");
		DefaultMutableTreeNode child = null;
		DefaultMutableTreeNode grandChild;
		
		//system 
		/*child = new DefaultMutableTreeNode("system - 1.3.6.1.2.1.1");
		root.add(child);
		grandChild = new DefaultMutableTreeNode("sysDescr - 1.3.6.1.2.1.1.1");
		child.add(grandChild);
		grandChild = new DefaultMutableTreeNode("sysObjectID - 1.3.6.1.2.1.1.2");
		child.add(grandChild);
		grandChild = new DefaultMutableTreeNode("sysUpTime - 1.3.6.1.2.1.1.3");
		child.add(grandChild);
		grandChild = new DefaultMutableTreeNode("sysContact - 1.3.6.1.2.1.1.4");
		child.add(grandChild);
		grandChild = new DefaultMutableTreeNode("sysName - 1.3.6.1.2.1.1.5");
		child.add(grandChild);
		grandChild = new DefaultMutableTreeNode("sysLocation - 1.3.6.1.2.1.1.6");
		child.add(grandChild);
		grandChild = new DefaultMutableTreeNode("sysServices - 1.3.6.1.2.1.1.7");
		child.add(grandChild);*/
		
		tcpMibArrayList = new ArrayList<String>();
		tcpMibArrayList.add("tcp - 1.3.6.1.2.1.6");
		tcpMibArrayList.add("tcpRtoAlgorithm - 1.3.6.1.2.1.6.1");
		tcpMibArrayList.add("tcpRtoMin - 1.3.6.1.2.1.6.2");
		tcpMibArrayList.add("tcpRtoMax- 1.3.6.1.2.1.6.3");
		tcpMibArrayList.add("tcpMaxConn - 1.3.6.1.2.1.6.4");
		tcpMibArrayList.add("tcpActiveOpens - 1.3.6.1.2.1.6.5");
		tcpMibArrayList.add("tcpPassiveOpens - 1.3.6.1.2.1.6.6");
		tcpMibArrayList.add("tcpAttemptFails - 1.3.6.1.2.1.6.7");
		tcpMibArrayList.add("tcpEstabResets - 1.3.6.1.2.1.6.8");
		tcpMibArrayList.add("tcpCurrEstab - 1.3.6.1.2.1.6.9");
		tcpMibArrayList.add("tcpInSegs - 1.3.6.1.2.1.6.10");
		tcpMibArrayList.add("tcpOutSegs - 1.3.6.1.2.1.6.11");
		tcpMibArrayList.add("tcpRetransSegs - 1.3.6.1.2.1.6.12");
		tcpMibArrayList.add("tcpConnTable - 1.3.6.1.2.1.6.13");
		tcpMibArrayList.add("tcpInErrs - 1.3.6.1.2.1.6.14");
		tcpMibArrayList.add("tcpOutRsts - 1.3.6.1.2.1.6.15");

		//initialize views
		initializeView(tcpMibArrayList);
		
		//add rest of MIB
		for (int i = 0; i < tcpMibArrayList.size(); i++)
		{
			if (i == 0)
			{
				child = new DefaultMutableTreeNode(tcpMibArrayList.get(i));
				root.add(child);
			}
			else
			{
				/*
				System.out.println((tcpMibArrayList.get(i)
						.split("-")[1]));
				System.out.println(snmpViewRecord.contains((tcpMibArrayList.get(i)
						.split("-")[1]).trim()));
				*/
				
				String str = tcpMibArrayList.get(i).split("-")[1].trim();
				
				if (snmpViewRecord.get(str).toString().equalsIgnoreCase("include") ) 
				{
					grandChild = new DefaultMutableTreeNode(tcpMibArrayList.get(i));
					child.add(grandChild);
			
				}
				//else, exclude do nothing, don't add
			}
		}
		
		tree = new JTree(root);
		//add tree selection listener
		//tree.addTreeSelectionListener(this);
		
		//Add Components to this panel.
        GridBagConstraints c = new GridBagConstraints();
        c.gridwidth = GridBagConstraints.REMAINDER;
 
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1.0;
        c.weighty = 1.0;
        
        JScrollPane treePane = new JScrollPane(tree);        
		//add content
		add(treePane, c);
		currentSelectionField = new JTextField("Current Selection: NONE");
		add(currentSelectionField, c);
	}
	
	/**
	 * Check view of OID currently in the browser
	 * @param OID the specified OID
	 * @return the view of the OID
	 */
	public String checkView(String OID)
	{
		//we'll assume it'll be include by default
		String view = snmpViewRecord.get(OID);
		
		return view;
	}
	
	/**
	 * Set the view for the OID present in the browser
	 * @param OID the oid
	 * @param viewString the string view (include or exclude)
	 */
	public void setView(String OID, String viewString)
	{
		snmpViewRecord.put(OID, viewString);
	}
	
	/**
	 * All views for OID is include by default
	 */
	public void initializeView(ArrayList<String> list)
	{
		//starting at 1 because we only have tcp mib, tcp is root
		for (int i = 1; i < list.size(); i++)
		{
			//System.out.println(tcpMibArrayList.get(i));
			String[] string = list.get(i).split("-");
			snmpViewRecord.put(string[1].trim(), "include");
			
			//System.out.println(snmpViewRecord.containsKey(string[1].trim()));
		}
	}
	
	public JTree getTree()
	{
		return tree;
	}

	public void valueChanged(TreeSelectionEvent event) {
		currentSelectionField.setText("Current Selection: "
				+ tree.getLastSelectedPathComponent().toString());
	}
}
