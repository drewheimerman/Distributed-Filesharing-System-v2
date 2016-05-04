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
		
		ConcurrentSkipListMap<InetAddress, Integer> servers = new ConcurrentSkipListMap<InetAddress, Integer>();
		
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
		
		try {
			rmMulticast = new MulticastUtilities(
					InetAddress.getByName(appProps.getProperty("rmMulticastIP")), 
					Integer.parseInt(appProps.getProperty("rmMulticastPort")));
			
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
		
	}

}
