package example;

/* import hibernate.manager.DBManager; */

import com.sas.esp.api.dfESPException;
import com.sas.esp.api.pubsub.clientCallbacks;
import com.sas.esp.api.pubsub.clientFailureCodes;
import com.sas.esp.api.pubsub.clientFailures;
import com.sas.esp.api.pubsub.clientGDStatus;
import com.sas.esp.api.server.event.EventOpcodes;
import com.sas.esp.api.server.ReferenceIMPL.dfESPevent;
import com.sas.esp.api.server.ReferenceIMPL.dfESPeventblock;
import com.sas.esp.api.server.ReferenceIMPL.dfESPschema;

public class CallBacks implements clientCallbacks {
	/** Flag indicating whether to keep main in a non-busy wait while the publisher does its thing. */
	static boolean nonBusyWait = true;
	/* private DBManager db = new DBManager(); */
	private String type;
	private int TH_BID = 20;
	private int TH_SAS = 100;
	
	
	public void setType(String type){
		this.type=type;
	}
	
	private void update(String eventStr){
		if("bid".equals(type)){
			try{
				String val = eventStr.split(":")[1].split(",")[1];
				System.out.println("=> New Value: <" + val + ">");
				if(Integer.parseInt(val)>TH_BID){
					// db.updateCounter("valbid", "0");
				}else{
					// db.updateCounter("valbid", val);
				}
				
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		if("sas".equals(type)){
			try{
				String val = eventStr.split(":")[1].split(",")[1];
				System.out.println("=> New Value: <" + val + ">");
				if(Integer.parseInt(val)>TH_SAS){
					// db.updateCounter("valsas", "0");
				}else{
					// db.updateCounter("valsas", val);
				}
				
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		if("text".equals(type)){
			try{
				String val = eventStr.split(":")[1].split(",")[1];
				System.out.println("=> New Value: <" + val + ">");
				// db.updateCounter("usertweet", val);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * dfESPGDpublisherCB_func
	 * Dummy function not used but required for interface
	 */
	public void dfESPsubscriberCB_func(dfESPeventblock eventBlock, dfESPschema schema, Object ctx) {
		dfESPevent event;
		int eventCnt = eventBlock.getSize();

		for (int eventIndx = 0; eventIndx < eventCnt; eventIndx++) {
	        /* Get the event out of the event block. */
			event = eventBlock.getEvent(eventIndx);
			try {
		        /* Convert from binary to CSV using the schema and print to System.err. */
			    String eventStr = event.toStringCSV(schema, true, false);
				System.out.println("=> New Event: <" + eventStr + ">");
				update(eventStr);
			} catch (dfESPException e) {
				System.err.println("event.toString() failed");
				return;
			}
			if (event.getOpcode() == EventOpcodes.eo_UPDATEBLOCK) {
				++eventIndx;  /* skip the old record in the update block */
			}
		}
		
	}
	
	/**
	 * Callback function for publisher failures given
	   we may want to try to reconnect/recover, but in this example we will just print
	   out some error information and release the non-busy wait set below so the 
	   main in program can end. The cbf has an optional context pointer for sharing state
	   across calls or passing state into calls.
	 *
/*
	public void dfESPpubsubErrorCB_func(clientFailures failure, clientFailureCodes code, Object ctx) {
		switch (failure) {
	    case pubsubFail_APIFAIL:
	    	System.err.println("Client subscription API error with code " + code);
	        break;
	    case pubsubFail_THREADFAIL:
	        System.err.println("Client subscription thread error with code " + code);
	        break;
	    case pubsubFail_SERVERDISCONNECT:
	        System.err.println("Server disconnect");
		}
		nonBusyWait = false;
	}
*/
	public void dfESPpubsubErrorCB_func(clientFailures failure, clientFailureCodes code, Object ctx) {
	    if (failure == clientFailures.pubsubFail_APIFAIL) {
	    	if (code != clientFailureCodes.pubsubCode_CLIENTEVENTSQUEUED) {  /* don't print error for client busy */
	            System.err.println("Client services api failed with code: " + code);
	    	}
	    }
	    else {
	    	System.err.println("Client services thread failed with code: " + code);
	    }
	    /* Release the busy wait which will end the program. */
		nonBusyWait = false;
	}
	/**
	 *  We need to define a dummy publish method which is only used when implementing guaranteed delivery.
	*/
	public void dfESPGDpublisherCB_func(clientGDStatus arg0, long arg1, Object arg2) {}
	
	
	public boolean getNonBusyWait() {
		return nonBusyWait;
	}

	
	
	
	
	

}
