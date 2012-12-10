import java.net.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Scanner;
import java.io.*;
 
public class TCPServer extends Thread{
	
	private ArrayList<RMONEvent> rMonEvent;
    
    private static Socket s;
    private static int port=9000;
    public TCPServer(Socket s,ArrayList<RMONEvent> rMonEvent){
    	this.s = s;
    	this.rMonEvent = rMonEvent;
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
	    		{
	    			Hashtable<String,String> vBinding = obj.getvBinding();
	    			rMonEvent.add(new RMONEvent(Integer.parseInt(vBinding.get("1.3.6.1.2.1.16.9.1.1.1")), vBinding.get("1.3.6.1.2.1.16.9.1.1.2"),
	    					Integer.parseInt(vBinding.get("1.3.6.1.2.1.16.9.1.1.3")), vBinding.get("1.3.6.1.2.1.16.9.1.1.4"), Integer.parseInt(vBinding.get("1.3.6.1.2.1.16.9.1.1.5")),
	    					vBinding.get("1.3.6.1.2.1.16.9.1.1.6"), Integer.parseInt(vBinding.get("1.3.6.1.2.1.16.9.1.1.7"))));
	    		}
	    		if(obj.pdutype.equalsIgnoreCase("SET"))
	    		{
	    			Socket ss = new Socket("host", port);
	    			ObjectOutputStream set = new ObjectOutputStream(ss.getOutputStream());
	    			set.writeObject(obj);
	    		}
	    		if(obj.pdutype.equalsIgnoreCase("GET"))
	    		{
	    			if(obj.flag)
	    			{
	    			ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());
	    			oos.writeObject(rMonEvent);
	    			}
	    			else
	    			{
	    				Socket ss= new Socket("host", port);
	    				ObjectOutputStream get = new ObjectOutputStream(ss.getOutputStream());
	    				get.writeObject(obj);
	    				ObjectInputStream ois1 = new ObjectInputStream(ss.getInputStream());   
	    			 	SNMP response = (SNMP)ois1.readObject();
	    			 	ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());
	    			 	oos.writeObject(response);
	    			}
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
    	ArrayList<RMONEvent> rmon = new ArrayList<RMONEvent>();
    	ServerSocket ss = new ServerSocket(9000);
    	while(true)
    		new TCPServer(ss.accept(),rmon).start();   
		
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

