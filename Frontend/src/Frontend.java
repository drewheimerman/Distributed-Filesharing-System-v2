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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Frontend {
	
	public static void main(String[] args) throws IOException {
		
		Vector timestamp = new Vector();
		MulticastUtilities requestMulticast = null;
		
		Properties properties = new Properties();
		Properties appProps = new Properties(properties);
		try{
			InputStream in = new FileInputStream("./src/resources/config.properties");
			appProps.load(in);
			in.close();
			
			requestMulticast = new MulticastUtilities(
					InetAddress.getByName(appProps.getProperty("rmRequestMulticastIP")),
					Integer.parseInt(appProps.getProperty("rmRequestMulticastPort")));
			
		}catch(IOException e){
			e.printStackTrace();
			System.exit(1);
		}
		ServerSocket feSocket = new ServerSocket(2121);
		//ServerSocket feReturnSocket = new ServerSocket(2122);
		while(true){
			try{
				Socket client = feSocket.accept();
				Thread t = new Thread(new FrontendThread(requestMulticast,client, timestamp));
				t.start();
			}catch(IOException e){
				e.printStackTrace();
				feSocket.close();
				break;
			}
			
		}
	}

}
