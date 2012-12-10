import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Random;


public class NetworkElement{
	String server = "localhost";
	int port = 9000;
	String id = "network-1";
	String community = "password";
	Hashtable<String, Integer> tcpMIB = new Hashtable<String, Integer>();
	
	ArrayList<RMONAlarm> alarms;
	ArrayList<RMONEvent> events;
	public NetworkElement(){
		community = "password";
		alarms = new ArrayList<RMONAlarm>();
		events = new ArrayList<RMONEvent>();
	}
	
	public void generateMIB(){
		tcpMIB.put("1.3.6.1.2.1.6.1", 0);	//tcpRtoAlgorithm - Integer32
		tcpMIB.put("1.3.6.1.2.1.6.2", 0);	//tcpRtoMin - Integer32
		tcpMIB.put("1.3.6.1.2.1.6.3", 0);	//tcpRtoMax - Integer32
		tcpMIB.put("1.3.6.1.2.1.6.4", 0); 	// tcpMaxConn - Integer32
		tcpMIB.put("1.3.6.1.2.1.6.5", 0); 	// tcpActiveOpens- Counter32 
		tcpMIB.put("1.3.6.1.2.1.6.6", 0); 	// tcpPassiveOpens - Counter32 
		tcpMIB.put("1.3.6.1.2.1.6.7", 0); 	// tcpAttemptFails - Counter32 
		tcpMIB.put("1.3.6.1.2.1.6.8", 0); 	// tcpEstabResets- Counter32 
		tcpMIB.put("1.3.6.1.2.1.6.9", 0); 	// tcpCurrEstab - Counter32 
		tcpMIB.put("1.3.6.1.2.1.6.10", 0); 	// tcpInSegs - Counter32 
		tcpMIB.put("1.3.6.1.2.1.6.11", 0); 	// tcpOutSegs - Counter32 
		tcpMIB.put("1.3.6.1.2.1.6.12", 0);	// tcpRetransSegs - Counter32 
											// 13 is entry table
		tcpMIB.put("1.3.6.1.2.1.6.14", 0); 	// tcpInErrs  - Counter32 
		tcpMIB.put("1.3.6.1.2.1.6.15", 0); 	// tcpOutRsts - Counter32 
	}
	public void startGenerator(){
		new EventGenerator(this).start();
	}
	
	public void startAlarmMonitor(){
		for(RMONAlarm a: alarms){
			if(tcpMIB.containsKey(a.alarmVariable)){
					new AlarmMonitor(this,a).start();
			}
		}
	}
	
	public void addAlarm(int interval, String oid, int maxThreshold){
		alarms.add(new RMONAlarm(alarms.size(),interval,oid,maxThreshold));
	}
	
	
	public static void main(String[]args){
		NetworkElement ne = new NetworkElement();
		ne.generateMIB();
		ne.addAlarm(1, "1.3.6.1.2.1.6.4", 50);
		ne.addAlarm(1, "1.3.6.1.2.1.6.14", 70);
		ne.startGenerator();
		ne.startAlarmMonitor();
		new CommunicationManager(9000,ne).start();
	}
	
}



