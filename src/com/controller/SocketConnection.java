package com.controller;

import com.telemetry.TelemetryMessage;

public class SocketConnection extends Thread{
	
	MessageHandler messageHandler;
	public boolean connect(MessageHandler messageHandler) {
		this.messageHandler = messageHandler;
		start();
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
	
	@Override
	public void run() {
		
	}
	
	
	

}
