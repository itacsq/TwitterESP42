package example;



import java.util.ArrayList;
import java.util.logging.Level;

import com.sas.esp.api.pubsub.dfESPclient;
import com.sas.esp.api.pubsub.dfESPclientHandler;
import com.sas.esp.api.server.eventblock.EventBlockType;
import com.sas.esp.api.server.ReferenceIMPL.dfESPevent;
import com.sas.esp.api.server.ReferenceIMPL.dfESPeventblock;
import com.sas.esp.api.server.ReferenceIMPL.dfESPschema;


/**
 * [class] - Publisher
 * Handle the connection with ESP
 * 
 * 
 * ESPAdapter
 * @author itacsq - @date 15/ott/2015
 */
public class Publisher {
	
	
	private dfESPclientHandler handler;
	private dfESPclient client;
	private dfESPschema schema;
	private String windowsRef;
	private final int ID=100;
	
	/**
	 * Default Constructor with
	 * Init() method instantiate the ESP handler
	 */
	public Publisher(){
		init();
	}
	
	
	/**
	 * Manage the Event.
	 * The logic reside inside eventBody.
	 * 
	 * @param eventBody
	 * @param windowsRef
	 * @return
	 */
	public boolean manageEvent(String eventBody, String windowsRef){
		if(windowsRef == null && "".equals(windowsRef))
			return false;
		this.windowsRef=windowsRef;
		if(startSession()){
			try{
				dfESPevent ev_ins = new dfESPevent(schema, eventBody , ',');
				ArrayList<dfESPevent> _events = new ArrayList<dfESPevent>();
				_events.add(_events.size(), ev_ins);
				dfESPeventblock ib = new dfESPeventblock(_events, EventBlockType.ebt_NORMAL);
				_events.clear();
				
				if (!handler.publisherInject(client, ib)) {
					System.err.println("publisherInject() failed");
					System.exit(1);
				}
				
				stopSession();
				return true;
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		return false;
	}
	
	
	/**
	 * Start the Client Sesssion
	 * 
	 * @return
	 */
	private boolean startSession(){
		CallBacks callBack = new CallBacks();
		client = handler.publisherStart(windowsRef, callBack, 0);
		if (client == null) {
			System.err.println("publisherStart(" + windowsRef + ", myCallbacks) failed");
			System.exit(1);
		}
		try{
			if (!handler.connect(client)) {
				System.err.println("connect() failed");
				return false;
			}
			String schemaUrl = windowsRef + "?get=schema";
			ArrayList<String> schemaVector = handler.queryMeta(schemaUrl);
			if (schemaVector == null) {
				System.err.println("Schema query (" + schemaUrl + ") failed");
				return false;
			}
			schema = new dfESPschema(schemaVector.get(0));
			
			
			
			return true;
		}catch(Exception e){
			e.printStackTrace();
		}
		return false;
	}
	
	
	/**
	 * Stop the client session 
	 * 
	 * @return
	 */
	private boolean stopSession(){
		try{
			handler.stop(client, true);
			return true;
		}catch(Exception e){
			e.printStackTrace();
		}
		return false;
	}
	
	
	/**
	 * Generate a series of events.
	 * 
	 * @param windowsRef
	 */
	public void serve(String windowsRef){
		this.windowsRef=windowsRef;
		if(init()){
			if(startPublishSession()){
				System.out.println("Connected to: [" + windowsRef + "]");
			}else{
				System.err.println("Error during publication");
			}
		}else{
			System.err.println("Error during init");
		}
	}
	
	
	/**
	 * Init the connection
	 * 
	 * @return
	 */
	private boolean init(){
		handler = new dfESPclientHandler();
		if (!handler.init(Level.WARNING)) {
			System.err.println("init() failed");
			return false;
		}
		return true;
	}
	
	/**
	 * Start the automatic publication
	 * 
	 * @return
	 */
	private boolean startPublishSession(){
		CallBacks callBack = new CallBacks();
		client = handler.publisherStart(windowsRef, callBack, 0);
		if (client == null) {
			System.err.println("publisherStart(" + windowsRef + ", myCallbacks) failed");
			System.exit(1);
		}
		try{
			if (!handler.connect(client)) {
				System.err.println("connect() failed");
				return false;
			}
			String schemaUrl = windowsRef + "?get=schema";
			ArrayList<String> schemaVector = handler.queryMeta(schemaUrl);
			if (schemaVector == null) {
				System.err.println("Schema query (" + schemaUrl + ") failed");
				return false;
			}
			dfESPschema schema = new dfESPschema(schemaVector.get(0));
			
			
			
			for(int i=1; i<100; i++){
				try {
			        /* Create event block. */
					
					String id=new Integer(ID + i).toString();
					String dId=new Integer(ID + i -2).toString();
					String uId=new Integer(ID + i -1).toString();
					dfESPevent ev_ins = new dfESPevent(schema, "i,n," + id + ",CHR,3000,100.33", ',');
					
					
					System.out.println("i,n," + id + ",CHR,3000,100.33");
					
					ArrayList<dfESPevent> _events = new ArrayList<dfESPevent>();
					_events.add(_events.size(), ev_ins);
					
					if(i>3){
						dfESPevent ev_del = new dfESPevent(schema, "d,n," + dId + ",CHR,3000,0.0", ',');
						dfESPevent ev_upd = new dfESPevent(schema, "u,n," + uId + ",CHR,3000,200.66", ',');
						_events.add(_events.size(), ev_del);
						_events.add(_events.size(), ev_upd);
					}
					
					
					dfESPeventblock ib = new dfESPeventblock(_events, EventBlockType.ebt_NORMAL);
					_events.clear();
					
					if (!handler.publisherInject(client, ib)) {
						System.err.println("publisherInject() failed");
						System.exit(1);
					}
					System.out.println("Events injected");
				} catch (Exception e) {
					// hit end-of-file, just quit
					e.printStackTrace();
				}
				
				try {
				    Thread.sleep(5000);
				} catch(InterruptedException ex) {
				    Thread.currentThread().interrupt();
				}
				
			}
		    /* Stop pubsub, but block (i.e., true) to ensure that all queued events are first processed. */
			handler.stop(client, true);
			

			return true;
		}catch(Exception e){
			e.printStackTrace();
		}
		
		
		return false;
	}

}
