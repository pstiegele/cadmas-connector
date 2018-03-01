package controller;

import telemetryMessages.Heartbeat;

public class Autopilot {

	
	public boolean connect(SocketConnection socketConnection) {
		//initialize
		
		//wait for packages
		
		//filter for eg heartbeat
			Heartbeat hb = new Heartbeat();
			socketConnection.send(hb);
		//filter for attitude
		//filter for altitude
		///...
		
		return true;
	}
}
