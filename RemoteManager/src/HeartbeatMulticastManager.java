import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HeartbeatMulticastManager implements Runnable {

	private MulticastUtilities heartbeatMulticast;
	private ConcurrentSkipListMap<Integer, String[]> servers;
	int serverCount = 0;
	
	public HeartbeatMulticastManager(MulticastUtilities m, ConcurrentSkipListMap<Integer, String[]> c){
		heartbeatMulticast = m;
		servers = c;
	}
	
	@Override
	public void run() {
		
		UDPUtilities udpUtils = new UDPUtilities();
		System.out.println(udpUtils.getSelfPort());
		Thread pulse = new Thread(new HeartbeatPulse(heartbeatMulticast, udpUtils.getSelfPort()));
		pulse.start();
		
		while(true){
			
			DatagramPacket packet = null;
			try {
				packet = udpUtils.listen();
			} catch (IOException e) {
				e.printStackTrace();
			}
			String received = new String(packet.getData(), 0, packet.getLength());
			System.out.println(received);
			if(packet!=null){
				
				int sid = Integer.parseInt(received);
				String[] temp = {packet.getAddress().getHostAddress(), ""+packet.getPort()};
				
				if(!servers.containsKey(sid)){
					servers.put(sid, temp);
					
					UDPUtilities u = new UDPUtilities();
					u.setDestination(packet.getAddress());
					u.setDestPort(packet.getPort());
					
					Thread thd = new Thread(new HeartbeatMonitor(u, servers, sid));
					thd.start();
					for(Integer k : servers.keySet()){
						System.out.print("Server: "+k.intValue()+" ");
					}
					System.out.println("\n");
				}
				
			}
			
		}

	}

}
