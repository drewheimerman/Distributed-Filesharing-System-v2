import java.io.*;
import java.net.*;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RequestThreadManager implements Runnable {

	private MulticastUtilities mUtil;
	private MulticastUtilities serverRequest;
	private ExecutorService executor;
	private ConcurrentSkipListMap<Integer, String[]> servers;
	private ServerSocket serverSocket;
	private RemoteManager.Management management;
	private ConcurrentHashMap<String, Integer> versions;
	
	
	public RequestThreadManager(ConcurrentSkipListMap<Integer,String[]> c, RemoteManager.Management m, ConcurrentHashMap<String, Integer> v){
		//mUtil = u;
		servers = c;
		management = m;
		//serverRequest = s;
		versions = v;
	}
	
	@Override
	public void run() {
		try {
			UpdateManager backgroundUpdateProcess = new UpdateManager(management, servers, versions);
			Thread bup = new Thread(backgroundUpdateProcess);
			bup.start();
			while(true){
				System.err.println("Socket setup");
				serverSocket = new ServerSocket(management.rmid+3000);
				//serverSocket.setSoTimeout(2000);
				//serverSocket.bind(null);
				/*Pattern pattern = Pattern.compile("\\d+");
				Matcher matcher = null;
				String query = "";
				DatagramPacket p = null;
				
				System.err.println("Listen loop");
				while(matcher == null || matcher.matches()){
					p = mUtil.listen();
					query = new String(p.getData(), 0, p.getLength());
					
					matcher = pattern.matcher(query);
				}*/
				System.err.println("Post listen loop");
				//System.err.println(query);
				//mUtil.sendToSocket(serverSocket.getLocalPort()+"");
				//System.out.println(serverSocket.getLocalPort());
				try{
					System.err.println("waiting");
					Socket client = serverSocket.accept();
					serverSocket.close();
					//System.out.println("RequestThreadManager: "+(new String(p.getData(), 0, p.getLength())));
					Thread t = new Thread(new RequestThread(client,servers, management, versions));
					t.start();
				}catch(SocketTimeoutException e){
					//System.out.println("beaten");
				}
			}
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
}


