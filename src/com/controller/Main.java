package com.controller;

import com.MAVLink.enums.MAV_FRAME;
import com.controller.autopilot.Autopilot;
import com.controller.cameraTransmission.CameraTransmission;
import com.controller.messageHandler.MessageHandler;
import com.controller.socketConnection.SocketConnection;

import argparser.ArgParser;
import argparser.BooleanHolder;
import argparser.DoubleHolder;
import argparser.IntHolder;
import argparser.StringHolder;
import tools.Settings;

public class Main {

	public static void main(String[] args) {
		parseArguments(args);
		
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

	private static void parseArguments(String[] args) {
		//setup values
		IntHolder serialPort = new IntHolder(3); // raspi = 3; yoga: 0
		IntHolder baudRate = new IntHolder(115200); // raspi = 115200; yoga = 9600;
		IntHolder telemetryRefreshRate = new IntHolder(1000);
		
		//autopilot values
		IntHolder loiterRadius = new IntHolder(50);
		IntHolder abortAltitude = new IntHolder(10);
		IntHolder takeOffPitch = new IntHolder(10);
		IntHolder frameOrientation = new IntHolder(MAV_FRAME.MAV_FRAME_GLOBAL_RELATIVE_ALT);
		
		//debugging values
		BooleanHolder startAutopilot = new BooleanHolder(true);
		BooleanHolder startMessageHandler = new BooleanHolder(true);
		BooleanHolder startSocketConnection = new BooleanHolder(true);
		BooleanHolder startCameraTransmission = new BooleanHolder(true);
		BooleanHolder emulateCamera = new BooleanHolder(false);
		BooleanHolder emulateCpuTemp = new BooleanHolder(false);
		IntHolder cameraIntervall = new IntHolder(2000);		//in ms
		BooleanHolder useUDP = new BooleanHolder(false);
		StringHolder udpIPAdress = new StringHolder("localhost");
		IntHolder udpOutgoingPort = new IntHolder(63091);
		BooleanHolder retryOpenArdupilotPort = new BooleanHolder(false);
		
		//socket settings
		StringHolder socketURI = new StringHolder("ws://localhost/connector");
		StringHolder socketAPIKey = new StringHolder("myapikey");
		StringHolder emulateCameraFilePath = new StringHolder("/tmp/cameratest");
	 
	    // create the parser and specify the allowed options ...
	 
	    ArgParser parser = new ArgParser("java -jar \"Ardupilot Connector.jar\"");
	    parser.addOption ("-serialPort %i #port of the Ardupilot", serialPort); 
	    parser.addOption ("-baudRate %i #baudrate of the Ardupilot", baudRate);
	    parser.addOption ("-telemetryRefreshRate %i #How often should a Telemetry Topic be sent?", telemetryRefreshRate);
	    parser.addOption ("-loiterRadius %i #loiter radius", loiterRadius);
	    parser.addOption ("-abortAltitude %i #abort Altitude", abortAltitude);
	    parser.addOption ("-takeOffPitch %i #takeOffPitch", takeOffPitch);
	    parser.addOption ("-frameOrientation %i #MAVLINK frameOrientation", frameOrientation);
	    parser.addOption ("-startAutopilot %b #only for debugging: Should the Autopilot Thread be started?", startAutopilot);
	    parser.addOption ("-startMessageHandler %b #only for debugging: Should the Message Handler Thread be started?", startMessageHandler);
	    parser.addOption ("-startSocketConnection %b #only for debugging: Should the Socket Connection Thread be started?", startSocketConnection);
	    parser.addOption ("-startCameraTransmission %b #only for debugging: Should the Camera Transmission Thread be started?", startCameraTransmission);
	    parser.addOption ("-emulateCamera %b #Should the camera be emulated?", emulateCamera);
	    parser.addOption ("-emulateCpuTemp %b #Should the CPU temperature be emulated?", emulateCpuTemp);
	    parser.addOption ("-cameraIntervall %i #How often should a Camera Image be sent?", cameraIntervall);
	    parser.addOption ("-useUDP %b #use UDP instead of a seriell connection (e.g. for SITL)", useUDP);
	    parser.addOption ("-udpIPAdress %s #udp IP Adress to connect Autopilot", udpIPAdress);
	    parser.addOption ("-udpOutgoingPort %i #outgoing UDP Port (should be fine on default settings)", udpOutgoingPort);
	    parser.addOption ("-retryOpenArdupilotPort %b #Retry to connect to Autopilot on a failed connection", retryOpenArdupilotPort);
	    parser.addOption ("-socketURI %s #Server URI, necessary when you are self hosting CADMAS", socketURI);
	    parser.addOption ("-socketAPIKey %s #API Key of the drone, this is necessary for a connection", socketAPIKey);
	    parser.addOption ("-emulateCameraFilePath %s #if you emulate the camera: this is the path of the images", emulateCameraFilePath);
	    

	    // match the arguments ...
	 
	    parser.matchAllArgs (args);

	    // and print out the values

	    Settings.getInstance().setSerialPort(serialPort.value);
	    Settings.getInstance().setBaudRate(baudRate.value);
	    Settings.getInstance().setTelemetryRefreshRate(telemetryRefreshRate.value);
	    Settings.getInstance().setLoiterRadius(loiterRadius.value);
	    Settings.getInstance().setAbortAltitude(abortAltitude.value);
	    Settings.getInstance().setTakeOffPitch(takeOffPitch.value);
	    Settings.getInstance().setFrameOrientation((short)frameOrientation.value);
	    Settings.getInstance().setStartAutopilot(startAutopilot.value);
	    Settings.getInstance().setStartMessageHandler(startMessageHandler.value);
	    Settings.getInstance().setStartSocketConnection(startSocketConnection.value);
	    Settings.getInstance().setStartCameraTransmission(startCameraTransmission.value);
	    Settings.getInstance().setEmulateCamera(emulateCamera.value);
	    Settings.getInstance().setEmulateCpuTemp(emulateCpuTemp.value);
	    Settings.getInstance().setCameraIntervall(cameraIntervall.value);
	    Settings.getInstance().setUseUDP(useUDP.value);
	    Settings.getInstance().setUdpIPAdress(udpIPAdress.value);
	    Settings.getInstance().setUdpOutgoingPort(udpOutgoingPort.value);
	    Settings.getInstance().setRetryOpenArdupilotPort(retryOpenArdupilotPort.value);
	    Settings.getInstance().setSocketURI(socketURI.value);
	    Settings.getInstance().setSocketAPIKey(socketAPIKey.value);
	    Settings.getInstance().setEmulateCameraFilePath(emulateCameraFilePath.value);
	}

}
