import java.net.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.io.*;

public class ServerUpdater implements Runnable {

	Socket remoteManager;
	
	public ServerUpdater(Socket s){
			remoteManager = s;
	}
	
	
	@Override
	public void run() {
		try{
			OutputStream os = remoteManager.getOutputStream();
			InputStream is = remoteManager.getInputStream();
			ObjectOutputStream oos = new ObjectOutputStream(os);
			ObjectInputStream ois = new ObjectInputStream(is);
			
			FilePacket fpacket = (FilePacket)ois.readObject();
			
			if(fpacket.getOperation()==1){
				write(fpacket);
			}else{
				read(fpacket);
			}
			oos.writeObject(fpacket);
			System.out.println("return");
			oos.close();
			ois.close();
		}catch(IOException e){
			
		}catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

	}
	
	private synchronized void write(FilePacket fpacket){
		File file = new File("./"+fpacket.getFilename());
		try {
			FileOutputStream fos = new FileOutputStream(file);
			if(fpacket.getBuffer()!=null)
				fos.write(fpacket.getBuffer(), 0, fpacket.getBuffer().length);
			fpacket.success(true);
		} catch (IOException e) {
			fpacket.success(false);
		}
	}
	private synchronized void read(FilePacket fpacket){
		byte[] buffer = null;
		try {
			buffer = Files.readAllBytes((Path)Paths.get(fpacket.getFilename()));
			fpacket.setBuffer(buffer);
			System.err.println("Byte Array: "+buffer.length);
			fpacket.success(true);
		} catch (IOException e) {
			fpacket.success(false);
			e.printStackTrace();
		}
	}

}
