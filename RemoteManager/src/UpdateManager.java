import java.io.IOException;
import java.net.DatagramPacket;
import java.net.ServerSocket;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ExecutorService;

import com.sun.security.ntlm.Server;

public class UpdateManager implements Runnable {

	private MulticastUtilities mUtil;
	private MulticastUtilities serverUpdates;
	private ExecutorService executor;
	private ConcurrentSkipListMap<Integer, String[]> servers;
	private ServerSocket serverSocket;
	private RemoteManager.Management management;
	private ConcurrentHashMap<String, Integer> versions;
	
	public UpdateManager(RemoteManager.Management m, ConcurrentSkipListMap<Integer, String[]> s, ConcurrentHashMap<String, Integer> v){
		management = m;
		servers = s;
		versions = v;
	}
	
	@Override
	public void run() {
		while(servers.size()<3){
			
		}
		try {
			management.rmMulticast.sendToSocket("request");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		UpdateRequestThread urt = new UpdateRequestThread(management, servers);
		Thread u = new Thread(urt);
		u.start();
		Updater updater = new Updater(management, servers, versions);
		Thread up = new Thread(updater);
		up.start();
	}

}
