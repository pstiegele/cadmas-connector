package com.controller.autopilot;

import java.net.SocketException;
import java.net.UnknownHostException;

import com.MAVLink.MAVLinkPacket;
import com.fazecast.jSerialComm.SerialPort;
import com.telemetry.Attitude;
import com.telemetry.Battery;
import com.telemetry.CommandAck;
import com.telemetry.Heartbeat;
import com.telemetry.MissionItem;
import com.telemetry.MissionState;
import com.telemetry.Position;
import com.telemetry.Velocity;

import tools.Settings;

public class Autopilot extends Thread {

	private SerialPort port;
	private static Autopilot autopilot;
	AutopilotTransmitter transmitter;
	AutopilotReceiver receiver;

	public Autopilot() {
		Autopilot.autopilot = this;
		this.setName("Autopilot");
		start();
	}

	public static Autopilot getAutopilot() {
		if (autopilot == null) {
			new Autopilot();
		}
		return autopilot;
	}

	@Override
	public void run() {
		connect();
		// onClose:
		// port.closePort();
	}

	public void connect() {
		if (!Settings.getInstance().getUseUDP())
			port = init();
		initTelemetryObjects();
		transmitter = new AutopilotTransmitter(port);
		receiver = new AutopilotReceiver(port);
		//runTest(transmitter);
	}
	
	public void runTest(AutopilotTransmitter transmitter) {
		Test test = new Test(transmitter);
		try {
			System.out.println("test result: " + test.runTestFlight());
		} catch (UnknownHostException | SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
		SerialPort port = ports[Settings.getInstance().getSerialPort()]; // raspi = 0; surfacePro4 = 2
		port.setBaudRate(Settings.getInstance().getBaudRate()); // raspi = 115200; surfacePro4 = 9600;
		port.openPort();
		System.out
				.println("\n" + port.getDescriptivePortName() + " (Baudrate: " + port.getBaudRate() + ") is now open.");
		return port;
	}
	
	public AutopilotTransmitter getAutopilotTransmitter() {
		return transmitter;
	}
	
	public AutopilotReceiver getAutopilotReceiver() {
		return receiver;
	}
	

	private void initTelemetryObjects() {
		new Attitude();
		new Battery();
		new Heartbeat();
		new MissionState();
		new Position();
		new Velocity();
		new MissionItem();
		new CommandAck();
	}

}
