import java.net.*;
import java.util.UUID;
import java.util.Vector;
import java.io.*;

public class FrontendThread implements Runnable {

	private Socket client;
	private Vector timestamp;
	
	public FrontendThread(Socket c, Vector t){
		client = c;
		timestamp = t;
	}
	
	
	@Override
	public void run() {
		try {
			
			OutputStream os = client.getOutputStream();
			InputStream is = client.getInputStream();
			
			ObjectInputStream ois = new ObjectInputStream(is);
			FilePacket fpacket = (FilePacket)ois.readObject();
			
			
			ObjectOutputStream oos = new ObjectOutputStream(os);
			fpacket.success(true);
			oos.writeObject(fpacket);
			oos.flush();
			ois.close();
			oos.close();
			
		}catch(IOException e){
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

}
