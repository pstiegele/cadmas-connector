package com.controller.autopilot;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;

import com.MAVLink.MAVLinkPacket;
import com.MAVLink.common.msg_command_ack;
import com.MAVLink.common.msg_command_long;
import com.MAVLink.common.msg_mission_clear_all;
import com.MAVLink.common.msg_mission_count;
import com.MAVLink.common.msg_mission_item;
import com.MAVLink.common.msg_mission_set_current;
import com.MAVLink.common.msg_set_mode;
import com.MAVLink.enums.MAV_CMD;
import com.MAVLink.enums.MAV_COMPONENT;
import com.MAVLink.enums.MAV_MODE_FLAG;
import com.MAVLink.enums.MAV_RESULT;
import com.fazecast.jSerialComm.SerialPort;
import com.telemetry.CommandAck;

public class AutopilotTransmitter extends Thread {
	
	boolean udpInsteadOfSerial = false;
	private SerialPort port;
	
	boolean inUse = true;

	public AutopilotTransmitter(SerialPort port) {
		//udpInsteadOfSerial = true;
		
		this.port = port;
		start();
	}

	@Override
	public void run(){
		waitMillis(5000);
		inUse = false;

		//INSERT COMMANDS HERE
		System.out.println("start");
		waitMillis(2000);
		
	}
	
	public int setMode(int mode) throws UnknownHostException, SocketException{
//		MANUAL        = 0
//	    CIRCLE        = 1
//	    STABILIZE     = 2
//	    TRAINING      = 3
//	    ACRO          = 4
//	    FLY_BY_WIRE_A = 5
//	    FLY_BY_WIRE_B = 6
//	    CRUISE        = 7
//	    AUTOTUNE      = 8
//	    AUTO          = 10
//	    RTL           = 11
//	    LOITER        = 12
//	    AVOID_ADSB    = 14
//	    GUIDED        = 15
//	    INITIALISING  = 16
//	    QSTABILIZE    = 17
//	    QHOVER        = 18
//	    QLOITER       = 19
//	    QLAND         = 20
//	    QRTL          = 21
		msg_set_mode changeMode = new msg_set_mode();
		changeMode.base_mode = MAV_MODE_FLAG.MAV_MODE_FLAG_CUSTOM_MODE_ENABLED;
		changeMode.custom_mode = mode;
		int attempts = 3;
		int maxTime = 500;
		for(int i = 0; i < attempts; i++){
			int arraySize = CommandAck.getMessageMemory().size();
			long sendTime = System.currentTimeMillis();
			send(changeMode.pack());
			while(System.currentTimeMillis() - sendTime < maxTime){
				waitMillis(20);
				if(CommandAck.getMessageMemory().size() > arraySize){
					if(CommandAck.getMessageMemory().get(arraySize).getCommand() == msg_set_mode.MAVLINK_MSG_ID_SET_MODE){
						if(CommandAck.getMessageMemory().get(arraySize).getResult() == MAV_RESULT.MAV_RESULT_ACCEPTED){
							return MAV_RESULT.MAV_RESULT_ACCEPTED;
						}
						else if(i == 2){
							return CommandAck.getMessageMemory().get(arraySize).getResult();
						}
					}
					if(CommandAck.getMessageMemory().size() > arraySize){
						arraySize += 1;
					}
				}
			}
		}
		return MAV_RESULT.MAV_RESULT_FAILED;
	}
	
	//PREFLIGHT CALIBRATION (airspeed + baro)
	public void calibrate() throws UnknownHostException, SocketException{
		inUse = true;
		msg_command_long cali;
		
		//ground pressure
		cali = new msg_command_long();
		cali.command = MAV_CMD.MAV_CMD_PREFLIGHT_CALIBRATION;
		cali.param3 = 1;
		send(cali.pack());
		
		waitMillis(100);
		//airspeed
		cali = new msg_command_long();
		cali.command = MAV_CMD.MAV_CMD_PREFLIGHT_CALIBRATION;
		cali.param6 = 2;
		//send(cali.pack());
		
		waitMillis(100);
		//barometer temperature
		cali = new msg_command_long();
		cali.command = MAV_CMD.MAV_CMD_PREFLIGHT_CALIBRATION;
		cali.param7 = 3;
		send(cali.pack());
		
		waitMillis(1000);
		//stop calibration
		cali = new msg_command_long();
		cali.command = MAV_CMD.MAV_CMD_PREFLIGHT_CALIBRATION;
		cali.param3 = 0;
		cali.param6 = 0;
		cali.param7 = 0;
		//send(cali.pack());
		
		inUse = false;
	}
	
