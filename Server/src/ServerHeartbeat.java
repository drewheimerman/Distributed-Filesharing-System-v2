import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ServerHeartbeat implements Runnable {

	private MulticastUtilities heartbeatMulticast;
	private UDPUtilities udpUtils;
	private int sid;
	private InetAddress rmip;
	
	public ServerHeartbeat(MulticastUtilities m, int sid, InetAddress r){
		heartbeatMulticast = m;
		udpUtils = new UDPUtilities();
		try {
			udpUtils.getIncomingSocket().setSoTimeout(2000);
		} catch (SocketException e) {
			e.printStackTrace();
		}
		this.sid = sid;
		this.rmip = r;
	}
	
	public class Ping implements Runnable {
		UDPUtilities utils;
		int sid;
		public Ping(UDPUtilities u, int sid){
			utils = u;
			this.sid = sid;
		}
		public void run() {
			while(true){
				try {
					utils.sendString(sid+"");
					utils.listen();
					//Thread.sleep(500);
				} catch (IOException e) {
					break;
				}
			}
		}
	}

	@Override
	public void run() {
		while(true){
			/*try{
				DatagramPacket pingPacket = null;
				System.err.println("Listening");
				pingPacket = heartbeatMulticast.listen();
				udpUtils.setDestination(pingPacket.getAddress());
				String s = new String(pingPacket.getData());
				//String s = new String(pingPacket.getData(), 0, pingPacket.getData().length);
				System.out.println(Arrays.toString(pingPacket.getData()));
				udpUtils.setDestPort(Integer.parseInt(s));
				udpUtils.getIncomingSocket().setSoTimeout(4000);
				Thread t = new Thread(new Ping(udpUtils));
				t.start();
				t.join();
			}catch(IOException | InterruptedException e){
				
			}*/
			DatagramPacket pingPacket = null;
			/*try {
				//Wait for Multicast ping from the RemoteManager
				System.err.println("Multicast Listen");
				System.out.println(heartbeatMulticast.getsocket().getReceiveBufferSize());
				pingPacket = heartbeatMulticast.listen();
			} catch (IOException e) {
				e.printStackTrace();
			}*/
			//Convert the packet data to the integer of the UDP heartbeat request port to send heartbeats
			//String received = new String(pingPacket.getData(), 0, pingPacket.getLength());
			//if(received!=null){
				//Set the destination to the RemoteManater
				try{
					System.err.println(1);
					udpUtils.setDestination(rmip);
					udpUtils.setDestPort((sid%3)+8000);
					System.err.println("IP: "+udpUtils.getDestination()+"Port: "+udpUtils.getDestPort());
					udpUtils.sendString(""+sid);
					System.err.println(2);
					DatagramPacket packet = udpUtils.listen();
					System.err.println(3);
					//received = new String(packet.getData(), 0, packet.getData().length);
					ByteBuffer bbuff = ByteBuffer.wrap(packet.getData());
					int port = bbuff.getInt();
					udpUtils.setDestPort(port);
					System.err.println(4);
					Ping ping = new Ping(udpUtils, sid);
					Thread p = new Thread(ping);
					System.err.println(5);
					p.start();
					System.err.println(6);
					p.join();
					System.err.println(7);
					System.err.println("Thread crashed");
				}catch(IOException e){
					
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			//}	
		}

	}

}
