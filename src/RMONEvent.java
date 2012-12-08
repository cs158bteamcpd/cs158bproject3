
public class RMONEvent {
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
    
}
