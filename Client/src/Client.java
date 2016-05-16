import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

public class Client {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
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
		
		MulticastUtilities requestMulticast = null;
		InetAddress feAddress = null;
		
		//Set the address for the Frontend provided in the args (args[0])
		try {
			feAddress = InetAddress.getByName(args[0]);
		} catch (UnknownHostException e1) {
			
			e1.printStackTrace();
		}
		
		/*try{
			requestMulticast = new MulticastUtilities(
					InetAddress.getByName(appProps.getProperty("rmRequestMulticastIP")),
					Integer.parseInt(appProps.getProperty("rmRequestMulticastPort")));
		}catch(IOException e){
			e.printStackTrace();
		}*/
		String action = "";
		String filename = "";
		do{
			System.out.println("Upload or Download file?");
			try{
				action = readKeyboardInput();
				action = action.replaceAll("[^A-Za-z0-9]", "");
				action = action.toLowerCase();
			}catch(IOException e){
				e.printStackTrace();
				System.exit(4);
			}
			
		}while(!action.equals("upload") && !action.equals("download"));
		do{
			System.out.print("Filename (with extention):");
			try{
				filename = readKeyboardInput();
				filename = filename.replaceAll("[^A-Za-z0-9.]", "");
				filename = filename.toLowerCase();
			}catch(IOException e){
				e.printStackTrace();
				System.exit(4);
			}
			
		}while(filename.isEmpty());
		
		/*----Start client thread which then makes connection to Broker/RM----*/
		
		try {
			FilePacket fpacket = new FilePacket();
			fpacket.setFilename(filename);
			if(action.equals("upload")){
				fpacket.setOperation(1);
			}else{
				fpacket.setOperation(0);
			}
			System.out.println(fpacket.getFilename()+"\noperation :"+fpacket.getOperation());
			ClientThread clientThread = new ClientThread(feAddress, fpacket);
			Thread t = new Thread(clientThread);
			t.start();
			t.join();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}
	public static String readKeyboardInput() throws IOException {
		BufferedReader buffreader = new BufferedReader(new InputStreamReader(System.in));
		return buffreader.readLine();
	}
	public static String getExtension(String filename){
		
		String ext ="";
		
		return ext;
	}

}
