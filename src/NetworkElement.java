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
	int port = 9999;
	String id = "network-1";
	String community;
	Hashtable<String, Integer> tcpMIB = new Hashtable<String, Integer>();
	
	ArrayList<RMONAlarm> alarms;
	ArrayList<RMONEvent> events;
	public NetworkElement(){
		community = "public";
		alarms = new ArrayList<RMONAlarm>();
		events = new ArrayList<RMONEvent>();
	}
	
	public void generateMIB(){
		tcpMIB.put("1.3.6.1.2.1.6.4", 0); 	// tcpMaxConn
		tcpMIB.put("1.3.6.1.2.1.6.8", 0); 	// tcpEstabResets
		tcpMIB.put("1.3.6.1.2.1.6.12", 0);	// tcpRetransSegs
		tcpMIB.put("1.3.6.1.2.1.6.14", 0); 	// tcpInErrs
		tcpMIB.put("1.3.6.1.2.1.6.15", 0); 	// tcpOutRsts
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
		new CommunicationManager(9999,ne).start();
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
			
			if(ne.tcpMIB.get(key) != null){
				int x = ne.tcpMIB.get(key);
				//Create new SNMP object;
				Hashtable<String,String> ht = new Hashtable<String,String>();
				ht.put(key, Integer.toString(x));
				SNMP snmp = new SNMP("1","Public","1","RESPONSE", ht);
				oos.writeObject(snmp);
				oos.close();
			}
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
			System.out.println(value);
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
					System.out.println("Unknown host");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		}
	}
}