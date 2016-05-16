
public class HeartbeatPulse implements Runnable {
	private MulticastUtilities heartbeatMulticast;
	private int port;
	
	public HeartbeatPulse(MulticastUtilities m, int p){
		heartbeatMulticast = m;
		port = p;
	}
	
	@Override
	public void run() {
		while(true){
			try{
				heartbeatMulticast.sendToSocket(""+port);
			}catch(Exception e){
				e.printStackTrace();
			}
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
				break;
			}
		}
	}

}
