import java.io.*;
import java.net.*;

public class MulticastUtilities {
	
	private static final int BUFFER_SIZE = 1024;
	
	
	/*
	 * 
	 * MULTICAST UTILITIES
	 * 	contains variabes:
	 * 		MulticastSocket socket
	 * 		int port
	 * 		int ttl
	 * 		InetAddress groupIP
	 * 
	 * 	contains methods:
	 * 		joinGroup(), joins multicast group
	 * 		leaveGroup(), leaves multicast group
	 * 		readObjectFrosocket(), reads and deserializes object from the MulticastSocket socket
	 * 		readStringFrosocket(), reads and returns a string from packet data
	 * 		sendToSocket(args), sends item with size less than const BUFFER_SIZE
	 * 			args:
	 * 				Object obj
	 * 				String s
	 * 				byte[] b
	 * 		deserializeObject(Object obj), deserializes object
	 * 		serializeObject(Object obj), serializes object
	 * 
	 */
	
	private MulticastSocket socket;
	private int port;
	private int ttl = 1;
	private InetAddress groupIP;
	
	public MulticastUtilities(){
		try{
			socket = new MulticastSocket(port);
			socket.setInterface(InetAddress.getLocalHost());
			socket.setTimeToLive(ttl);
			joinGroup();
		}catch(IOException e){
			e.printStackTrace();
		}
		
	}
	public MulticastUtilities(InetAddress group, int port){
		groupIP = group;
		this.port = port;
		try{
			socket = new MulticastSocket(port);
			socket.setInterface(InetAddress.getLocalHost());
			socket.setTimeToLive(ttl);
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
		socket.joinGroup(groupIP);
	}
	public void leave() throws IOException{
		socket.leaveGroup(groupIP);
	}
	public MulticastSocket getsocket() {
		return socket;
	}
	public void setsocket(MulticastSocket socket) {
		this.socket = socket;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
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
		socket.leaveGroup(groupIP);
		socket.close();
	}
	
	/*
	 * RECEIVING FROM SOCKET
	 * 
	 */
	
	public Object readFrosocket() throws IOException{
		byte[] buffer = new byte[BUFFER_SIZE];
		DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
		socket.receive(packet);
		Object received = deserializeObject(buffer);
		return 	received;
	}
	public String readStringFrosocket() throws IOException{
		byte[] buffer = new byte[BUFFER_SIZE];
		DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
		socket.receive(packet);
		return new String(packet.getData(), 0, packet.getLength());
	}
	public byte[] readBytesFrosocket() throws IOException{
		byte[] buffer = new byte[BUFFER_SIZE];
		DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
		socket.receive(packet);
		return packet.getData();
	}
	public DatagramPacket listen() throws IOException{
		byte[] buffer = new byte[BUFFER_SIZE];
		DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
		socket.receive(packet);
		return packet;
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
			socket.send(new DatagramPacket(buffer,buffer.length, groupIP, port));
		}else{
			System.err.println("The data is too large to send.");
		}
	}
	public void sendToSocket(String s) throws IOException{
		byte[] buffer = new byte[BUFFER_SIZE];
		buffer = s.getBytes();
		if(buffer.length<BUFFER_SIZE){
			socket.send(new DatagramPacket(buffer,buffer.length, groupIP, port));
		}else{
			System.err.println("The data is too large to send.");
		}
	}
	public void sendToSocket(byte[] b) throws IOException{
		if(b.length<BUFFER_SIZE){
			socket.send(new DatagramPacket(b,b.length, groupIP, port));
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
