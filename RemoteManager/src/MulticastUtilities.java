import java.io.*;
import java.net.*;

public class MulticastUtilities {
	
	private static final int BUFFER_SIZE = 1024;
	
	
	/*
	 * 
	 * MULTICAST UTILITIES
	 * 	contains variabes:
	 * 		MulticastSocket mSocket
	 * 		int port
	 * 		int ttl
	 * 		InetAddress groupIP
	 * 
	 * 	contains methods:
	 * 		joinGroup(), joins multicast group
	 * 		leaveGroup(), leaves multicast group
	 * 		readObjectFromSocket(), reads and deserializes object from the MulticastSocket mSocket
	 * 		readStringFromSocket(), reads and returns a string from packet data
	 * 		sendToSocket(args), sends item with size less than const BUFFER_SIZE
	 * 			args:
	 * 				Object obj
	 * 				String s
	 * 				byte[] b
	 * 		deserializeObject(Object obj), deserializes object
	 * 		serializeObject(Object obj), serializes object
	 * 
	 */
	
	private MulticastSocket mSocket;
	private int mPort;
	private int ttl = 1;
	private InetAddress groupIP;
	
	public MulticastUtilities(){
		try{
			mSocket = new MulticastSocket(mPort);
			mSocket.setInterface(InetAddress.getLocalHost());
			mSocket.setTimeToLive(ttl);
			joinGroup();
		}catch(IOException e){
			e.printStackTrace();
		}
		
	}
	public MulticastUtilities(InetAddress group, int port){
		groupIP = group;
		mPort = port;
		try{
			mSocket = new MulticastSocket(mPort);
			mSocket.setInterface(InetAddress.getLocalHost());
			mSocket.setTimeToLive(ttl);
			joinGroup();
		}catch(IOException e){
			e.printStackTrace();
		}
	}

	
	/*
	 * GETTERS AND SETTERS
	 * 
	 */
	
	public void joinGroup() throws IOException{
		mSocket.joinGroup(groupIP);
	}
	public void leave() throws IOException{
		mSocket.leaveGroup(groupIP);
	}
	public MulticastSocket getmSocket() {
		return mSocket;
	}
	public void setmSocket(MulticastSocket mSocket) {
		this.mSocket = mSocket;
	}
	public int getmPort() {
		return mPort;
	}
	public void setmPort(int mPort) {
		this.mPort = mPort;
	}
	public int getTtl() {
		return ttl;
	}
	public void setTtl(int ttl) {
		this.ttl = ttl;
	}
	public InetAddress getGroupIP() {
		return groupIP;
	}
	public void setGroupIP(InetAddress groupIP) {
		this.groupIP = groupIP;
	}
	public void leaveGroup() throws IOException{
		mSocket.leaveGroup(groupIP);
		mSocket.close();
	}
	
	/*
	 * RECEIVING FROM SOCKET
	 * 
	 */
	
	public Object readFromSocket() throws Exception{
		byte[] buffer = new byte[BUFFER_SIZE];
		DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
		mSocket.receive(packet);
		Object received = deserializeObject(buffer);
		return 	received;
	}
	public String readStringFromSocket() throws IOException{
		byte[] buffer = new byte[BUFFER_SIZE];
		DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
		mSocket.receive(packet);
		return new String(packet.getData(), 0, packet.getLength());
	}
	public byte[] readBytesFromSocket() throws IOException{
		byte[] buffer = new byte[BUFFER_SIZE];
		DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
		mSocket.receive(packet);
		return buffer;
	}
	
	/*
	 * SENDING TO SOCKET
	 *
	 */
	public void sendToSocket(Object obj) throws IOException{
		ByteArrayOutputStream baos = serializeObject(obj);
		byte[] buffer;
		buffer = baos.toByteArray();
		if(buffer.length<BUFFER_SIZE){
			mSocket.send(new DatagramPacket(buffer,buffer.length, groupIP, mPort));
		}else{
			System.err.println("The data is too large to send.");
		}
	}
	public void sendToSocket(String s) throws IOException{
		byte[] buffer = new byte[1024];
		buffer = s.getBytes();
		if(buffer.length<BUFFER_SIZE){
			mSocket.send(new DatagramPacket(buffer,buffer.length, groupIP, mPort));
		}else{
			System.err.println("The data is too large to send.");
		}
	}
	public void sendToSocket(byte[] b) throws IOException{
		if(b.length<BUFFER_SIZE){
			mSocket.send(new DatagramPacket(b,b.length, groupIP, mPort));
		}else{
			System.err.println("The data is too large to send.");
		}
	}
	
	
	/*
	 * READING AND PRINTING INPUT/OUTPUT FROM/TO LOCAL MACHINE (UNUSED)
	 * 
	 */
	
	public String readKeyboardInput() throws IOException {
		BufferedReader buffreader = new BufferedReader(new InputStreamReader(System.in));
		System.out.println("Input: ");
		return buffreader.readLine();
	}
	
	public void print(String s){
		System.out.println(s);
	}
	public void printChat(String s){
		System.out.println("Chat: " + s);
	}
	
	
	/*
	 * OBJECT SERIALIZATION
	 * 
	 */
	
	public Object deserializeObject(byte[] data){
		try{
			ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data));
			Object obj = (Object)ois.readObject();
			return obj;
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
	public ByteArrayOutputStream serializeObject(Object obj){
		try{
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(obj);
			return baos;
		}catch(IOException e){
			e.printStackTrace();
		}
		return null;
	}
	
}
