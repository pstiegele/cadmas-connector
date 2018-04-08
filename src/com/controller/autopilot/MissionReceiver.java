package com.controller.autopilot;

import java.io.IOException;
import java.util.ArrayList;

import com.MAVLink.MAVLinkPacket;
import com.MAVLink.Parser;
import com.MAVLink.common.msg_mission_count;
import com.MAVLink.common.msg_mission_current;
import com.MAVLink.common.msg_mission_item;
import com.MAVLink.common.msg_mission_request;
import com.MAVLink.common.msg_mission_request_list;
import com.MAVLink.enums.MAV_CMD;
import com.fazecast.jSerialComm.SerialPort;

public class MissionReceiver extends Thread{
	
	private static SerialPort port;
	static int sequence;
	static ArrayList<CustomMissionItem> mission = new ArrayList<>();
	static boolean missionReceived = false;
	
	
	public MissionReceiver(SerialPort port) {
		MissionReceiver.port=port;
		start();
	}

	@Override
	public void run() {
		sequence = waitForSequence();
		
		//get mission count
		int count = sendAndWaitCount();
		System.out.println("got count: " + count + " --> mission size: " + (count-1));
		
		//loop for mission items
		for (int i = 1; i < count; i++) {
			msg_mission_item item = sendAndWaitRequest(i);
			int type = 0;
			switch(item.command){
			case MAV_CMD.MAV_CMD_NAV_LAND:
				type = -3;
				break;
			case MAV_CMD.MAV_CMD_NAV_TAKEOFF:
				type = -2;
				break;
			case MAV_CMD.MAV_CMD_NAV_WAYPOINT:
				type = 0;
				break;
			case MAV_CMD.MAV_CMD_NAV_RETURN_TO_LAUNCH:
				type = -1;
				break;
			case MAV_CMD.MAV_CMD_NAV_LOITER_TIME:
				type = (int)item.param1;
				break;
			default:
				break;
			}
			CustomMissionItem cmi = new CustomMissionItem(type, item.x, item.y, (int)item.z);
			mission.add(cmi);
			System.out.println("got item " + i);
		}
		missionReceived = true;
		//printMission(mission);
	}
	
	public static void printMission(ArrayList<CustomMissionItem> mission){
		for (int i = 0; i < mission.size(); i++) {
			System.out.println((i+1) + ": " + mission.get(i).toString());
		}
	}
	
	private static int waitForSequence(){
		while(true){
			MAVLinkPacket receivedPacket = getPacket(port);
			if(receivedPacket.msgid == msg_mission_current.MAVLINK_MSG_ID_MISSION_CURRENT){
				msg_mission_current current = new msg_mission_current(receivedPacket);
				return current.seq;
			}
		}
	}
	
	private static int sendAndWaitCount(){
		MAVLinkPacket packet;
		msg_mission_request_list list = new msg_mission_request_list();
		packet = list.pack();
		send(packet);
		long time = System.currentTimeMillis();
		while(System.currentTimeMillis()-time < 50){
			MAVLinkPacket receivedPacket = getPacket(port);
			if(receivedPacket.msgid == msg_mission_count.MAVLINK_MSG_ID_MISSION_COUNT){
				msg_mission_count count = new msg_mission_count(receivedPacket);
				return count.count;
			}
		}
		return sendAndWaitCount();
	}
	
	private static msg_mission_item sendAndWaitRequest(int sequence){
		MAVLinkPacket packet;
		msg_mission_request request = new msg_mission_request();
		request.seq = sequence;
		packet = request.pack();
		send(packet);
		long time = System.currentTimeMillis();
		while(System.currentTimeMillis()-time < 50){
			MAVLinkPacket receivedPacket = getPacket(port);
			if(receivedPacket.msgid == msg_mission_item.MAVLINK_MSG_ID_MISSION_ITEM){
				msg_mission_item item = new msg_mission_item(receivedPacket);
				return item;
			}
		}
		return sendAndWaitRequest(sequence);
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
