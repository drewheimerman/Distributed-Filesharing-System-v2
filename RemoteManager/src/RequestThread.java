import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.util.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
//import java.io.*;
import java.net.*;

public class RequestThread implements Runnable {
	
	//private UDPUtilities udpUtil;
	private ConcurrentSkipListMap<Integer, String[]> availableServers;
	private DatagramPacket clientPacket;
	private Socket client;
	private RemoteManager.Management management;
	
	public RequestThread(Socket s, ConcurrentSkipListMap<Integer, String[]> c, DatagramPacket p, RemoteManager.Management m){
		//udpUtil = new UDPUtilities();
		availableServers = c;
		clientPacket = p;
		client = s;
		management = m;
	}
	
	@Override
	public void run() {
		ObjectOutputStream oos = null;
		ObjectInputStream ois = null;
		try {
			OutputStream os = client.getOutputStream();
			InputStream is = client.getInputStream();
			oos = new ObjectOutputStream(os);
			ois = new ObjectInputStream(is);
			
			FilePacket fpacket = (FilePacket)ois.readObject();
			
			//IF THE FilePacket HAS A TIMESTAMP <= THE RM TIMESTAMP
			if(fpacket.getPreviousTimestamp().get(0) <= management.getStateTimestamp().get(0) 
					&& fpacket.getPreviousTimestamp().get(1) <= management.getStateTimestamp().get(1) 
					&& fpacket.getPreviousTimestamp().get(2) <= management.getStateTimestamp().get(2)){
				
				if(fpacket.getOperation()==0){
					//fpacket.setBuffer(management.readFile(fpacket));
				}else{
					
				}
				
			}
			
			fpacket.success(true);
			oos.writeObject(fpacket);
			oos.flush();
			oos.close();
			ois.close();
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		
		
		/*int numAvailable = availableServers.size();
	
		System.out.println("number availble: " + numAvailable);
		if(numAvailable!=0){
			Iterator it = availableServers.keySet().iterator();
			Integer i = (Integer) it.next();
			String[] s = availableServers.get(i);
			System.out.println(i);
			System.out.println(s[0]+" "+s[1]);
			System.out.println(availableServers.size());
			String m = s[0]+":"+s[1];*/
			/*try {
				mUtils.sendString(m);
			} catch (IOException e) {
				
				e.printStackTrace();
			}*/
			
		//}else{
			/*try {
				//udpUtil.sendString("none");
			} catch (IOException e) {
				e.printStackTrace();
			}*/
		/*}
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}*/
	}
}
