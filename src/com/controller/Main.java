package com.controller;

public class Main {
	
	private static Autopilot autopilot = new Autopilot();
	private static Thread socketConnection = new SocketConnection();
	private static Thread messageHandler = new MessageHandler();

	public static void main(String[] args) {
		//connect to server
		((SocketConnection) socketConnection).connect(((MessageHandler)messageHandler));
		//connect to autopilot
		autopilot.connect((MessageHandler) messageHandler);

		
	}

}
