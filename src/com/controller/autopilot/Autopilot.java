package com.controller.autopilot;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import com.MAVLink.MAVLinkPacket;
import com.MAVLink.Parser;
import com.MAVLink.common.msg_attitude;
import com.MAVLink.common.msg_heartbeat;
import com.MAVLink.common.msg_mission_clear_all;
import com.MAVLink.common.msg_scaled_pressure;
import com.MAVLink.common.msg_set_home_position;
import com.controller.messageHandler.MessageHandler;
import com.fazecast.jSerialComm.SerialPort;
import com.telecommand.TelecommandMessage;
import com.telemetry.Heartbeat;

public class Autopilot extends Thread {

	private MessageHandler messageHandler;
	private SerialPort port;

	public Autopilot(MessageHandler messageHandler) {
		this.setName("Autopilot");
		this.messageHandler = messageHandler;
		start();
	}

	@Override
	public void run() {
		connect();
		//onClose:
		//port.closePort();
	}

	public void connect() {
		//port = init();
		//AutopilotTransmitter transmitter = new AutopilotTransmitter(port);
		AutopilotReceiver receiver = new AutopilotReceiver(port);
		//MissionReceiver missionreceiver = new MissionReceiver(port);
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
