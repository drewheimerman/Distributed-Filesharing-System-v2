import java.net.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.*;

public class FrontendThread implements Runnable {

	private Socket client;
	private Socket remoteManager;
	private Vector timestamp;
	private MulticastUtilities requestMulticast;
	
	public FrontendThread(MulticastUtilities m, Socket c, Vector t){
		client = c;
		timestamp = t;
		requestMulticast = m;
	}
	
	
	@Override
	public void run() {
		try {
			
			OutputStream os = client.getOutputStream();
			InputStream is = client.getInputStream();
			
			ObjectInputStream ois = new ObjectInputStream(is);
			FilePacket fpacket = (FilePacket)ois.readObject();
			fpacket.setUuid(new UUID(10000L, 1000L));
			
			requestMulticast.sendToSocket("request");
			DatagramPacket response = requestMulticast.listen();
			String strport = new String(response.getData(), 0, response.getLength());
			
			Pattern pattern = Pattern.compile("\\d+");
			Matcher matcher = null;
			while(matcher == null || !matcher.matches()){
				response = requestMulticast.listen();
				strport = new String(response.getData(), 0, response.getLength());
				matcher = pattern.matcher(strport);
			}
			int port = Integer.parseInt(strport);
			
			remoteManager = new Socket(response.getAddress(), port);
			
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
