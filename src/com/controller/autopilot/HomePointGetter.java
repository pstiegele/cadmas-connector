package com.controller.autopilot;

import java.io.IOException;

import com.MAVLink.MAVLinkPacket;
import com.MAVLink.Parser;
import com.MAVLink.common.msg_command_long;
import com.MAVLink.common.msg_home_position;
import com.MAVLink.common.msg_mission_count;
import com.MAVLink.common.msg_mission_request_list;
import com.MAVLink.enums.MAV_CMD;
import com.fazecast.jSerialComm.SerialPort;

public class HomePointGetter extends Thread{

	private static SerialPort port;
	CustomMissionItem homePosition;
	static boolean homeReceived = false;
	
	public HomePointGetter(SerialPort port){
		this.port = port;
		start();
	}
	
	@Override
	public void run(){
		msg_home_position hp = requestAndWait();
		double lat_tmp = hp.latitude / 1E7;
		double long_tmp = hp.longitude / 1E7;
		double alt_tmp = hp.altitude / 1E3;
		homePosition = new CustomMissionItem(0, (float) lat_tmp, (float) long_tmp, (int) alt_tmp);
		homeReceived = true;
	}
	
	private static msg_home_position requestAndWait(){
		MAVLinkPacket packet;
		msg_command_long getHP = new msg_command_long();
		getHP.command = MAV_CMD.MAV_CMD_GET_HOME_POSITION;
		packet = getHP.pack();
		send(packet);
		long time = System.currentTimeMillis();
		while(System.currentTimeMillis()-time < 50){
			MAVLinkPacket receivedPacket = getPacket(port);
			if(receivedPacket.msgid == msg_home_position.MAVLINK_MSG_ID_HOME_POSITION){
				msg_home_position home = new msg_home_position(receivedPacket);
				return home;
			}
		}
		return requestAndWait();
	}
	private static MAVLinkPacket getPacket(SerialPort port) {
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

	private static int unsignedToBytes(byte a) {
		int b = a & 0xFF;
		return b;
	}
	
	public static void waitMillis(long t){
		long millis = t;
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	public static void send(MAVLinkPacket packet){
		try {
			port.getOutputStream().write(packet.encodePacket());
			//System.out.println("message sent, id: " + packet.msgid);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
