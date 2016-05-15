import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.Vector;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentSkipListMap;

public class RemoteManager {
	
	/**
	 * @param args : ID of the RemoteManager
	 */
	
	public static void main(String[] args) {
		
		Management management = new RemoteManager().new Management();
		management.stateTimestamp.add(0, new Integer(0));
		management.stateTimestamp.add(1, new Integer(0));
		management.stateTimestamp.add(2, new Integer(0));
		/*int state;
		Vector<Integer> stateTimestamp = new Vector<Integer>(3);
		
		ConcurrentLinkedDeque updateLog = new ConcurrentLinkedDeque();
		ConcurrentLinkedDeque committed = new ConcurrentLinkedDeque();
		ConcurrentLinkedDeque timestampTable = new ConcurrentLinkedDeque();*/
		ConcurrentSkipListMap<Integer, String[]> servers = new ConcurrentSkipListMap<Integer, String[]>();
		
		//Read in the Properties from config.properties
		
		Properties properties = new Properties();
		Properties appProps = new Properties(properties);
		try{
			InputStream in = new FileInputStream("./src/resources/config.properties");
			appProps.load(in);
			in.close();
		}catch(IOException e){
			e.printStackTrace();
			System.exit(1);
		}
		
		//Create the MulticastUtilities for the RM, join the RemoteManager Multicast Group
		MulticastUtilities rmMulticast = null;
		MulticastUtilities heartbeatMulticast = null;
		MulticastUtilities serverMulticast = null;
		MulticastUtilities rmRequestMulticast = null;
		
		
		UDPUtilities udpUtilGen = new UDPUtilities(7778);
		
		try {
			rmMulticast = new MulticastUtilities(
					InetAddress.getByName(appProps.getProperty("rmMulticastIP")), 
					Integer.parseInt(appProps.getProperty("rmMulticastPort")));
			rmRequestMulticast = new MulticastUtilities(
					InetAddress.getByName(appProps.getProperty("rmRequestMulticastIP")),
					Integer.parseInt(appProps.getProperty("rmRequestMulticastPort")));
			
			int t = Integer.parseInt(args[0]);
			heartbeatMulticast = new MulticastUtilities(
					InetAddress.getByName(appProps.getProperty("serverMulticastIP"+t)),
					Integer.parseInt(appProps.getProperty("heartbeatMulticastPort")));
			
			serverMulticast = new MulticastUtilities(
					InetAddress.getByName(appProps.getProperty("serverMulticastIP"+t)), 
					Integer.parseInt(appProps.getProperty("serverMulticastPort"+t)));
			
		} catch (NumberFormatException | UnknownHostException e) {
			e.printStackTrace();
			System.exit(2);
		}
		//Create and start a HeartbeatMulticastManager thread to listen for new/existing servers
		HeartbeatMulticastManager hbManager = new HeartbeatMulticastManager(heartbeatMulticast, servers);
		Thread hbm = new Thread(hbManager);
		hbm.start();
		System.out.println("request thread manager");
		RequestThreadManager requestManager = new RequestThreadManager(rmRequestMulticast, servers, management);
		Thread reqManagerThread = new Thread(requestManager);
		reqManagerThread.start();
		
		
	}
	public class Management{
		/*State Manager*/
		public int state;
		public Vector<Integer> stateTimestamp = new Vector<Integer>(3);
		/*Replica Manager*/
		public Vector<Integer> replicaTimestamp = new Vector<Integer>(3);
		public ConcurrentLinkedDeque updateLog = new ConcurrentLinkedDeque();
		/*Queries*/
		public ConcurrentLinkedQueue holdback = new ConcurrentLinkedQueue();
		/*Extras*/
		public ConcurrentLinkedDeque committed = new ConcurrentLinkedDeque();
		public ConcurrentLinkedDeque timestampTable = new ConcurrentLinkedDeque();
		
		public synchronized byte[] readFile(FilePacket fpacket){
			byte[] buffer = null;
			try {
				buffer = Files.readAllBytes((Path)Paths.get(fpacket.getFilename()));
				fpacket.setBuffer(buffer);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			return buffer;
		}
		public synchronized int writeFile(FilePacket fpacket){
			
			
			return 1;
		}
		
		
		/*Getters*/
		public int getState() {
			return state;
		}
		public Vector<Integer> getStateTimestamp() {
			return stateTimestamp;
		}
		public Vector<Integer> getReplicaTimestamp() {
			return replicaTimestamp;
		}
		public ConcurrentLinkedDeque getUpdateLog() {
			return updateLog;
		}
		public ConcurrentLinkedQueue getHoldback() {
			return holdback;
		}
		public ConcurrentLinkedDeque getCommitted() {
			return committed;
		}
		public ConcurrentLinkedDeque getTimestampTable() {
			return timestampTable;
		}
	}
}
