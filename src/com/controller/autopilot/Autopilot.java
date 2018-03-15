package com.controller.autopilot;

import java.io.IOException;

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
		port = init();
		//test();
		AutopilotTransmitter transmitter = new AutopilotTransmitter(port);
		AutopilotReceiver receiver = new AutopilotReceiver(port);
	}

	public void test() {
		// System.out.println("init finished");
		/*
		 * while(true) { //System.out.println("start getPacket"); MAVLinkPacket
		 * mavpacket = getPacket(port); //System.out.println("finished getPacket");
		 * //System.out.println("start handlePacket"); handlePacket(mavpacket);
		 * //System.out.println("finished handlePacket"); }
		 */

		// ########## MAVLINK SEND TEST BEGIN ##########
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		msg_mission_clear_all clearitem = new msg_mission_clear_all();
		MAVLinkPacket pclear = clearitem.pack();
		try {
			port.getOutputStream().write(pclear.encodePacket());
			System.out.println("clear sent");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			Thread.sleep(5000);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		msg_set_home_position homeitem = new msg_set_home_position();
		homeitem.latitude = 498186868;
		homeitem.longitude = 98934782;
		homeitem.altitude = 200000;
		MAVLinkPacket phome = homeitem.pack();
		try {
			port.getOutputStream().write(phome.encodePacket());
			System.out.println("home position set");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// ########## MAVLINK SEND TEST END ##########
	}

	

	public boolean send(TelecommandMessage cmd) {
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
		System.out
				.println("\n" + port.getDescriptivePortName() + " (Baudrate: " + port.getBaudRate() + ") is now open.");
		return port;
	}

	
}
