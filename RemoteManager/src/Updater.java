import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.concurrent.ConcurrentSkipListMap;

public class Updater implements Runnable {

	private RemoteManager.Management management;
	private ConcurrentSkipListMap<Integer, String[]> servers;
	
	public Updater(RemoteManager.Management m, ConcurrentSkipListMap<Integer, String[]> s){
		management = m;
		servers = s;
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
					FilePacket fpacket = new FilePacket();
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
							fpacket = (FilePacket) serverOIS.readObject();
							if(fpacket.getOperation()==0){
								break;
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
