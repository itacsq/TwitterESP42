package example;

import java.io.File;
import java.util.Calendar;
import java.util.Random;
import java.util.Scanner;


/**
 * [Class] Kajita
 * Project Runner
 * 
 * 
 * 
 * ESPAdapter
 * @author itacsq - @date 15/ott/2015
 */
public class Kajita {
	private Publisher pub;

	/**
	 * Generate a loop of 500 events wich inject, 
	 * delete ad update
	 * 
	 * @param csvInputPath
	 * @param windowsRef
	 */
	public void serve(String windowsRef){
		System.out.println("Starting App");
		try{
			pub = new Publisher();
			pub.serve(windowsRef);
		}catch(Exception e){
			e.printStackTrace();
		}
		
		System.out.println("Ending App");
	}
	
	/**
	 * Handle the Event Management
	 * 
	 * @param eventBody
	 * @param windowsRef
	 */
	public void manageEvent(String eventBody, String windowsRef){
		System.out.println("WindowsRef: [" + windowsRef + "]");
		System.out.println("Event: [" + eventBody + "]");
		try{
			pub = new Publisher();
			pub.manageEvent(eventBody, windowsRef);
		}catch(Exception e){
			System.err.println("Errore .... .. .");
			e.printStackTrace();
		}
	}
	
	
	public String getLine(){
		String line = "";
		Random rand = new Random();
		String result = "";
		int n = 0;
		try{
			for(Scanner sc = new Scanner(new File("res/customer.csv")); sc.hasNext(); ){
				++n;
				line = sc.nextLine();
				if(rand.nextInt(n) == 0)
					result = line;         
			}
		}catch(Exception e){e.printStackTrace();}
		return result;
	}
	
	private String createEvent(){
		Calendar calendar = Calendar.getInstance();
		String ts = calendar.getTimeInMillis() + "";
		String source = getLine().split(";")[0];
		double latitude = new Random().nextDouble()*100;
		double longitude = new Random().nextDouble()*100;
		int status = new Random().nextInt(2);
		String event = "i,n";
		event += "," + ts;
		event += "," + source;
		event += "," + latitude;
		event += "," + longitude;
		event += "," + status;
		return event;
	}
	
	/**
	 * Main
	 * 
	 * @param args
	 */
	public static void main(String[] args){
		if (args == null || args.length != 2) {
			args = new String[2];
			args[0] = "i,n,201602201702,S10005,10001.51,10001.52,0";
			args[1] = "dfESP://sasserver01.race.sas.com:5555/Project_1/cq1/src";
		}
		Kajita k = new Kajita();
		//k.manageEvent(args[0], args[1]);
		try{Thread.sleep(1000);}catch(Exception e){}
		
		
		
		
		while(true){
			String ev = k.createEvent();
			System.out.println(ev);
			
			//k.manageEvent(ev, args[1]);
			k.manageEvent(args[0], args[1]);
			try{Thread.sleep(1000);}catch(Exception e){}
		}
		

	}

}
