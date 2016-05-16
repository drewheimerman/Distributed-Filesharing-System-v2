import java.io.*;
import java.net.*;
import java.util.Iterator;
import java.util.concurrent.ConcurrentSkipListMap;

public class UpdateRequestThread implements Runnable {

	
	//private MulticastUtilities requestMulticast;
	private RemoteManager.Management management;
	private ConcurrentSkipListMap<Integer, String[]> servers;
	
	public UpdateRequestThread(RemoteManager.Management me, ConcurrentSkipListMap<Integer, String[]> s){
		management = me;
		servers = s;
	}
	
	@Override
	public void run() {
		try{
			ServerSocket servSocket = new ServerSocket(6161+management.rmid);
			management.rmRequestMulticast.sendToSocket(Integer.toString(management.rmid));
			Socket socket = servSocket.accept();
			InputStream is = socket.getInputStream();
			ObjectInputStream ois = new ObjectInputStream(is);
			
			while(true){
				FilePacket fpacket = (FilePacket)ois.readObject();
				Iterator it = servers.keySet().iterator();
				while(it.hasNext()){
					synchronized(management.lock){
						System.err.println("iterating");
						Integer i = (Integer) it.next();
						String[] s = servers.get(i);
						Socket sock = new Socket(InetAddress.getByName(s[0]), i+2000);
						
						OutputStream sos = sock.getOutputStream();
						InputStream sis = sock.getInputStream();
						ObjectOutputStream serverOOP = new ObjectOutputStream(sos);
						ObjectInputStream serverOIS = new ObjectInputStream(sis);
						
						serverOOP.writeObject(fpacket);
						fpacket = (FilePacket) serverOIS.readObject();
						
					}
				}
			}
			
		}catch(IOException | ClassNotFoundException e){
			System.out.println("Update complete.");
		}
	}

}
