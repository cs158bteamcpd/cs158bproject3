import java.net.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Scanner;
import java.io.*;
 
public class TCPServer extends Thread{
	
	private ArrayList<SNMP> snmp;
    
    private static Socket s;
    private static int port=9000;
    public TCPServer(Socket s,ArrayList<SNMP> snmp){
    	this.s = s;
    	this.snmp = snmp;
    }
    public void run()
    {
    	// port = 9999; // Default Port
    	// Allow changing the default port
    	//if(args.length > 0)
    		//port=Integer.parseInt(args[0]);
    	try {
    		
    	
    		InputStream is = s.getInputStream();
    		
    		
    		while(!s.isClosed()){
	    		ObjectInputStream ois = new ObjectInputStream(is);  
	    		SNMP obj = (SNMP)ois.readObject();
	    		if(obj.pdutype.equalsIgnoreCase("TRAP"))
	    		snmp.add(obj);
	    		if(obj.pdutype.equalsIgnoreCase("GET"))
	    		{
	    			ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());
	    			oos.writeObject(snmp);
	    		}
	    		System.out.println("PDU TYPE: " +obj.pdutype);
	    		
	    		
	    		ois.close();

    		}
    		is.close();  
    		s.close();    
    		}catch(Exception e){
    			e.printStackTrace();
    		}
    	
    }
    
	public static void main(String[] args) throws IOException
    {
    	//Socket s = new Socket("localhost",port); 
    	ArrayList<SNMP> snmp = new ArrayList<SNMP>();
    	ServerSocket ss = new ServerSocket(9000);
    	while(true)
    		new TCPServer(ss.accept(),snmp).start();   
		
    /*String command = "" ;
    while (!command.equalsIgnoreCase("Exit"))
    {
    	Scanner in = new Scanner(System.in);
    	command= in.nextLine();
    	String [] cmd = command.split(" ");
    	if(cmd[0].equalsIgnoreCase("show")&&cmd[1].equalsIgnoreCase("alarms"))
    	{
    		for (int i=0; i<snmp.size(); i++)
    		{
    			Enumeration<String> key = snmp.get(i).vBinding.keys();
    			System.out.println(snmp.get(i).vBinding.get(key));
    		}
    	}
    	if(cmd[0].equalsIgnoreCase("Show"))
    	{
    		for (int i=0; i<snmp.size(); i++)
    		{
    			if (snmp.get(i).vBinding.containsKey(cmd[1]))
    			{
    				System.out.println(snmp.get(i).vBinding.get(cmd[1]));
    			}
    		}
    	}
    	
    }
    ss.close();
    */
}
}

