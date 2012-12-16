import java.awt.*;

import javax.swing.*;
import javax.swing.tree.*;
import javax.swing.event.*;

public class MibBrowser extends JPanel implements TreeSelectionListener {
	
	private JTree tree;
	private JTextField currentSelectionField;

	public MibBrowser() 
	{		
		super(new GridBagLayout());
		
		DefaultMutableTreeNode root = new DefaultMutableTreeNode("mib-2 - 1.3.6.1.2.1");
		DefaultMutableTreeNode child;
		DefaultMutableTreeNode grandChild;
		
		//system 
		child = new DefaultMutableTreeNode("system - 1.3.6.1.2.1.1");
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
		child.add(grandChild);
		
		//tcp
		child = new DefaultMutableTreeNode("tcp - 1.3.6.1.2.1.6");
		root.add(child);
		grandChild = new DefaultMutableTreeNode("tcpRtoAlgorithm - 1.3.6.1.2.1.6.1");
		child.add(grandChild);
		grandChild = new DefaultMutableTreeNode("tcpRtoMin - 1.3.6.1.2.1.6.2");
		child.add(grandChild);
		grandChild = new DefaultMutableTreeNode("tcpRtoMax- 1.3.6.1.2.1.6.3");
		child.add(grandChild);
		grandChild = new DefaultMutableTreeNode("tcpMaxConn - 1.3.6.1.2.1.6.4");
		child.add(grandChild);
		grandChild = new DefaultMutableTreeNode("tcpActiveOpens - 1.3.6.1.2.1.6.5");
		child.add(grandChild);
		grandChild = new DefaultMutableTreeNode("tcpPassiveOpens - 1.3.6.1.2.1.6.6");
		child.add(grandChild);
		grandChild = new DefaultMutableTreeNode("tcpAttemptFails - 1.3.6.1.2.1.6.7");
		child.add(grandChild);
		grandChild = new DefaultMutableTreeNode("tcpEstabResets - 1.3.6.1.2.1.6.8");
		child.add(grandChild);
		grandChild = new DefaultMutableTreeNode("tcpCurrEstab - 1.3.6.1.2.1.6.9");
		child.add(grandChild);
		grandChild = new DefaultMutableTreeNode("tcpInSegs - 1.3.6.1.2.1.6.10");
		child.add(grandChild);
		grandChild = new DefaultMutableTreeNode("tcpOutSegs - 1.3.6.1.2.1.6.11");
		child.add(grandChild);
		grandChild = new DefaultMutableTreeNode("tcpRetransSegs - 1.3.6.1.2.1.6.12");
		child.add(grandChild);
		grandChild = new DefaultMutableTreeNode("tcpConnTable - 1.3.6.1.2.1.6.13");
		child.add(grandChild);
		grandChild = new DefaultMutableTreeNode("tcpInErrs - 1.3.6.1.2.1.6.14");
		child.add(grandChild);
		grandChild = new DefaultMutableTreeNode("tcpOutRsts - 1.3.6.1.2.1.6.15");
		child.add(grandChild);
		
		
		tree = new JTree(root);
		//add tree selection listener
		tree.addTreeSelectionListener(this);
		
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

	public void valueChanged(TreeSelectionEvent event) {
		currentSelectionField.setText("Current Selection: "
				+ tree.getLastSelectedPathComponent().toString());
	}
}
