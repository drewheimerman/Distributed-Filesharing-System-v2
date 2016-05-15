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
				//readFile();
				
			}
			ObjectOutputStream oos = new ObjectOutputStream(os);
			System.err.println("send fpacket");
			oos.writeObject(fpacket);
			oos.flush();
			
			ObjectInputStream ois = new ObjectInputStream(is);
			FilePacket resultFilePacket = (FilePacket)ois.readObject();
			
			
			if(resultFilePacket.success()){
				System.out.println("success");
			}
			ois.close();
			oos.close();	
		}catch(IOException e){
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		
		//^^^Clean^^^//
		
		/*-----Initialize TCP Socket-----*/
		
		/*try{
			Socket clientSocket = new Socket(rmIP, rmPort);
			
			DataOutputStream dos = new DataOutputStream(clientSocket.getOutputStream());
			DataInputStream dis = new DataInputStream(clientSocket.getInputStream());*/
			//send the action, filename to the server
			
		/*-----Upload to the server------*/
			
			/*File dir = new File("./");
			File file = null;
			
			if(action.equals("upload")){
				// /Users/Andrew/Development/EclipseWorkspace/DistributedFileTransfer/src
				File[] files = new File("./").listFiles();
				
				for(File f: files){
					if(f.isFile() && f.getName().equals(filename)){
						System.out.println(f.getName());
						file = f;
						break;
						
					}
				}
				if(file != null){
					dos.write((action+"\n").getBytes());
					dos.write((filename+"\n").getBytes());
					FileInputStream fis = new FileInputStream(file);
					BufferedInputStream bis = new BufferedInputStream(fis);
					System.out.println("Going to write the file to the stream.");
					sendFile(dos, fis, bis, file);
				//dos.writeLong(file.length());
					
					
				}else{
					System.out.println("There are no files named "+filename+" in this directory.");
				}
				
			}*/
		
		/*-----Download from the Server-----*/
			/*if(action.equals("download")){
				dos.write((action+"\n").getBytes());
				dos.write((filename+"\n").getBytes());
				FileOutputStream fos = new FileOutputStream("./"+filename);
				BufferedOutputStream bos = new BufferedOutputStream(fos);
				System.out.println(filename);
				receiveFile(dis, fos, bos);
				
				bos.flush();
				bos.close();
			}*/
			
		/*-----Get the result of the action, flush, and close-----*/
			/*int result = dis.readInt();
			if(result == 1){
				System.out.print(action+" complete.");
			}else{
				System.out.print(action+" failed");
			}
			dos.flush();
			
			clientSocket.close();
			udpUtils.close();
		}catch(IOException e){
			e.printStackTrace();
		}*/
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
	private synchronized void receiveFile(){
		try {
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
