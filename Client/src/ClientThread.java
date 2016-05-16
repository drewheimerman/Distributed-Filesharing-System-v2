import java.io.*;
import java.net.*;
import java.util.*;
import java.nio.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;

import com.sun.jndi.toolkit.dir.DirSearch;

import sun.misc.IOUtils;

public class ClientThread implements Runnable {

	private MulticastUtilities mUtils;
	private UDPUtilities udpUtils;
	private Socket socket;
	
	private FilePacket fpacket;
	
	private InetAddress rmIP = null;
	private int rmPort = 0;
	
	public ClientThread(InetAddress fe, FilePacket p) throws IOException{
		socket = new Socket(fe, 2121);
		fpacket = p;
	}
	
	/*
	 * GOALS:
	 * 
	 * Send the filename over the rmRequestMulticast (X)
	 * Receive TCP port (X)
	 * Send file over TCP to RM ()
	 * Wait for response ()
	 * 
	 */
	
	@Override
	public void run() {
		System.out.println("thread started");
		try{
			//Set up 2-way communication streams on the TCP socket with the RM
			OutputStream os = socket.getOutputStream();
			InputStream is = socket.getInputStream();
			
			if(fpacket.getOperation()==1){
				readFile();
			}
			ObjectOutputStream oos = new ObjectOutputStream(os);
			System.err.println("send fpacket");
			oos.writeObject(fpacket);
			oos.flush();
			
			ObjectInputStream ois = new ObjectInputStream(is);
			FilePacket resultFilePacket = (FilePacket)ois.readObject();
			
			if(resultFilePacket.success()){
				System.out.println("success");
				if(resultFilePacket.getOperation()==0){
					receiveFile(resultFilePacket);
				}
				System.out.println(fpacket.getPreviousTimestamp());
			}
			ois.close();
			oos.close();	
		}catch(IOException e){
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	
	private synchronized void readFile(){
		byte[] buffer;
		try {
			buffer = Files.readAllBytes((Path)Paths.get(fpacket.getFilename()));
			fpacket.setBuffer(buffer);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	private synchronized void receiveFile(FilePacket fpacket){
		try {
			System.err.println("Byte Array: "+fpacket.getBuffer().length);
			FileOutputStream fos = new FileOutputStream(new File(fpacket.getFilename()));
			BufferedOutputStream bos = new BufferedOutputStream(fos);
			bos.write(fpacket.getBuffer());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
}
