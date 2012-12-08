
public class RMONAlarm extends Thread {
	 int alarmIndex;			//1.3.6.1.2.1.16.3.1.1.1
	 int alarmInterval;			//1.3.6.1.2.1.16.3.1.1.2
	 String alarmVariable;		//1.3.6.1.2.1.16.3.1.1.3
	 int alarmSampleType;		//1.3.6.1.2.1.16.3.1.1.4
	 int alarmValue;			//1.3.6.1.2.1.16.3.1.1.5
	 int alarmStartupAlarm;		//1.3.6.1.2.1.16.3.1.1.6
	 int alarmRisingThreshold;	//1.3.6.1.2.1.16.3.1.1.7
	 int alarmFallingThreshold;	//1.3.6.1.2.1.16.3.1.1.8
	 int alarmRisingEventIndex;	//1.3.6.1.2.1.16.3.1.1.9
	 int alarmFallingEventIndex;//1.3.6.1.2.1.16.3.1.1.10
	 String alarmOwner;			//1.3.6.1.2.1.16.3.1.1.11
	 String alarmStatus;		//1.3.6.1.2.1.16.3.1.1.12
	 
	 public RMONAlarm( int alarmIndex, int alarmInterval, String alarmVariable, int alarmRisingThreshold ){
		 this.alarmIndex = alarmIndex;
		 this.alarmInterval = alarmInterval;
		 this.alarmVariable = alarmVariable;
		 this.alarmRisingThreshold = alarmRisingThreshold;
	 }
}
