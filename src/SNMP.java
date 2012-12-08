import java.io.Serializable;
import java.util.Hashtable;


public class SNMP implements Serializable
{
	 
	private static final long serialVersionUID = -2084003647637684925L;
	public Hashtable<String,String> vBinding; // the variable binding, OID and value of MIB object
	public String pdutype = "RESPONSE";
	
	private String community; // the community string

	private String host; // the host ID
	private String version; //version of SNMP
	  
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
	
 
	
	public Hashtable<String, String> getvBinding() {
		return vBinding;
	}

	public void setvBinding(Hashtable<String, String> vBinding) {
		this.vBinding = vBinding;
	}

	public String getPdutype() {
		return pdutype;
	}

	public void setPdutype(String pdutype) {
		this.pdutype = pdutype;
	}

	public String getCommunity() {
		return community;
	}

	public void setCommunity(String community) {
		this.community = community;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	//missing snmp traps
}