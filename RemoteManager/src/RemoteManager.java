import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Properties;

public class RemoteManager {
	
	/**
	 * @param args : ID of the RemoteManager
	 */
	public static void main(String[] args) {
		
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
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(2);
		}
		try{
			heartbeatMulticast.sendToSocket("existing server check");
		}catch(Exception e){
			e.printStackTrace();
		}
		
		
	}

}
