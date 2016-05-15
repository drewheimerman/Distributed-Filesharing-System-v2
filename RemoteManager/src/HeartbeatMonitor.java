import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;

public class HeartbeatMonitor implements Runnable {


	private UDPUtilities udpUtil;
	private int sid;
	private int serverPort;
	
	private ConcurrentSkipListMap<Integer, String[]> availableServers;
	
	public HeartbeatMonitor(UDPUtilities u, ConcurrentSkipListMap<Integer, String[]> c, int sid){
		//mUtil = m;
		//id = i;
		udpUtil = u;
		availableServers = c;
		this.sid = sid;
	}
	
	@Override
	public void run() {
		try {
			//Send the server the port to which to send the heartbeat and set timeout to 15s
			udpUtil.sendString(""+udpUtil.getSelfPort());
			System.out.println("Started heartbeat monitor.");
			udpUtil.getIncomingSocket().setSoTimeout(15000);
			while(true){
				try{
					//Block listen until heartbeat or timeout.
					DatagramPacket received = udpUtil.listen();
					String message = new String(received.getData(), 0, received.getLength());
					
				}catch(SocketTimeoutException e){
					
					//Remove server from active server list if it does not respond within 15s
					System.out.println("lost connection.");
					availableServers.remove(sid);
					System.out.println(availableServers.size()+" servers alive.");
					for(int k : availableServers.keySet()){
						System.out.print("Server: "+k+" ");
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
