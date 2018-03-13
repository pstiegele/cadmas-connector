package com.controller;

import com.telecommand.Waypoint;
import com.telemetry.TelemetryMessage;

public class SocketConnection {
	
	MessageHandler messageHandler;
	public boolean connect(MessageHandler messageHandler) {
		this.messageHandler = messageHandler;
		//initialize
		//when received cmd
		Waypoint waypoint= new Waypoint();
		//autopilot.send(waypoint);
		return true;
	}
	
	
	public boolean send(TelemetryMessage msg) {
		System.out.println((msg.getJSON()));
		
		return true;
	}
	
	
	

}
