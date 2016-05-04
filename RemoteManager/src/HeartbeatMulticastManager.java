import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.concurrent.ConcurrentSkipListMap;

public class HeartbeatMulticastManager implements Runnable {

	private MulticastUtilities heartbeatMulticast;
	private ConcurrentSkipListMap<InetAddress, Integer> servers;
	
	public HeartbeatMulticastManager(MulticastUtilities m, ConcurrentSkipListMap<InetAddress, Integer> c){
		heartbeatMulticast = m;
		servers = c;
	}
	
	@Override
	public void run() {
		Thread pulse = new Thread(new HeartbeatPulse(heartbeatMulticast));
		pulse.start();
		while(true){
			
			DatagramPacket packet = null;
			try {
				packet = heartbeatMulticast.listen();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			if(packet!=null && !(new String(packet.getData(), 0, packet.getLength()).equals("ping"))){
				ByteBuffer bbuff = ByteBuffer.wrap(packet.getData());
				int port = bbuff.getInt();
				if(!servers.containsKey(packet.getAddress())){
					servers.put(packet.getAddress(), port);
					for(InetAddress k : servers.keySet()){
						System.out.print("Server: "+k.getHostAddress()+" ");
					}
					System.out.println("\n");
				}
			}
			
		}

	}

}
