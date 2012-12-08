import java.io.Serializable;
import java.util.Hashtable;


public class SNMP implements Serializable
{
 /**
	 * 
	 */
	private static final long serialVersionUID = -2084003647637684925L;
public Hashtable<String,String> vBinding; // the variable binding, OID and value of MIB object
 public String pdutype = "RESPONSE";

 public String community; // the community string
 public String host; // the host ID
 public String version; //version of SNMP
  
 public SNMP(String hostID, String communityStr, String versionSNMP, String pdutype, Hashtable<String,String> variableBinding)
 {
 	//OID = objectID;
 	//value = vvalue;
 	this.pdutype = pdutype;
 	vBinding = variableBinding;
 	community = communityStr;
 	host = hostID;
 	version = versionSNMP;
 }

 //missing snmp traps
}