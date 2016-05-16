import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Properties;
import java.util.Vector;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Frontend {
	
	public static void main(String[] args) throws IOException {
		
		Vector<Integer> timestamp = new Vector<Integer>(3);
		timestamp.add(0);
		timestamp.add(0);
		timestamp.add(0);
		MulticastUtilities heartbeatMulticast = null;
		
		ConcurrentSkipListMap<Integer, String[]> rms = new ConcurrentSkipListMap<Integer, String[]>();
		String[] zero = {args[0],Integer.toString(3000)};
		String[] one = {args[0],Integer.toString(3001)};
		String[] two = {args[0],Integer.toString(3002)};
		rms.put(0, zero);
		rms.put(1, one);
		rms.put(2, two);
		
		Properties properties = new Properties();
		Properties appProps = new Properties(properties);
		try{
			InputStream in = new FileInputStream("./src/resources/config.properties");
			appProps.load(in);
			in.close();
			
			heartbeatMulticast = new MulticastUtilities(
					InetAddress.getByName(appProps.getProperty("rmRequestMulticastIP")),
					Integer.parseInt(appProps.getProperty("rmRequestMulticastPort")));
			
		}catch(IOException e){
			e.printStackTrace();
			System.exit(1);
		}
		//HeartbeatMulticastManager hbManager = new HeartbeatMulticastManager(heartbeatMulticast, rms);
		//Thread hbm = new Thread(hbManager);
		//hbm.start();
		
		ServerSocket feSocket = new ServerSocket(2121);
		//ServerSocket feReturnSocket = new ServerSocket(2122);
		while(true){
			try{
				Socket client = feSocket.accept();
				System.err.println("Accepted");
				Thread t = new Thread(new FrontendThread(rms,client, timestamp));
				t.start();
				t.join();
			}catch(IOException | InterruptedException e){
				e.printStackTrace();
				feSocket.close();
				break;
			}
			
		}
	}

}
