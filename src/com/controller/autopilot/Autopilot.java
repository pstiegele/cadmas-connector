package com.controller.autopilot;

import com.MAVLink.MAVLinkPacket;
import com.fazecast.jSerialComm.SerialPort;

public class Autopilot extends Thread {

	
	private SerialPort port;
	private static Autopilot autopilot;

	public Autopilot() {
		Autopilot.autopilot=this;
		this.setName("Autopilot");
		start();
	}
	
	public static Autopilot getAutopilot() {
		if(autopilot==null) {
			new Autopilot();
		}
		return autopilot;
	}

	@Override
	public void run() {
		connect();
		//onClose:
		//port.closePort();
	}

	public void connect() {
		//port = init();
		AutopilotTransmitter transmitter = new AutopilotTransmitter(port);
		AutopilotReceiver receiver = new AutopilotReceiver(port);
		//MissionGetter missionreceiver = new MissionGetter(port);
	}

	public boolean send(MAVLinkPacket mavLinkPacket) {
		// MAVLinkMessage msg = cmd.getMAVLink();
		// send telecommand to autopilot
		return true;
	}

	private SerialPort init() {
		SerialPort[] ports = SerialPort.getCommPorts();
		for (SerialPort serialPort : ports) {
			System.out.println(serialPort.getDescriptivePortName() + " | Baudrate: " + serialPort.getBaudRate());
		}
		SerialPort port = ports[2];
		port.setBaudRate(9600);
		port.openPort();
		System.out.println("\n" + port.getDescriptivePortName() + " (Baudrate: " + port.getBaudRate() + ") is now open.");
		return port;
	}

	
}
