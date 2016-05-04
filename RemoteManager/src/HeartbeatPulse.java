
public class HeartbeatPulse implements Runnable {
	private MulticastUtilities heartbeatMulticast;
	
	public HeartbeatPulse(MulticastUtilities m){
		heartbeatMulticast = m;
	}
	
	@Override
	public void run() {
		while(true){
			try{
				heartbeatMulticast.sendToSocket("ping");
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
