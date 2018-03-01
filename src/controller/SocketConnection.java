package controller;

import telecommand.Waypoint;
import telemetry.TelemetryMessage;

public class SocketConnection {
	
	public boolean connect(Autopilot autopilot) {
		//initialize
		//when received cmd
		Waypoint waypoint= new Waypoint();
		autopilot.send(waypoint);
		return true;
	}
	
	
	public boolean send(TelemetryMessage msg) {
		return true;
	}
	
	
	

}