	public int arm() throws UnknownHostException, SocketException{
		inUse = true;
		msg_command_long arm = new msg_command_long();
		arm.command = MAV_CMD.MAV_CMD_COMPONENT_ARM_DISARM;
		arm.param1 = 1;
		int attempts = 3;
		int maxTime = 500;
		for(int i = 0; i < attempts; i++){
			int arraySize = CommandAck.getMessageMemory().size();
			long sendTime = System.currentTimeMillis();
			send(arm.pack());
			while(System.currentTimeMillis() - sendTime < maxTime){
				waitMillis(20);
				if(CommandAck.getMessageMemory().size() > arraySize){
					if(CommandAck.getMessageMemory().get(arraySize).getCommand() == MAV_CMD.MAV_CMD_COMPONENT_ARM_DISARM){
						if(CommandAck.getMessageMemory().get(arraySize).getResult() == MAV_RESULT.MAV_RESULT_ACCEPTED){
							return MAV_RESULT.MAV_RESULT_ACCEPTED;
						}
						else if(i == 2){
							return CommandAck.getMessageMemory().get(arraySize).getResult();
						}
					}
					if(CommandAck.getMessageMemory().size() > arraySize){
						arraySize += 1;
					}
				}
			}
		}
		return MAV_RESULT.MAV_RESULT_FAILED;
	}
	
	public int disarm() throws UnknownHostException, SocketException{
		inUse = true;
		msg_command_long disarm = new msg_command_long();
		disarm.command = MAV_CMD.MAV_CMD_COMPONENT_ARM_DISARM;
		disarm.param1 = 0;
		int attempts = 3;
		int maxTime = 500;
		for(int i = 0; i < attempts; i++){
			int arraySize = CommandAck.getMessageMemory().size();
			long sendTime = System.currentTimeMillis();
			send(disarm.pack());
			while(System.currentTimeMillis() - sendTime < maxTime){
				waitMillis(20);
				if(CommandAck.getMessageMemory().size() > arraySize){
					if(CommandAck.getMessageMemory().get(arraySize).getCommand() == MAV_CMD.MAV_CMD_COMPONENT_ARM_DISARM){
						if(CommandAck.getMessageMemory().get(arraySize).getResult() == MAV_RESULT.MAV_RESULT_ACCEPTED){
							return MAV_RESULT.MAV_RESULT_ACCEPTED;
						}
						else if(i == 2){
							return CommandAck.getMessageMemory().get(arraySize).getResult();
						}
					}
					if(CommandAck.getMessageMemory().size() > arraySize){
						arraySize += 1;
					}
				}
			}
		}
		return MAV_RESULT.MAV_RESULT_FAILED;
	}
	
