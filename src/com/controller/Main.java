package com.controller;

import com.controller.autopilot.Autopilot;
import com.controller.messageHandler.MessageHandler;
import com.controller.socketConnection.SocketConnection;

public class Main {
	
	
	

	public static void main(String[] args) {
		MessageHandler messageHandler = new MessageHandler();
		new Autopilot(messageHandler);
		new SocketConnection(messageHandler);
		
	}

}
