package com.controller;

import com.controller.autopilot.Autopilot;
import com.controller.messageHandler.MessageHandler;
import com.controller.socketConnection.SocketConnection;

public class Main {
	
	private static MessageHandler messageHandler = new MessageHandler();
	private static Autopilot autopilot = new Autopilot(messageHandler);
	private static SocketConnection socketConnection = new SocketConnection(messageHandler);
	

	public static void main(String[] args) {
		
		//connect to autopilot
		//autopilot.connect(messageHandler);
		//connect to server
		//socketConnection.connect(messageHandler);
		
	}

}
