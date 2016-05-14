import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.Socket;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sun.jndi.toolkit.dir.DirSearch;

public class ClientThread implements Runnable {

	private MulticastUtilities mUtils;
	private UDPUtilities udpUtils;
	private String action;
	private String filename;
	
	private InetAddress rmIP = null;
	private int rmPort = 0;
	
	public ClientThread(MulticastUtilities m, String action, String filename){
		mUtils = m;
		udpUtils = new UDPUtilities();
		this.action = action;
		this.filename = filename;
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
			mUtils.sendToSocket(filename);
			System.out.println("filename sent");
			
			Pattern pattern = Pattern.compile("\\d+");
			
			Matcher matcher = null;
			String reply = "";
			DatagramPacket p = null;
			while(matcher == null || !matcher.matches()){
				p = mUtils.listen();
				reply = new String(p.getData(), 0, p.getLength());
				matcher = pattern.matcher(reply);
			}
			
			System.out.println(reply);
			
			
			
			rmIP = p.getAddress();
			rmPort = Integer.parseInt(reply);
			
		}catch(IOException e){
			e.printStackTrace();
		}
		
		
		//^^^Clean^^^//
		
		/*-----Initialize TCP Socket-----*/
		
		try{
			Socket clientSocket = new Socket(rmIP, rmPort);
			
			DataOutputStream dos = new DataOutputStream(clientSocket.getOutputStream());
			DataInputStream dis = new DataInputStream(clientSocket.getInputStream());
			//send the action, filename to the server
			
		/*-----Upload to the server------*/
			
			File dir = new File("./");
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
				
			}
		
		/*-----Download from the Server-----*/
			if(action.equals("download")){
				dos.write((action+"\n").getBytes());
				dos.write((filename+"\n").getBytes());
				FileOutputStream fos = new FileOutputStream("./"+filename);
				BufferedOutputStream bos = new BufferedOutputStream(fos);
				System.out.println(filename);
				receiveFile(dis, fos, bos);
				
				bos.flush();
				bos.close();
			}
			
		/*-----Get the result of the action, flush, and close-----*/
			int result = dis.readInt();
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
		}
	}
	
	
	private synchronized void sendFile(DataOutputStream dos, FileInputStream fis, BufferedInputStream bis, File file){
		
		int bytes = 128;
		long read = 0;
		byte[] b = new byte[bytes];
		System.out.println(file.length());
		while(read < file.length()){
			if(file.length()-read >= bytes){
				read = read + bytes;
			}else{
				bytes = (int)(file.length()-read);
				read = read + bytes;
			}
			try{
				bis.read(b, 0 , bytes);
				System.out.println(b.length);
				for(byte a: b){
					System.out.print(a);
				}
				dos.write(b);
			}catch(Exception e){
				e.printStackTrace();
			}
			System.out.println((read/file.length())*100+"%");
		}
	}
	private synchronized void receiveFile(DataInputStream dis, FileOutputStream fos, BufferedOutputStream bos){
		
		byte[] b = new byte[128];
		int size = 0;
		try{
			size = dis.read(b);
			while(size != -1){
				bos.write(b, 0, size);
				size = dis.read(b);
			}
		}catch(IOException e){
			e.printStackTrace();
		}
	
	}
}
