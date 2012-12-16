import java.io.Serializable;


public class RMONEvent implements Serializable{
	int eventIndex;         
    String eventDescription;  
    int eventType; 
    String eventCommunity;     
    int eventLastTimeSent;   
    String eventOwner;          
    int eventStatus;         
    
    public RMONEvent(int eventIndex,String eventDescription, int eventType,String eventCommunity,int eventLastTimeSent,String eventOwner,int eventStatus){
    	this.eventIndex = eventIndex;
    	this.eventDescription = eventDescription;
    	this.eventType = eventType;
    	this.eventCommunity = eventCommunity;
    	this.eventLastTimeSent = eventLastTimeSent;
    	this.eventOwner = eventOwner;
    	this.eventStatus = eventStatus;
    }
    
    public String toString()
    {
    	return "Index: " + this.eventIndex + " Description: " 
    			+ this.eventDescription + " Type: " + this.eventType 
    			+ " Community: " + this.eventCommunity 
    			+ " Last Time Sent: " + this.eventLastTimeSent 
    			+ " Owner: " + this.eventOwner 
    			+ " Status: " + this.eventStatus; 
    }
}
