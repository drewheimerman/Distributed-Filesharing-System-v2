import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.util.Properties;

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
		
		try{
			requestMulticast = new MulticastUtilities(
					InetAddress.getByName(appProps.getProperty("rmRequestMulticastIP")),
					Integer.parseInt(appProps.getProperty("rmRequestMulticastPort")));
		}catch(IOException e){
			e.printStackTrace();
		}
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
		
		ClientThread clientThread = new ClientThread(requestMulticast, action, filename);
		Thread t = new Thread(clientThread);
		t.start();
		try {
			t.join();
		} catch (InterruptedException e) {
			
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
