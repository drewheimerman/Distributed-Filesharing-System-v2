import java.io.IOException;
import java.net.DatagramPacket;
import java.util.concurrent.ConcurrentSkipListMap;

public class HeartbeatMulticastManager implements Runnable {

	private MulticastUtilities heartbeatMulticast;
	private ConcurrentSkipListMap<Integer, String[]> servers;
	
	public HeartbeatMulticastManager(MulticastUtilities m, ConcurrentSkipListMap<Integer, String[]> c){
		heartbeatMulticast = m;
		servers = c;
	}
	
	@Override
	public void run() {
		while(true){
			DatagramPacket packet = null;
			try {
				packet = heartbeatMulticast.listen();
			} catch (IOException e) {
				e.printStackTrace();
			}
			if(packet!=null){
				Thread thd = new Thread(new HeartbeatMonitor(packet,heartbeatMulticast, servers));
			}
			
		}

	}

}
