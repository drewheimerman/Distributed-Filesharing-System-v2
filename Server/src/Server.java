import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.Properties;

public class Server {
	
	public static void main(String[] args) {
		//Read in the Properties from config.properties
		int sid = 0;
		InetAddress rmip = null;
		try{
			sid = Integer.parseInt(args[0]);
			rmip = InetAddress.getByName(args[1]);
		}catch(IOException e){
			e.printStackTrace();
		}
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
		
		//Create the MulticastUtilities for the server
		MulticastUtilities heartbeatMulticast = null;
		MulticastUtilities serverMulticast = null;
		
		try {
			heartbeatMulticast = new MulticastUtilities(
					InetAddress.getByName(appProps.getProperty("serverMulticastIP"+sid%3)),
					Integer.parseInt(appProps.getProperty("heartbeatMulticastPort")));
			
			serverMulticast = new MulticastUtilities(
					InetAddress.getByName(appProps.getProperty("serverMulticastIP"+sid%3)), 
					Integer.parseInt(appProps.getProperty("serverMulticastPort"+sid%3)));
			
		} catch (NumberFormatException | UnknownHostException e) {
			e.printStackTrace();
			System.exit(2);
		}
		//Start ServerHeartbeat thread
		ServerHeartbeat heartbeat = new ServerHeartbeat(heartbeatMulticast, sid, rmip);
		Thread serverHeartbeat = new Thread(heartbeat);
		serverHeartbeat.start();
		
		ServerSocket socket;
		try {
			socket = new ServerSocket(sid+2000);
		
			while(true){
				try {
					System.out.println(sid+2000);
					Socket rm = socket.accept();
					System.err.println("Accepted ServerSocket");
					ServerUpdater updater = new ServerUpdater(rm, sid);
					Thread u = new Thread(updater);
					u.start();
				} catch (IOException e) {
					
					e.printStackTrace();
				}
			}
		}catch(Exception e){
			
		}
	}

}
