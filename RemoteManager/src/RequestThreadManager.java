import java.io.*;
import java.net.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RequestThreadManager implements Runnable {

	private MulticastUtilities mUtil;
	private ExecutorService executor;
	private ConcurrentSkipListMap<Integer, String[]> servers;
	private ServerSocket serverSocket;
	
	public RequestThreadManager(MulticastUtilities u, ConcurrentSkipListMap<Integer, String[]> c){
		mUtil = u;
		//executor = Executors.newFixedThreadPool(5);
		servers = c;
	}
	
	@Override
	public void run() {
		try {
			
			while(true){
				serverSocket = new ServerSocket();
				serverSocket.setSoTimeout(2000);
				serverSocket.bind(null);
				Pattern pattern = Pattern.compile("\\d+");
				Matcher matcher = null;
				String query = "";
				DatagramPacket p = null;
				
				while(matcher == null || matcher.matches()){
					p = mUtil.listen();
					query = new String(p.getData(), 0, p.getLength());
					
					matcher = pattern.matcher(query);
				}
				System.err.println(query);
				mUtil.sendToSocket(serverSocket.getLocalPort()+"");
				System.out.println(serverSocket.getLocalPort());
				try{
					Socket client = serverSocket.accept();
					serverSocket.close();
					System.out.println("RequestThreadManager: "+(new String(p.getData(), 0, p.getLength())));
					Thread t = new Thread(new RequestThread(client,servers,p));
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
