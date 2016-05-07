import java.io.*;
import java.net.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class RequestThreadManager implements Runnable {

	private MulticastUtilities mUtil;
	private ExecutorService executor;
	private ConcurrentSkipListMap<Integer, String[]> servers;
	
	public RequestThreadManager(MulticastUtilities u, ConcurrentSkipListMap<Integer, String[]> c){
		mUtil = u;
		//executor = Executors.newFixedThreadPool(5);
		servers = c;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		try{
			while(true){
				DatagramPacket p = mUtil.listen();
				System.out.println("RequestThreadManager: "+(new String(p.getData(), 0, p.getLength())));
				Thread t = new Thread(new RequestThread(servers,p));
				t.start();
			}
		}catch(IOException e){
			e.printStackTrace();
		}
	}

}
