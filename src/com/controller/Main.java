package com.controller;

import com.controller.autopilot.Autopilot;
import com.controller.messageHandler.MessageHandler;
import com.controller.socketConnection.SocketConnection;


public class Main {

	public static void main(String[] args) {

		//if (Settings.getInstance().getStartMessageHandler())
			MessageHandler.getMessageHandler();
		//if (Settings.getInstance().getStartAutopilot())
			Autopilot.getAutopilot();
		//if (Settings.getInstance().getStartSocketConnection())
			SocketConnection.getSocketConnection();

	}

}
