package com.controller.autopilot;

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
		new AutopilotTransmitter(port);
		new AutopilotReceiver(port);
		// MissionGetter missionreceiver = new MissionGetter(port);
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
		port.setBaudRate(115200); // raspi = 115200; surfacePro4 = 9600;
		port.openPort();
		System.out
				.println("\n" + port.getDescriptivePortName() + " (Baudrate: " + port.getBaudRate() + ") is now open.");
		return port;
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
