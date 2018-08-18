package com.controller;

import com.controller.autopilot.Autopilot;
import com.controller.cameraTransmission.CameraTransmission;
import com.controller.messageHandler.MessageHandler;
import com.controller.socketConnection.SocketConnection;

import tools.Settings;

public class Main {

	public static void main(String[] args) {
		if(args.length!=0&&args[0]!=null) {
			Settings.getInstance().setSocketAPIKey(args[0]);	
		}
		
		if (Settings.getInstance().getStartMessageHandler())
			MessageHandler.getMessageHandler();
		if (Settings.getInstance().getStartAutopilot())
			Autopilot.getAutopilot();
		if (Settings.getInstance().getStartSocketConnection())
			SocketConnection.getSocketConnection();
		if(Settings.getInstance().getStartCameraTransmission()) {
			CameraTransmission.getCameraTransmission();
		}

	}

}
