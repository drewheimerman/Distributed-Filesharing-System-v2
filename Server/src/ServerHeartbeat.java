import java.io.IOException;
import java.net.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ServerHeartbeat implements Runnable {

	private MulticastUtilities heartbeatMulticast;
	private UDPUtilities udpUtils;
	private int sid;
	
	public ServerHeartbeat(MulticastUtilities m, int sid){
		heartbeatMulticast = m;
		udpUtils = new UDPUtilities();
		this.sid = sid;
	}
	
	
	@Override
	public void run() {
		while(true){
			DatagramPacket pingPacket = null;
			try {
				//Wait for Multicast ping from the RemoteManager
				pingPacket = heartbeatMulticast.listen();
			} catch (IOException e) {
				e.printStackTrace();
			}
			//Convert the packet data to the integer of the UDP heartbeat request port to send heartbeats
			String received = new String(pingPacket.getData(), 0, pingPacket.getLength());
			if(pingPacket!=null){
				//Set the destination to the RemoteManater
				udpUtils.setDestination(pingPacket.getAddress());
				udpUtils.setDestPort(Integer.parseInt(received));
				
				try {
					//Send the server ID (sid) to the RM which will be used as a key in the server list
					udpUtils.sendString(sid+"");
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				try {
					//Block listen for RM response, contains the dedicated port through which the RM will receive server(#id)'s heartbeats
					DatagramPacket p = udpUtils.listen();
					received = new String(p.getData(),0,p.getLength());
					udpUtils.setDestPort(Integer.parseInt(received));
					while(true){
						//Send heartbeat and wait 5s
						udpUtils.sendString("ping");
						Thread.sleep(5000);
					}
				} catch (IOException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}				
			}
					
		}

	}

}
