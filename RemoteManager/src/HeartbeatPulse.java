
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
				String s = Integer.toString(port);
				//int i = Integer.parseInt(s);
				//System.out.println(i);
				heartbeatMulticast.sendToSocket(s);
			}catch(Exception e){
				e.printStackTrace();
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
				break;
			}
		}
	}

}
