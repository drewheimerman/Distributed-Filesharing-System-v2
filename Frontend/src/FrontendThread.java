import java.net.*;
import java.util.*;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.*;

public class FrontendThread implements Runnable {

	private Socket client;
	private Socket remoteManager;
	private Vector timestamp;
	private ConcurrentSkipListMap<Integer, String[]> rms;
	
	public FrontendThread(ConcurrentSkipListMap<Integer, String[]> r, Socket c, Vector t){
		client = c;
		timestamp = t;
		rms = r;
	}
	
	
	@Override
	public void run() {
		try {
			
			OutputStream os = client.getOutputStream();
			InputStream is = client.getInputStream();
			
			ObjectInputStream ois = new ObjectInputStream(is);
			FilePacket fpacket = (FilePacket)ois.readObject();
			fpacket.setUuid(new UUID(10000L, 1000L));
			fpacket.setTimestamp(timestamp);
			
			/*requestMulticast.sendToSocket("request");
			DatagramPacket response = requestMulticast.listen();
			String strport = new String(response.getData(), 0, response.getLength());*/
			
			/*Pattern pattern = Pattern.compile("\\d+");
			Matcher matcher = null;
			while(matcher == null || !matcher.matches()){
				response = requestMulticast.listen();
				strport = new String(response.getData(), 0, response.getLength());
				matcher = pattern.matcher(strport);
			}*/
			//int port = Integer.parseInt(strport); //parse RM TCP port
			
			//Connection with the RemoteManager
			
			FilePacket resultPacket = null;
			Iterator it = rms.keySet().iterator();
			while(it.hasNext()){	
				Integer i = (Integer) it.next();
				String[] s = rms.get(i);
				try{
					remoteManager = new Socket(InetAddress.getByName(s[0]), i+3000);
					
					OutputStream rmOS = remoteManager.getOutputStream();
					InputStream rmIS = remoteManager.getInputStream();
					ObjectOutputStream rmOOS = new ObjectOutputStream(rmOS);
					ObjectInputStream rmOIS = new ObjectInputStream(rmIS);
					
					rmOOS.writeObject(fpacket);
					resultPacket = (FilePacket)rmOIS.readObject();
					if(fpacket.getOperation()==0){
						break;
					}
				}catch(Exception e){
					
				}
			}
			//END connection with RemoteManager
			
			//Send result back to Client
			ObjectOutputStream oos = new ObjectOutputStream(os);
			
			oos.writeObject(resultPacket);
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