	public void flyToHere(CustomMissionItem item) throws UnknownHostException, SocketException{
		MAVLinkPacket packet;
		//get current mission
		MissionGetter missionGetter = new MissionGetter(port);
		boolean x = missionGetter.missionReceived;
		while(!x){
			waitMillis(20);
			x = missionGetter.missionReceived;
		}
		ArrayList<CustomMissionItem> currentMission = missionGetter.mission;
		
		//get current active mission sequence
		int currentSequence = missionGetter.sequence;
		
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
	
	public void returnToLaunch() throws UnknownHostException, SocketException{
		inUse = true;
		ArrayList<CustomMissionItem> mission = new ArrayList<>();
		CustomMissionItem rtl = new CustomMissionItem(-1, 0, 0, 0);
		mission.add(rtl);
		sendMission(mission);
		inUse = false;
	}
	
	public CustomMissionItem getHomePosition(){
		HomePointGetter hpg = new HomePointGetter(port);
		boolean x = hpg.homeReceived;
		while(!x){
			waitMillis(20);
			x = hpg.homeReceived;
		}
		return hpg.homePosition;
	}
	
	public void setHomePosition(CustomMissionItem hp) throws UnknownHostException, SocketException{
		msg_command_long setHome = new msg_command_long();
		setHome.command = MAV_CMD.MAV_CMD_DO_SET_HOME;
		setHome.param1 = 0;
		setHome.param5 = hp.latitude;
		setHome.param6 = hp.longitude;
		setHome.param7 = hp.altitude;
		send(setHome.pack());
	}
	
	public void udpTest(int port) throws UnknownHostException, SocketException{
		msg_mission_clear_all clear = new msg_mission_clear_all();
		MAVLinkPacket clearPacket = clear.pack();
		int p = port;
		//InetAddress ipAdress = InetAddress.getByName("127.0.0.1");
		InetAddress ipAdress = InetAddress.getByName("192.168.178.40");
		//InetAddress ipAdress = InetAddress.getByName("192.168.178.45");
		DatagramSocket dSocket = new DatagramSocket(p);
		DatagramPacket sendPacket = new DatagramPacket(clearPacket.encodePacket(), clearPacket.encodePacket().length, ipAdress, p);
		try {
			dSocket.send(sendPacket);
			dSocket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//System.out.println("clear sent");
	}
	
	public void sendMission(ArrayList<CustomMissionItem> mission) throws UnknownHostException, SocketException{
		int missionCount = mission.size();
		MAVLinkPacket packet;
		msg_mission_item item;
		
		//CLEAR PREVIOUS MISSION
		waitMillis(50);
		msg_mission_clear_all clear = new msg_mission_clear_all();
		packet = clear.pack();
		send(packet);
		System.out.println("old mission cleared");
		
		//SEND MISSION COUNT
		waitMillis(50);
		msg_mission_count count = new msg_mission_count();
		count.count = missionCount + 1;
		packet = count.pack();
		send(packet);
		System.out.println("mission count sent");
		
		//SEND EMPTY MISSION ITEM
		waitMillis(50);
		item = new msg_mission_item();
		item.command = MAV_CMD.MAV_CMD_NAV_WAYPOINT;
		item.seq = 0;
		packet = item.pack();
		send(packet);
		System.out.println("empty mission item sent");
		
		//SEND MISSION ITEMS
		for (int i = 0; i < missionCount; i++) {
			waitMillis(20);
			item = new msg_mission_item();
			item.seq = i + 1;
			item.x = mission.get(i).latitude;
			item.y = mission.get(i).longitude;
			item.z = mission.get(i).altitude;
			switch(mission.get(i).type){
			case -3:
				item.command = MAV_CMD.MAV_CMD_NAV_LAND;
				item.param1 = 10; //abort altitude in meters
			case -2:
				item.command = MAV_CMD.MAV_CMD_NAV_TAKEOFF;
				item.param1 = 10; //pitch angle in degrees
				break;
			case -1:
				item.command = MAV_CMD.MAV_CMD_NAV_RETURN_TO_LAUNCH;
				break;
			case 0:
				item.command = MAV_CMD.MAV_CMD_NAV_WAYPOINT;
				break;
			default:
				item.command = MAV_CMD.MAV_CMD_NAV_LOITER_TIME;
				item.param1 = mission.get(i).type; //loiter time in seconds
				item.param3 = 50; //loiter radius in meters
				break;
			}
			packet = item.pack();
			send(packet);
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
		send(packet);
		System.out.println("mission cleared");
	}
	
	public void send(MAVLinkPacket packet) throws UnknownHostException, SocketException{
		//System.out.println("sending message");
		if(udpInsteadOfSerial){
			sendUDP(packet);
		}
		else{
			sendSerial(packet);
		}
	}
	
	public void sendSerial(MAVLinkPacket packet){
		try {
			port.getOutputStream().write(packet.encodePacket());
			//System.out.println("message sent, id: " + packet.msgid);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void sendUDP(MAVLinkPacket packet) throws UnknownHostException, SocketException{
		int port = 14550;
		InetAddress ipAdress;
		//ipAdress = InetAddress.getByName("127.0.0.1");
		//ipAdress = InetAddress.getByName("10.0.2.2");
		ipAdress = InetAddress.getByName("192.168.178.58");
		DatagramSocket dSocket = new DatagramSocket(port);
		DatagramPacket sendPacket = new DatagramPacket(packet.encodePacket(), packet.encodePacket().length, ipAdress, port);
		try {
			dSocket.send(sendPacket);
			System.out.println("sent to " + ipAdress + ":" + port);
			dSocket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
