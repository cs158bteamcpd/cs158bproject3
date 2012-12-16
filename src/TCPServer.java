import java.net.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.io.*;
 
public class TCPServer extends Thread{
	
	private static ArrayList<RMONEvent> rMonEvent;
    
    private ServerSocket s;
    private static int neport=9000;
    private Hashtable<String,String> ACL = new Hashtable<String,String>();
    private final static int cport = 9005;
    public TCPServer(ServerSocket soc,ArrayList<RMONEvent> rMonEvent, Hashtable<String, String> list){
    	s = soc;
    	TCPServer.rMonEvent = rMonEvent;
    	ACL = list;
    }
    public void run()
    {
    	try {
    		int count =0;
    		while(!s.isClosed()){
    			Socket socket = s.accept();
    			InputStream is = socket.getInputStream();
    			ObjectInputStream ois = new ObjectInputStream(is);  
    			SNMP obj = (SNMP)ois.readObject();
	    		if(obj.pdutype.equalsIgnoreCase("TRAP")&& checkCommunity(obj.getCommunity())>0)
	    		{
	    			Hashtable<String,String> vBinding = obj.getvBinding();
	    			rMonEvent.add(new RMONEvent(Integer.parseInt(vBinding.get("1.3.6.1.2.1.16.9.1.1.1")), vBinding.get("1.3.6.1.2.1.16.9.1.1.2"),
	    			Integer.parseInt(vBinding.get("1.3.6.1.2.1.16.9.1.1.3")), vBinding.get("1.3.6.1.2.1.16.9.1.1.4"), Integer.parseInt(vBinding.get("1.3.6.1.2.1.16.9.1.1.5")),
	    			vBinding.get("1.3.6.1.2.1.16.9.1.1.6"), Integer.parseInt(vBinding.get("1.3.6.1.2.1.16.9.1.1.7"))));
	    			System.out.println(rMonEvent.toString());
	    		}
	    		else if(obj.pdutype.equalsIgnoreCase("GET")&& checkCommunity(obj.getCommunity())<=1)
	    		{
	    			if(obj.flag)
	    			{
	    			ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
	    			oos.flush();
	    			oos.writeObject(rMonEvent.get(0));
	    			oos.flush();
	    			oos.close();
	    			}
	    			else
	    			{
	    				System.out.println("Enter Else");
	    				Socket ss= new Socket("localhost", neport);
	    				ObjectOutputStream get = new ObjectOutputStream(ss.getOutputStream());
	    				get.flush();
	    				get.writeObject(obj);
	    				ObjectInputStream ois1 = new ObjectInputStream(ss.getInputStream());   
	    			 	SNMP response = (SNMP)ois1.readObject();
	    			 	ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
	    			 	oos.flush();
	    			 	oos.writeObject(response);
	    			 	oos.flush();
	    			 	oos.close();
	    			 	get.flush();
	    			 	get.close();
	    			 	ss.close();
	    			}
	    		}
	    		else if(obj.pdutype.equalsIgnoreCase("SET")&& checkCommunity(obj.getCommunity())>0)
	    		{
	    			Socket ss = new Socket("localhost", neport);
	    			ObjectOutputStream set = new ObjectOutputStream(ss.getOutputStream());
	    			set.flush();
	    			set.writeObject(obj);
	    			set.flush();
	    			set.close();
	    			ss.close();
	    		}
	    		else {
	    			ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
	    			Hashtable<String,String> ht = new Hashtable<String,String>();
	    			Enumeration<?> e = obj.getvBinding().keys();
	    			String key = (String) e.nextElement();
					ht.put(key, "Community String is wrong");
					SNMP snmp = new SNMP("1",obj.getCommunity(),"1","RESPONSE", ht);
					out.writeObject(snmp);
					out.flush();
					out.close();
	    		}
	    		//count++;
	    		//System.out.println("PDU TYPE: " +obj.pdutype);
	    		//System.out.println(s.isClosed());
	    		//System.out.println(count);
	    		is.close();
	    		ois.close();

    		}
    		//is.close();  
    		//s.close();    
    		}catch(Exception e){
    			System.out.println(e.getMessage());
    		}
    	
    }
    
	private int checkCommunity(String community) {
		if(ACL.get(community).equals("RW"))
			return 1;
		else
		return 0;
	}
	public static void main(String[] args) throws IOException
    {
    	ArrayList<RMONEvent> rmon = new ArrayList<RMONEvent>();
    	ServerSocket ness = new ServerSocket(neport);
    	ServerSocket cls = new ServerSocket(cport);
    	Hashtable<String,String> list = new Hashtable<String,String>();
    	list.put("public", "RO");
    	list.put("password", "RW");
    	//ServerSocket cis = new ServerSocket(cport);
    	TCPServer server = new TCPServer(ness, rmon, list);
    	TCPServer client = new TCPServer(cls, rmon, list);
		server.start();
		client.start();
    	while(!(ness.isClosed()&&cls.isClosed()));
    	ness.close();
    	
    }
}

