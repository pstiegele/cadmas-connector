package com.controller;

import com.MAVLink.MAVLinkPacket;
import com.MAVLink.Messages.MAVLinkMessage;
import com.MAVLink.Messages.MAVLinkPayload;
import com.MAVLink.common.msg_mission_item;
import com.MAVLink.enums.MAV_CMD;

public class Main {
	
	private static Autopilot autopilot = new Autopilot();
	private static SocketConnection socketConnection = new SocketConnection();
	private static MessageHandler messageHandler = new MessageHandler();

	public static void main(String[] args) {
		//System.out.println("connector started");
		//connect to server
		socketConnection.connect(messageHandler);
		//connect to autopilot
		autopilot.connect(messageHandler);

	}

}
