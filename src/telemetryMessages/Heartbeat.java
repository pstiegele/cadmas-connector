package telemetryMessages;

import java.util.Date;

public class Heartbeat implements TelemetryMessage{
	
	
	public Heartbeat() {
		
	}

	@Override
	public boolean send() {
		
		return false;
	}

}
