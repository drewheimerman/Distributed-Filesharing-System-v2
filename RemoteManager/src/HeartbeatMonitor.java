import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;

public class HeartbeatMonitor implements Runnable {

	private MulticastUtilities mUtil;
	//private UDPUtilities udpUtil;
	private int sid;
	private int serverPort;
	private boolean isMiddleware = false;
	private ConcurrentSkipListMap<Integer, String[]> availableServers;
	
	public HeartbeatMonitor(DatagramPacket p, MulticastUtilities u, ConcurrentSkipListMap<Integer, String[]> c){
		mUtil = u;
		availableServers = c;
	}
	
	@Override
	public void run() {
		try {
			
			
			while(true){
				try{
					DatagramPacket received = mUtil.listen();
					String message = new String(received.getData(), 0, received.getLength());
					
				}catch(SocketTimeoutException e){
					System.out.println("lost connection.");
					availableServers.remove(sid);
					for(int k : availableServers.keySet()){
						System.out.print(k+" ");
					}
					break;
				}
			}
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch(IOException e){
			
		}
		
	}
	public void setMiddleware(boolean b){
		isMiddleware = b;
	}

	public int getId() {
		return sid;
	}

	public void setId(int id) {
		this.sid = id;
	}

	public int getServerPort() {
		return serverPort;
	}

	public void setServerPort(int serverPort) {
		this.serverPort = serverPort;
	}
	
}
