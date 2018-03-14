package com.controller.socketConnection;

import com.controller.messageHandler.MessageHandler;
import com.telemetry.TelemetryMessage;

public class SocketConnection extends Thread{
	
	private MessageHandler messageHandler;
	public SocketConnection(MessageHandler messageHandler) {
		this.messageHandler = messageHandler;
		start();
	}
	
	@Override
	public void run() {
		connect();
	}
	
	
	

	public boolean connect() {
		//initialize
		//when received cmd
		//Waypoint waypoint= new Waypoint();
		//autopilot.send(waypoint);
		return true;
	}
	
	
	
	
	public boolean send(TelemetryMessage msg) {
		System.out.println((msg.getJSON()));
		
		return true;
	}
	
	
	

}
