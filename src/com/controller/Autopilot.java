package com.controller;

import java.io.IOException;

import com.MAVLink.MAVLinkPacket;
import com.MAVLink.Parser;
import com.MAVLink.Messages.MAVLinkMessage;
import com.MAVLink.Messages.MAVLinkPayload;
import com.MAVLink.common.msg_attitude;
import com.MAVLink.common.msg_heartbeat;
import com.MAVLink.common.msg_mission_clear_all;
import com.MAVLink.common.msg_mission_count;
import com.MAVLink.common.msg_mission_current;
import com.MAVLink.common.msg_mission_item;
import com.MAVLink.common.msg_scaled_pressure;
import com.MAVLink.common.msg_set_home_position;
import com.MAVLink.enums.MAV_CMD;
import com.fazecast.jSerialComm.SerialPort;
import com.telecommand.TelecommandMessage;
import com.telemetry.Heartbeat;

public class Autopilot {

	MessageHandler messageHandler=null;
	public boolean connect(MessageHandler messageHandler) {
		this.messageHandler=messageHandler;
		//initialize
		//System.out.println("start init");
		SerialPort port = init();
		//System.out.println("init finished");
		/*while(true) {
			//System.out.println("start getPacket");
			MAVLinkPacket mavpacket = getPacket(port);
			//System.out.println("finished getPacket");
			//System.out.println("start handlePacket");
			handlePacket(mavpacket);
			//System.out.println("finished handlePacket");
		}*/
		
		//########## MAVLINK SEND TEST ##########
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		System.out.println("done waiting");
		
		msg_mission_clear_all clearitem = new msg_mission_clear_all();
		MAVLinkPacket pclear = clearitem.pack();
		
		/*msg_mission_item item = new msg_mission_item();
		item.param2=10;
		item.x=12;
		item.y=12;
		item.z=12;
		MAVLinkPacket p = item.pack();*/
		try {
			port.getOutputStream().write(pclear.encodePacket());
			System.out.println("clear sent");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		msg_mission_count countitem = new msg_mission_count();
		countitem.count = 1;
		MAVLinkPacket pcount = countitem.pack();
		try {
			port.getOutputStream().write(pcount.encodePacket());
			System.out.println("mission count sent");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			Thread.sleep(30);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		msg_mission_item loiter = new msg_mission_item();
		loiter.x = 49.8187007f;
		loiter.y = 9.8934245f;
		loiter.z = 200;
		loiter.command = MAV_CMD.MAV_CMD_NAV_LOITER_UNLIM;
		MAVLinkPacket ploiter = loiter.pack();
		try {
			port.getOutputStream().write(pcount.encodePacket());
			System.out.println("loiter waypoint sent");
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
		homeitem.x = 49.8187007f;
		homeitem.y = 9.8934245f;
		homeitem.z = 200;
		MAVLinkPacket phome = homeitem.pack();
		try {
			port.getOutputStream().write(phome.encodePacket());
			System.out.println("home point sent");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//########## MAVLINK SEND TEST END ##########
		
		//port.closePort();
		return true;
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
		//MAVLinkMessage msg = cmd.getMAVLink();
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
		System.out.println(port.getDescriptivePortName()+" (Baudrate: "+port.getBaudRate()+") is open.");
		return port;
	}
	
	private static int unsignedToBytes(byte a) {
		int b = a & 0xFF;
		return b;
	}
}
