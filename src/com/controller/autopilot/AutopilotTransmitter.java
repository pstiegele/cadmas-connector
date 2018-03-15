package com.controller.autopilot;

import java.io.IOException;

import com.MAVLink.MAVLinkPacket;
import com.MAVLink.common.msg_mission_request;
import com.MAVLink.common.msg_mission_request_list;
import com.fazecast.jSerialComm.SerialPort;

public class AutopilotTransmitter extends Thread {

	private SerialPort port;

	public AutopilotTransmitter(SerialPort port) {
		this.port = port;
		start();
	}

	@Override
	public void run() {
//		try {
//			Thread.sleep(3000);
//		} catch (InterruptedException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
//		// msg_mission_item item = new msg_mission_item();
//		// item.command = MAV_CMD.MAV_CMD_GET_HOME_POSITION;
//		msg_mission_count c = new msg_mission_count();
//		c.count = 5;
//		MAVLinkPacket packet = c.pack();
//		try {
//			byte[] o = packet.encodePacket();
//			port.getOutputStream().write(packet.encodePacket());
//			System.out.println("count sent");
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//		
//		
//		
//		
//		int i = 0;
//		while (i < 5) {
//			i++;
//
//			try {
//				Thread.sleep(30);
//			} catch (InterruptedException e1) {
//				// TODO Auto-generated catch block
//				e1.printStackTrace();
//			}
//			msg_mission_item item = new msg_mission_item();
//			item.command = MAV_CMD.MAV_CMD_NAV_WAYPOINT;
//			item.x = 49.8187007f+i*0.01f;
//			item.y = 9.8132528f+i*0.01f;
//			item.z = 200f+i;
//			packet = item.pack();
//			try {
//				port.getOutputStream().write(packet.encodePacket());
//				System.out.println("waypoint "+i+" sent");
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
		
		
		
		
		
		
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		// msg_mission_item item = new msg_mission_item();
		// item.command = MAV_CMD.MAV_CMD_GET_HOME_POSITION;
		msg_mission_request_list list = new msg_mission_request_list();
		MAVLinkPacket packet = list.pack();
		packet.seq=250;
		try {
			byte[] o = packet.encodePacket();
			port.getOutputStream().write(packet.encodePacket());
			System.out.println("request list sent");
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
		// msg_mission_item item = new msg_mission_item();
		// item.command = MAV_CMD.MAV_CMD_GET_HOME_POSITION;
		msg_mission_request req1 = new msg_mission_request();
		req1.seq=0;
		packet = list.pack();
		packet.seq=251;
		try {
			byte[] o = packet.encodePacket();
			port.getOutputStream().write(packet.encodePacket());
			System.out.println("request 0 sent");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		try {
			Thread.sleep(100);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		// msg_mission_item item = new msg_mission_item();
		// item.command = MAV_CMD.MAV_CMD_GET_HOME_POSITION;
		req1 = new msg_mission_request();
		req1.seq=1;
		packet = list.pack();
		packet.seq=252;
		try {
			byte[] o = packet.encodePacket();
			port.getOutputStream().write(packet.encodePacket());
			System.out.println("request 1 sent");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		try {
			Thread.sleep(100);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		// msg_mission_item item = new msg_mission_item();
		// item.command = MAV_CMD.MAV_CMD_GET_HOME_POSITION;
		req1 = new msg_mission_request();
		req1.seq=0;
		packet = list.pack();
		packet.seq=253;
		try {
			byte[] o = packet.encodePacket();
			port.getOutputStream().write(packet.encodePacket());
			System.out.println("request 2 sent");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		try {
			Thread.sleep(100);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		// msg_mission_item item = new msg_mission_item();
		// item.command = MAV_CMD.MAV_CMD_GET_HOME_POSITION;
		req1 = new msg_mission_request();
		req1.seq=0;
		packet = list.pack();
		packet.seq=254;
		try {
			byte[] o = packet.encodePacket();
			port.getOutputStream().write(packet.encodePacket());
			System.out.println("request 3 sent");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		try {
			Thread.sleep(100);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		// msg_mission_item item = new msg_mission_item();
		// item.command = MAV_CMD.MAV_CMD_GET_HOME_POSITION;
		req1 = new msg_mission_request();
		req1.seq=255;
		packet = list.pack();
		try {
			byte[] o = packet.encodePacket();
			port.getOutputStream().write(packet.encodePacket());
			System.out.println("request 4 sent");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		
		

	}
	
	public boolean send(MAVLinkPacket packet) {
		byte[] output = packet.encodePacket();
		try {
			port.getOutputStream().write(output);
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

}
