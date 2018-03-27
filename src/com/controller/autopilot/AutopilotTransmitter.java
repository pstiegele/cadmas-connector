package com.controller.autopilot;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
<<<<<<< HEAD
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;

import com.MAVLink.MAVLinkPacket;
import com.MAVLink.common.msg_mission_clear_all;
import com.MAVLink.common.msg_mission_count;
import com.MAVLink.common.msg_mission_item;
import com.MAVLink.common.msg_mission_set_current;
import com.MAVLink.enums.MAV_CMD;
import com.fazecast.jSerialComm.SerialPort;
=======

import com.MAVLink.MAVLinkPacket;
>>>>>>> origin/master

public class AutopilotTransmitter extends Thread {

	private OutputStream out;

	public AutopilotTransmitter(OutputStream out) {
		this.out = out;
		start();
	}

	@Override
<<<<<<< HEAD
	public void run(){
		waitMillis(1500);
		long startTime = System.currentTimeMillis();
		//ArrayList<CustomMissionItem> mission = generateFromCSV();
		ArrayList<CustomMissionItem> mission = randomMissionGenerator(3, -35f, 150f, 100, 200);
		try {
			while(true){
				waitMillis(100);
				sendMission(mission);
			}
		} catch (UnknownHostException | SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		/*while(true){
			waitMillis(100);
			try {
				clearMission();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}*/
		
		//flyToHere(new CustomMissionItem(45, 49.789f, 10.456f, 123));
		//System.out.println("COMMAND DURATION: " + (System.currentTimeMillis()-startTime));
	}
	
	public void flyToHere(CustomMissionItem item) throws UnknownHostException, SocketException{
		MAVLinkPacket packet;
		//get current mission
		MissionReceiver missionReceiver = new MissionReceiver(port);
		boolean x = missionReceiver.missionReceived;
		while(!x){
			waitMillis(20);
			x = missionReceiver.missionReceived;
		}
		ArrayList<CustomMissionItem> currentMission = missionReceiver.mission;
		
		//get current active mission sequence
		int currentSequence = missionReceiver.sequence;
		
		//create new mission with cmi at sequence position
		ArrayList<CustomMissionItem> newMission = new ArrayList<>(currentMission);
		newMission.add(currentSequence, item);
		sendMission(newMission);
		
		//set current active mission sequence to sequence(in case of a change since new mission creation)
		msg_mission_set_current current = new msg_mission_set_current();
		current.seq = currentSequence;
		packet = current.pack();
		send(packet);
	}
	
	public void sendMission(ArrayList<CustomMissionItem> mission) throws UnknownHostException, SocketException{
		int missionCount = mission.size();
		MAVLinkPacket packet;
		msg_mission_item item;
		
		//CLEAR PREVIOUS MISSION
		waitMillis(50);
		msg_mission_clear_all clear = new msg_mission_clear_all();
		packet = clear.pack();
		//send(packet);
		sendUDP(packet);
		System.out.println("old mission cleared");
		
		//SEND MISSION COUNT
		waitMillis(50);
		msg_mission_count count = new msg_mission_count();
		count.count = missionCount + 1;
		packet = count.pack();
		//send(packet);
		sendUDP(packet);
		System.out.println("mission count sent");
		
		//SEND EMPTY MISSION ITEM
		waitMillis(50);
		item = new msg_mission_item();
		item.command = MAV_CMD.MAV_CMD_NAV_WAYPOINT;
		item.seq = 0;
		packet = item.pack();
		//send(packet);
		sendUDP(packet);
		System.out.println("empty mission item sent");
		
		//SEND MISSION ITEMS
		for (int i = 0; i < missionCount; i++) {
			waitMillis(20);
			item = new msg_mission_item();
			item.seq = i + 1;
			item.x = mission.get(i).latitude;
			item.y = mission.get(i).longitude;
			item.z = mission.get(i).height;
			switch(mission.get(i).type){
			case -1:
				item.command = MAV_CMD.MAV_CMD_NAV_RETURN_TO_LAUNCH;
				break;
			case 0:
				item.command = MAV_CMD.MAV_CMD_NAV_WAYPOINT;
				break;
			default:
				item.command = MAV_CMD.MAV_CMD_NAV_LOITER_TIME;
				item.param1 = mission.get(i).type; //loiter time in seconds
				break;
			}
			packet = item.pack();
			//send(packet);
			sendUDP(packet);
			System.out.println("mission item " + (i+1) + " sent");
		}
		System.out.println("mission sent");
	}
	
	public void waitMillis(long t){
		long millis = t;
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	public void clearMission() throws IOException{
		msg_mission_clear_all clear = new msg_mission_clear_all();
		MAVLinkPacket packet = clear.pack();
		//send(packet);
		sendUDP(packet);
		System.out.println("mission cleared");
	}
	
	public void send(MAVLinkPacket packet){
		try {
			port.getOutputStream().write(packet.encodePacket());
			//System.out.println("message sent, id: " + packet.msgid);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void sendUDP(MAVLinkPacket packet) throws UnknownHostException, SocketException{
		int port = 14557;
		InetAddress ipAdress = InetAddress.getByName("127.0.0.1");
		DatagramSocket dSocket = new DatagramSocket(port);
		DatagramPacket sendPacket = new DatagramPacket(packet.encodePacket(), packet.encodePacket().length, ipAdress, port);
		try {
			dSocket.send(sendPacket);
			dSocket.close();
=======
	public void run() {
////		try {
////			Thread.sleep(3000);
////		} catch (InterruptedException e1) {
////			// TODO Auto-generated catch block
////			e1.printStackTrace();
////		}
////		// msg_mission_item item = new msg_mission_item();
////		// item.command = MAV_CMD.MAV_CMD_GET_HOME_POSITION;
////		msg_mission_count c = new msg_mission_count();
////		c.count = 5;
////		MAVLinkPacket packet = c.pack();
////		try {
////			byte[] o = packet.encodePacket();
////			port.getOutputStream().write(packet.encodePacket());
////			System.out.println("count sent");
////		} catch (IOException e) {
////			// TODO Auto-generated catch block
////			e.printStackTrace();
////		}
////
////		
////		
////		
////		
////		int i = 0;
////		while (i < 5) {
////			i++;
////
////			try {
////				Thread.sleep(30);
////			} catch (InterruptedException e1) {
////				// TODO Auto-generated catch block
////				e1.printStackTrace();
////			}
////			msg_mission_item item = new msg_mission_item();
////			item.command = MAV_CMD.MAV_CMD_NAV_WAYPOINT;
////			item.x = 49.8187007f+i*0.01f;
////			item.y = 9.8132528f+i*0.01f;
////			item.z = 200f+i;
////			packet = item.pack();
////			try {
////				port.getOutputStream().write(packet.encodePacket());
////				System.out.println("waypoint "+i+" sent");
////			} catch (IOException e) {
////				// TODO Auto-generated catch block
////				e.printStackTrace();
////			}
////		}
//		
//		
//		
//		
//		
//		
//		try {
//			Thread.sleep(3000);
//		} catch (InterruptedException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
//		// msg_mission_item item = new msg_mission_item();
//		// item.command = MAV_CMD.MAV_CMD_GET_HOME_POSITION;
//		msg_mission_request_list list = new msg_mission_request_list();
//		MAVLinkPacket packet = list.pack();
//		packet.seq=250;
//		try {
//			byte[] o = packet.encodePacket();
//			port.getOutputStream().write(packet.encodePacket());
//			System.out.println("request list sent");
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//		
//		
//		try {
//			Thread.sleep(1000);
//		} catch (InterruptedException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
//		// msg_mission_item item = new msg_mission_item();
//		// item.command = MAV_CMD.MAV_CMD_GET_HOME_POSITION;
//		msg_mission_request req1 = new msg_mission_request();
//		req1.seq=0;
//		packet = list.pack();
//		packet.seq=251;
//		try {
//			byte[] o = packet.encodePacket();
//			port.getOutputStream().write(packet.encodePacket());
//			System.out.println("request 0 sent");
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//		
//		try {
//			Thread.sleep(100);
//		} catch (InterruptedException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
//		// msg_mission_item item = new msg_mission_item();
//		// item.command = MAV_CMD.MAV_CMD_GET_HOME_POSITION;
//		req1 = new msg_mission_request();
//		req1.seq=1;
//		packet = list.pack();
//		packet.seq=252;
//		try {
//			byte[] o = packet.encodePacket();
//			port.getOutputStream().write(packet.encodePacket());
//			System.out.println("request 1 sent");
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//		
//		try {
//			Thread.sleep(100);
//		} catch (InterruptedException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
//		// msg_mission_item item = new msg_mission_item();
//		// item.command = MAV_CMD.MAV_CMD_GET_HOME_POSITION;
//		req1 = new msg_mission_request();
//		req1.seq=0;
//		packet = list.pack();
//		packet.seq=253;
//		try {
//			byte[] o = packet.encodePacket();
//			port.getOutputStream().write(packet.encodePacket());
//			System.out.println("request 2 sent");
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//		
//		try {
//			Thread.sleep(100);
//		} catch (InterruptedException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
//		// msg_mission_item item = new msg_mission_item();
//		// item.command = MAV_CMD.MAV_CMD_GET_HOME_POSITION;
//		req1 = new msg_mission_request();
//		req1.seq=0;
//		packet = list.pack();
//		packet.seq=254;
//		try {
//			byte[] o = packet.encodePacket();
//			port.getOutputStream().write(packet.encodePacket());
//			System.out.println("request 3 sent");
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//		
//		try {
//			Thread.sleep(100);
//		} catch (InterruptedException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
//		// msg_mission_item item = new msg_mission_item();
//		// item.command = MAV_CMD.MAV_CMD_GET_HOME_POSITION;
//		req1 = new msg_mission_request();
//		req1.seq=255;
//		packet = list.pack();
//		try {
//			byte[] o = packet.encodePacket();
//			port.getOutputStream().write(packet.encodePacket());
//			System.out.println("request 4 sent");
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//		
//		
		
		

	}
	
	public boolean send(MAVLinkPacket packet) {
		byte[] output = packet.encodePacket();
		try {
			out.write(output);
			return true;
>>>>>>> origin/master
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public void sendUDP2(MAVLinkPacket packet) throws IOException{
		int port = 14551;
		InetAddress ipAdress = InetAddress.getByName("127.0.0.1");
		Socket socket = new Socket(ipAdress, port);
		OutputStream sOutputStream = socket.getOutputStream();
		sOutputStream.write(packet.encodePacket());
	}
	
	public ArrayList<CustomMissionItem> randomMissionGenerator(int size, float baseLat, float baseLong, int minAlt, int maxAlt){
		float variance = 0.1f; // in degrees
		CustomMissionItem item;
		ArrayList<CustomMissionItem> mission = new ArrayList<>();
		for (int i = 0; i < size; i++) {
			item = new CustomMissionItem(0, baseLat-variance/2+(float)(Math.random()*variance), baseLong-variance/2+(float)(Math.random()*variance), minAlt+(int)(Math.random()*(maxAlt-minAlt)));
			mission.add(item);
		}
		System.out.println("mission with " + size + " items generated");
		return mission;
	}
	
	public ArrayList<CustomMissionItem> generateFromCSV(){
		CustomMissionItem item;
		ArrayList<CustomMissionItem> mission = new ArrayList<>();
		File file = new File("resources/mission1.txt");
        try {
            BufferedReader FileReader = new BufferedReader(new FileReader(file));
           
            String line="";
           
            while(null!=(line=FileReader.readLine())){
                String[] split = line.split(";");
                item = new CustomMissionItem(Integer.parseInt(split[1]), Float.parseFloat(split[2]), Float.parseFloat(split[3]), Integer.parseInt(split[4]));
                mission.add(item);
            }
            FileReader.close();
           
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("waypoint file read");
		return mission;
    }

}
