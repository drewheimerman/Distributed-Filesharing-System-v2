import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;

public class Updater implements Runnable {

	private RemoteManager.Management management;
	private ConcurrentSkipListMap<Integer, String[]> servers;
	private ConcurrentHashMap<String, Integer> versions;
	
	public Updater(RemoteManager.Management m, ConcurrentSkipListMap<Integer, String[]> s, ConcurrentHashMap<String, Integer> v){
		management = m;
		servers = s;	
		versions = v;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		while(true){
			try {
				DatagramPacket packet = management.rmRequestMulticast.listen();
				String str = new String(packet.getData(), 0, packet.getLength());
				int id = Integer.parseInt(str);
				if(id!=management.rmid){
					Socket sock = new Socket(packet.getAddress(), 6161+id);
					OutputStream os = sock.getOutputStream();
					ObjectOutputStream oos = new ObjectOutputStream(os);
					/*synchronized(management.lock){
						//File path = new File("./");
						//File[] list = path.listFiles(); 
						
						for(File f: path.listFiles()){
							byte[] buffer = null;
							try {
								FilePacket fpacket = new FilePacket();
								buffer = Files.readAllBytes((Path)Paths.get(fpacket.getFilename()));
								fpacket.setBuffer(buffer);
								System.err.println("Byte Array: "+buffer.length);
								//fpacket.success(true);
								oos.writeObject(fpacket);
								oos.flush();
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
						
						
					}*/
					Iterator fit = versions.keySet().iterator();
					while(fit.hasNext()){
						String name = (String)fit.next();
						System.err.println(name);
						FilePacket fpacket = new FilePacket();
						fpacket.setFilename(name);
						fpacket.setOperation(0);
						Iterator it = servers.keySet().iterator();
						while(it.hasNext()){
							synchronized(management.lock){
								System.err.println("iterating");
								Integer i = (Integer) it.next();
								String[] s = servers.get(i);
								Socket sock2 = new Socket(InetAddress.getByName(s[0]), i+2000);
								
								OutputStream sos = sock2.getOutputStream();
								InputStream sis = sock2.getInputStream();
								ObjectOutputStream serverOOP = new ObjectOutputStream(sos);
								ObjectInputStream serverOIS = new ObjectInputStream(sis);
								
								serverOOP.writeObject(fpacket);
								
								//System.err.println(fpacket.getBuffer().length);
								fpacket = (FilePacket) serverOIS.readObject();
								fpacket.setOperation(1);
								oos.writeObject(fpacket);
								oos.flush();
								
							}
						}
					}
					
				}
			} catch (IOException | ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
	}

}
