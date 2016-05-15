import java.io.Serializable;
import java.util.UUID;
import java.util.Vector;

public class FilePacket implements Serializable{

	/**
	 * UUID uuid : unique request identifier (set by Frontend)
	 * Vector timestamp : current timestamp of the Frontend (set by Frontend)
	 * int md5 : file md5 checksum, not implemented
	 * 
	 * String filename : the name of the file
	 * byte[] buffer : file bytes
	 * int operation : operation of the packet, 0 download, 1 upload, 2 update
	 * 
	 */
	
	private UUID uuid;
	private Vector<Integer> timestamp;
	private int md5;
	
	private String filename;
	private byte[] buffer;
	private int operation;
	
	private boolean success = false;
	
	//PACKET INFORMATION
	
	public UUID getUuid() {
		return uuid;
	}
	public void setUuid(UUID uuid) {
		this.uuid = uuid;
	}
	public Vector<Integer> getPreviousTimestamp(){
		return timestamp;
	}
	public void setTimestamp(Vector<Integer> v){
		timestamp = v;
	}
	public int getMd5() {
		return md5;
	}
	public void setMd5(int md5) {
		this.md5 = md5;
	}
	public boolean success(){
		return success;
	}
	public void success(boolean b){
		success = b;
	}
	
	//FILE GETTERS AND SETTERS
	
	public String getFilename(){
		return filename;
	}
	public void setFilename(String f){
		filename = f;
	}
	public byte[] getBuffer() {
		return buffer;
	}
	public void setBuffer(byte[] buffer) {
		this.buffer = buffer;
	}
	public int getOperation(){
		return operation;
	}
	public void setOperation(int i){
		operation = i;
	}
	
}
