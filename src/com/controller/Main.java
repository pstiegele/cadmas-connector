package com.controller;

public class Main {
	
	private static Autopilot autopilot = new Autopilot();
	private static SocketConnection socketConnection = new SocketConnection();
	private static MessageHandler messageHandler = new MessageHandler();

	public static void main(String[] args) {
		//connect to server
		socketConnection.connect(messageHandler);
		//connect to autopilot
		autopilot.connect(messageHandler);
		
		
		
		

	}

}
