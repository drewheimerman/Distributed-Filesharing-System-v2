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
	private Socket client;
	private Socket server;
	private RemoteManager.Management management;
	private ConcurrentHashMap<String, Integer> versions;
	
	public RequestThread(Socket s, ConcurrentSkipListMap<Integer, String[]> c, RemoteManager.Management m, ConcurrentHashMap<String, Integer> v){
		//udpUtil = new UDPUtilities();
		availableServers = c;
		client = s;
		management = m;
		versions = v;
		
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
			System.err.println("get FilePacket");
			//IF THE FilePacket HAS A TIMESTAMP <= THE RM TIMESTAMP
			System.out.println(fpacket.getPreviousTimestamp()+""+management.getStateTimestamp());
			
			
			if(fpacket.getPreviousTimestamp().get(0) <= management.getStateTimestamp().get(0) 
					&& fpacket.getPreviousTimestamp().get(1) <= management.getStateTimestamp().get(1) 
					&& fpacket.getPreviousTimestamp().get(2) <= management.getStateTimestamp().get(2))
			{	
					//int temp = management.getStateTimestamp().get(management.rmid);
					//management.getStateTimestamp().set(management.rmid, temp++);
					
					versions.put(fpacket.getFilename(), 0);
					Iterator it = availableServers.keySet().iterator();
					System.err.println("About to iterate");
					while(it.hasNext()){
						synchronized(management.lock){
							System.err.println("iterating");
							Integer i = (Integer) it.next();
							String[] s = availableServers.get(i);
							server = new Socket(InetAddress.getByName(s[0]), i+2000);
							
							OutputStream sos = server.getOutputStream();
							InputStream sis = server.getInputStream();
							ObjectOutputStream serverOOP = new ObjectOutputStream(sos);
							ObjectInputStream serverOIS = new ObjectInputStream(sis);
							
							serverOOP.writeObject(fpacket);
							fpacket = (FilePacket) serverOIS.readObject();
							if(fpacket.getOperation()==0){
								break;
							}
						}
					}
					if(fpacket.success()){
						
						//management.committed.put(fpacket.getUuid(), );
					}
					//fpacket = (FilePacket)ois.readObject();
					//Vector<Integer> vector = management.get
					fpacket.setTimestamp((Vector<Integer>)management.getStateTimestamp().clone());
					oos.writeObject(fpacket);
				
			}else{
				System.out.println("HERE");
				
				//UpdateRequestThread req = new UpdateRequestThread(management);
				//Thread request = new Thread(req);
				//request.start();
				//request.join();
				
				//management.holdback.add(fpacket);
			}
			//fpacket.success(true);
			//oos.writeObject(fpacket);
			oos.flush();
			//oos.close();
			//ois.close();
		}catch(IOException | ClassNotFoundException e){
			e.printStackTrace();
		}
	}
}
