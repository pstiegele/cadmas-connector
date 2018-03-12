package com.controller;

import com.MAVLink.MAVLinkPacket;
import com.MAVLink.Parser;
import com.MAVLink.common.msg_attitude;
import com.MAVLink.common.msg_heartbeat;
import com.MAVLink.common.msg_scaled_pressure;
import com.fazecast.jSerialComm.SerialPort;
import com.telecommand.TelecommandMessage;
import com.telemetry.Heartbeat;

public class Autopilot {

	SocketConnection socketConnection=null;
	public boolean connect(SocketConnection socketConnection) {
		this.socketConnection=socketConnection;
		//initialize
		SerialPort port = init();
		while(true) {
			MAVLinkPacket mavpacket = getPacket(port);
			handlePacket(mavpacket);
		}
		
		
		//port.closePort();
		//return true;
	}
	
	
	private void handlePacket(MAVLinkPacket mavpacket) {
		switch (mavpacket.msgid) {
		case msg_heartbeat.MAVLINK_MSG_ID_HEARTBEAT:
			Heartbeat hb = new Heartbeat(mavpacket);
			socketConnection.send(hb);
			break;
		case msg_attitude.MAVLINK_MSG_ID_ATTITUDE:
			msg_attitude att = new msg_attitude(mavpacket);
			//System.out.println(att);
			break;
		case msg_scaled_pressure.MAVLINK_MSG_ID_SCALED_PRESSURE:
			msg_scaled_pressure press = new msg_scaled_pressure(mavpacket);
			//System.out.println(press);
			break;

		default:
			//System.out.println("uncovered packet");
			break;
		}
		
	}


	/**
	 * @param port The SerialPort
	 * @return return MAVLinkPacket
	 */
	private MAVLinkPacket getPacket(SerialPort port) {
		Parser parser = new Parser();
		try {
			while (true) {
				while (port.bytesAvailable() == 0)
					Thread.sleep(20);
				int bytesToRead = port.bytesAvailable();
				byte[] readBuffer = new byte[bytesToRead];
				port.readBytes(readBuffer, bytesToRead);
				int readarr[] = new int[bytesToRead];
				for (int i = 0; i < readBuffer.length; i++) {
					readarr[i] = unsignedToBytes(readBuffer[i]);
				}

				MAVLinkPacket mavpacket = null;
				for (int i = 0; i < readarr.length; i++) {
					mavpacket = parser.mavlink_parse_char(readarr[i]);
					if (mavpacket != null) {
						return mavpacket;
						
					}
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
		
	}


	public boolean send(TelecommandMessage cmd) {
		//send telecommand to autopilot
		return true;
	}
	
	private SerialPort init() {
		SerialPort[] ports = SerialPort.getCommPorts();
		for (SerialPort serialPort : ports) {
			System.out.println(serialPort.getDescriptivePortName() + " | Baudrate: " + serialPort.getBaudRate());
		}
		SerialPort port = ports[0];
		port.openPort();
		return port;
	}
	
	private static int unsignedToBytes(byte a) {
		int b = a & 0xFF;
		return b;
	}
}
