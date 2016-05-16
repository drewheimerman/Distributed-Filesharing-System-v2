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
			System.out.println("Updater socket accepted");
			InputStream is = socket.getInputStream();
			ObjectInputStream ois = new ObjectInputStream(is);
			System.out.println("objectoutputstream");
			while(true){
				Iterator it = servers.keySet().iterator();
				System.out.println("before loop");
				FilePacket fpacket = (FilePacket)ois.readObject();
				System.out.println(fpacket.getFilename());
				while(it.hasNext() && fpacket != null){
					System.out.println("before synch");
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
						System.err.println("Sent");
						fpacket = (FilePacket) serverOIS.readObject();
						serverOOP.flush();
					}
				}
			}
			
		}catch(IOException | ClassNotFoundException e){
			//e.printStackTrace();
			System.out.println("Update complete.");
		}
	}

}
