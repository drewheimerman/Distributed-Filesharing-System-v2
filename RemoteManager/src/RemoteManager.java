import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Properties;
import java.util.concurrent.ConcurrentSkipListMap;

public class RemoteManager {
	
	/**
	 * @param args : ID of the RemoteManager
	 */
	public static void main(String[] args) {
		
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
		
		RequestThreadManager requestManager = new RequestThreadManager(rmRequestMulticast, servers);
		Thread reqManagerThread = new Thread(requestManager);
		reqManagerThread.start();
		
		
	}

}
