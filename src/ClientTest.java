import java.io.BufferedReader;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Hashtable;

// THIS IS ONLY A TEST.
// THIS IS NOT HOW THE CLIENT SHOULD BEHAVE.
//

public class ClientTest {
	public static void main(String[]args){
		try {
			Socket s = new Socket("localhost", 9000);
			
			SNMP snmp = null;
			
			BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		    
			System.out.print("Enter oid: '");
			
			String oid = in.readLine();
			
			
			
			System.out.print("Enter community String: '");
			
			String community = in.readLine();
			
			System.out.print("Get Or Set: '");
			
			String getOrSet = in.readLine();
			
			if( getOrSet.equalsIgnoreCase("get")){
			
				Hashtable<String,String> ht = new Hashtable<String,String>();
				
				ht.put(oid, oid);
				
				snmp = new SNMP("1",community,"1","GET", ht);
				
			} else {
				
				System.out.print("Enter New Value: '");
				
				String value = in.readLine();
				
				Hashtable<String,String> ht = new Hashtable<String,String>();
				
				ht.put(oid, value);
				
				snmp = new SNMP("1",community,"1","SET", ht);
				
			}
			
			
			ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());
			
			
			oos.writeObject(snmp);
			
			
			//Now Wait for response
			
			ObjectInputStream ois = new ObjectInputStream(s.getInputStream());	
			
			SNMP response = (SNMP)ois.readObject();
			
			System.out.println(response.vBinding.get(oid));
			
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			System.out.println("Unknown Host");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Error retreving data");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
}
