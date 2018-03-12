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
		System.out.println("start init");
		SerialPort port = init();
		System.out.println("init finished");
		while(true) {
			//System.out.println("start getPacket");
			MAVLinkPacket mavpacket = getPacket(port);
			//System.out.println("finished getPacket");
			//System.out.println("start handlePacket");
			handlePacket(mavpacket);
			//System.out.println("finished handlePacket");
		}
		
		
		//port.closePort();
		//return true;
	}
	
	
	private void handlePacket(MAVLinkPacket mavpacket) {
		//System.out.println("mavpacket id: "+mavpacket.msgid);
		switch (mavpacket.msgid) {
		case msg_heartbeat.MAVLINK_MSG_ID_HEARTBEAT:
			Heartbeat hb = new Heartbeat(mavpacket);
			//socketConnection.send(hb);
			break;
		case msg_attitude.MAVLINK_MSG_ID_ATTITUDE:
			msg_attitude att = new msg_attitude(mavpacket);
			System.out.println(att.pitch);
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
				//System.out.println("wait for bytes");
				while (port.bytesAvailable() == 0)
					Thread.sleep(20);
				int bytesToRead = port.bytesAvailable();
				//System.out.println("there are "+bytesToRead+" bytes to read");
				byte[] readBuffer = new byte[bytesToRead];
				//System.out.println("read: "+readBuffer.toString());
				port.readBytes(readBuffer, bytesToRead);
				int readarr[] = new int[bytesToRead];
				for (int i = 0; i < readBuffer.length; i++) {
					readarr[i] = unsignedToBytes(readBuffer[i]);
				}
				//System.out.println("unsignedToBytes: "+readarr.toString());
				MAVLinkPacket mavpacket = null;
				for (int i = 0; i < readarr.length; i++) {
					//System.out.println("parse char: "+readarr[i]);
					mavpacket = parser.mavlink_parse_char(readarr[i]);
					if (mavpacket != null) {
						//System.out.println("mavpacket found!!!");
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
		port.setBaudRate(115200);
		port.openPort();
		System.out.println("port is open: "+port.getBaudRate()+"baud. "+port.getDescriptivePortName());
		return port;
	}
	
	private static int unsignedToBytes(byte a) {
		int b = a & 0xFF;
		return b;
	}
}
