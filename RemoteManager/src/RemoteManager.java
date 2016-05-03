import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Properties;

public class RemoteManager {
	
	
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
		try {
			MulticastUtilities mUtilities = new MulticastUtilities(InetAddress.getByName(appProps.getProperty("rmMulticastIP")), Integer.parseInt(appProps.getProperty("rmMulticastPort")));
		} catch (NumberFormatException | UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(2);
		}
		
		
	}

}