class CommunicationManager extends Thread{
	private ServerSocket socket;
	NetworkElement ne;
	public CommunicationManager(int port, NetworkElement ne){
		this.ne = ne;
		try {
			socket = new ServerSocket(port);
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void run(){
		while(true){
			try {
				new SNMPInterperter(socket.accept(),ne).start();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}
}

class SNMPInterperter extends Thread{
	private Socket socket;
	SNMP snmp;
	NetworkElement ne;
	public SNMPInterperter(Socket socket,NetworkElement ne){
		this.ne = ne;
		try {
			this.socket = socket;
			InputStream is = socket.getInputStream();
			ObjectInputStream ois = new ObjectInputStream(is);
			snmp = (SNMP) ois.readObject();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void run(){
		try {
			OutputStream  os = socket.getOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(os);  
			Hashtable oid = snmp.vBinding;
			
			Enumeration e = oid.keys();
			String key = (String)e.nextElement();
			String value = snmp.vBinding.get(key);
			if(!(snmp. getCommunity().equalsIgnoreCase(ne.community))){
				
				System.out.println(snmp. getCommunity() +"!+"+ne.community);
				Hashtable<String,String> ht = new Hashtable<String,String>();
				ht.put(key, "Community String is wrong");
				SNMP snmp = new SNMP("1",ne.community,"1","RESPONSE", ht);
				oos.writeObject(snmp);
				oos.close();
			}
			else if(snmp.pdutype.equalsIgnoreCase("GET")){
				if(ne.tcpMIB.get(key) != null){
					System.out.println("GET");
					int x = ne.tcpMIB.get(key);
					//Create new SNMP object;
					Hashtable<String,String> ht = new Hashtable<String,String>();
					ht.put(key, Integer.toString(x));
					SNMP snmp = new SNMP("1",ne.community,"1","RESPONSE", ht);
					oos.writeObject(snmp);
					oos.close();
				}
				
			}
			else if(snmp.pdutype.equalsIgnoreCase("SET")){
				if(ne.tcpMIB.get(key) != null){
					try{
						ne.tcpMIB.put(key, Integer.parseInt(value));
					} catch (NumberFormatException n){
						Hashtable<String,String> ht = new Hashtable<String,String>();
						ht.put(key, "Error inputing new value");
						SNMP snmp = new SNMP("1",ne.community,"1","RESPONSE", ht);
						oos.writeObject(snmp);
						oos.close();
					}
					
					int x = ne.tcpMIB.get(key);
					
					//Create new SNMP object;
					Hashtable<String,String> ht = new Hashtable<String,String>();
					ht.put(key, Integer.toString(x));
					SNMP snmp = new SNMP("1",ne.community,"1","RESPONSE", ht);
					oos.writeObject(snmp);
					oos.close();
				}
				
			}
			socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

class EventGenerator extends Thread{
	NetworkElement ne;
	public EventGenerator(NetworkElement ne){
		this.ne = ne;
	}
	
	public void run(){
		while(true){
			Random rand = new Random();
			ne.tcpMIB.put("1.3.6.1.2.1.6.4", rand.nextInt(100)); 	// tcpMaxConn
			ne.tcpMIB.put("1.3.6.1.2.1.6.8",  rand.nextInt(100)); 	// tcpEstabResets
			ne.tcpMIB.put("1.3.6.1.2.1.6.12",  rand.nextInt(100));	// tcpRetransSegs
			ne.tcpMIB.put("1.3.6.1.2.1.6.14",  rand.nextInt(100)); 	// tcpInErrs
			ne.tcpMIB.put("1.3.6.1.2.1.6.15",  rand.nextInt(100)); 	// tcpOutRsts
			
			
			ne.tcpMIB.put("1.3.6.1.2.1.6.5", rand.nextInt(10)); 	// tcpActiveOpens
			ne.tcpMIB.put("1.3.6.1.2.1.6.6", rand.nextInt(10)); 	// tcpPassiveOpens
			ne.tcpMIB.put("1.3.6.1.2.1.6.7", rand.nextInt(10)); 	// tcpAttemptFails
			ne.tcpMIB.put("1.3.6.1.2.1.6.8", rand.nextInt(10)); 	// tcpEstabResets
			ne.tcpMIB.put("1.3.6.1.2.1.6.9", rand.nextInt(10)); 	// tcpCurrEstab
			ne.tcpMIB.put("1.3.6.1.2.1.6.10", rand.nextInt(10)); 	// tcpInSegs
			ne.tcpMIB.put("1.3.6.1.2.1.6.11", rand.nextInt(10)); 	// tcpOutSegs
			ne.tcpMIB.put("1.3.6.1.2.1.6.12", rand.nextInt(10));	// tcpRetransSegs
			ne.tcpMIB.put("1.3.6.1.2.1.6.14", rand.nextInt(10)); 	// tcpInErrs
			ne.tcpMIB.put("1.3.6.1.2.1.6.15", rand.nextInt(10)); 	// tcpOutRsts
			
			
			
			
			
		}
	}
}

class AlarmMonitor extends Thread{
	NetworkElement ne;
	RMONAlarm a;
	public AlarmMonitor(NetworkElement ne, RMONAlarm a){
		this.ne = ne;
		this.a = a;
	}
	public void run(){
		while(true){
			try {
				Thread.sleep(a.alarmInterval * 1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			int value = ne.tcpMIB.get((String)a.alarmVariable);
			//System.out.println(value);
			if(value > a.alarmRisingThreshold){
				System.out.println(a.alarmVariable + " Threshold Breached!!!!");
				RMONEvent d = new RMONEvent(ne.events.size(), a.alarmVariable+" is above threshold",3,ne.community,0,ne.id,1);
				ne.events.add(d);
				
				//Send trap!!!!!!
				try {
					Socket socket =new Socket(ne.server, ne.port);
					OutputStream  os = socket.getOutputStream();
					ObjectOutputStream oos = new ObjectOutputStream(os);  
				

					Hashtable<String,String> ht = new Hashtable<String,String>();
					ht.put("1.3.6.1.2.1.16.9.1.1.1", Integer.toString(d.eventIndex));
					ht.put("1.3.6.1.2.1.16.9.1.1.2", d.eventDescription);
					ht.put("1.3.6.1.2.1.16.9.1.1.3", Integer.toString(d.eventType));
					ht.put("1.3.6.1.2.1.16.9.1.1.4", d.eventCommunity);
					ht.put("1.3.6.1.2.1.16.9.1.1.5", Integer.toString(d.eventLastTimeSent));
					ht.put("1.3.6.1.2.1.16.9.1.1.6", d.eventOwner);
					ht.put("1.3.6.1.2.1.16.9.1.1.7", Integer.toString(d.eventStatus));
					
					SNMP snmp = new SNMP("1",ne.community,"1","TRAP", ht);
					oos.writeObject(snmp);
					oos.close();
					os.close();
					socket.close();
				} catch (UnknownHostException e) {
					// TODO Auto-generated catch block
					//System.out.println("Unknown host");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		}
	}
}