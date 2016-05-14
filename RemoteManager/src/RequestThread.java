import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.util.*;
import java.io.IOException;
//import java.io.*;
import java.net.*;

public class RequestThread implements Runnable {
	
	//private UDPUtilities udpUtil;
	private ConcurrentSkipListMap<Integer, String[]> availableServers;
	private DatagramPacket clientPacket;
	private Socket client;
	
	public RequestThread(Socket s, ConcurrentSkipListMap<Integer, String[]> c, DatagramPacket p){
		//udpUtil = new UDPUtilities();
		availableServers = c;
		clientPacket = p;
		client = s;
	}
	
	@Override
	public void run() {
		
		System.out.println("RequestThread");
		//udpUtil.setDestination(clientPacket.getAddress());
		//udpUtil.setDestPort(clientPacket.getPort());
		String message = new String(clientPacket.getData(), 0, clientPacket.getLength());
		int numAvailable = availableServers.size();
		
		String action = "";
		String filename = "";
		filename = message;
		if(action.equals("upload")){
			
		}
		System.out.println("number availble: " + numAvailable);
		if(numAvailable!=0){
			Iterator it = availableServers.keySet().iterator();
			Integer i = (Integer) it.next();
			String[] s = availableServers.get(i);
			System.out.println(i);
			System.out.println(s[0]+" "+s[1]);
			System.out.println(availableServers.size());
			String m = s[0]+":"+s[1];
			/*try {
				mUtils.sendString(m);
			} catch (IOException e) {
				
				e.printStackTrace();
			}*/
			
		}else{
			/*try {
				//udpUtil.sendString("none");
			} catch (IOException e) {
				e.printStackTrace();
			}*/
		}
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
