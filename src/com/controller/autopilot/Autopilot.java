package com.controller.autopilot;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.SocketException;
import java.net.UnknownHostException;

import com.MAVLink.MAVLinkPacket;
import com.MAVLink.common.msg_mission_clear_all;
import com.MAVLink.common.msg_set_home_position;
import com.controller.autopilot.udp.UDPInputStream;
import com.controller.autopilot.udp.UDPOutputStream;
import com.controller.messageHandler.MessageHandler;
import com.fazecast.jSerialComm.SerialPort;

public class Autopilot extends Thread implements ActionListener {

	public volatile short droneCompID = 1;
	public volatile short droneSysID = 1;
	public volatile short connectorCompID = 1;
	public volatile short connectorSysID = 1;
	private volatile short seq = 0;

	private MessageHandler messageHandler;
	private SerialPort port;
	private OutputStream out;
	private InputStream in;

	public AutopilotTransmitter transmitter;
	public AutopilotReceiver receiver;

	public Autopilot(MessageHandler messageHandler) {
		this.messageHandler = messageHandler;
		start();
	}

	@Override
	public void run() {
		connect();
		// onClose:
		// port.closePort();
	}

	public void connect() {
		boolean seriell = false;
		if(seriell) {
			port = init();
			out = port.getOutputStream();
			in = port.getInputStream();	
		}else {
			try {
				 out = new UDPOutputStream("127.0.0.1", 14551);
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SocketException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			try {
				in = new UDPInputStream("127.0.0.1", 14551);
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SocketException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		// test();
		transmitter = new AutopilotTransmitter(out);
		receiver = new AutopilotReceiver(in);
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

	public void send(MAVLinkPacket packet) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				transmitter.send(packet);
			}
		}).start();
	}

	public short getSeq() {
		// TODO: Passt das so?
		short returnValue = seq;
		seq = (short) (seq++ % 255);
		return returnValue;
	}

	public void setListener() {

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

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub

	}

}
