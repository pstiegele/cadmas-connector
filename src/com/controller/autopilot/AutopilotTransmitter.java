package com.controller.autopilot;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;

import com.MAVLink.MAVLinkPacket;
import com.MAVLink.common.msg_command_long;
import com.MAVLink.common.msg_home_position;
import com.MAVLink.common.msg_mission_clear_all;
import com.MAVLink.common.msg_mission_count;
import com.MAVLink.common.msg_mission_item;
import com.MAVLink.common.msg_mission_request;
import com.MAVLink.common.msg_mission_request_list;
import com.MAVLink.common.msg_mission_set_current;
import com.MAVLink.common.msg_set_mode;
import com.MAVLink.enums.MAV_CMD;
import com.MAVLink.enums.MAV_MODE_FLAG;
import com.MAVLink.enums.MAV_RESULT;
import com.fazecast.jSerialComm.SerialPort;
import com.telemetry.CommandAck;
import com.telemetry.Heartbeat;
import com.telemetry.MissionItem;
import com.telemetry.MissionState;
import tools.Settings;

public class AutopilotTransmitter extends Thread {
	
	boolean udpInsteadOfSerial = Settings.getInstance().getUseUDP();
	private SerialPort port;
	
	AutopilotTransmitter(SerialPort port) {
		//udpInsteadOfSerial = true;
		
		this.port = port;
		start();
		
	}

	@Override
	public void run(){
		waitMillis(5000);

		//INSERT COMMANDS HERE
		System.out.println("transmitterStart");
		waitMillis(2000);
		
	}
	
	public int setMode(int mode) throws UnknownHostException, SocketException{
		//use com.controller.autopilot.FlightModes
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
	public int calibrate() throws UnknownHostException, SocketException{
		msg_command_long cali;
		
		//ground pressure
		cali = new msg_command_long();
		cali.command = MAV_CMD.MAV_CMD_PREFLIGHT_CALIBRATION;
		cali.param3 = 1;
		int attempts = 3;
		int maxTime = 10000;
		for(int i = 0; i < attempts; i++){
			int arraySize = CommandAck.getMessageMemory().size();
			long sendTime = System.currentTimeMillis();
			send(cali.pack());
			while(System.currentTimeMillis() - sendTime < maxTime){
				waitMillis(20);
				if(CommandAck.getMessageMemory().size() > arraySize){
					if(CommandAck.getMessageMemory().get(arraySize).getCommand() == MAV_CMD.MAV_CMD_PREFLIGHT_CALIBRATION){
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
	
	public int arm() throws UnknownHostException, SocketException{
		
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
	
	public int setHomePosition(CustomMissionItem hp) throws UnknownHostException, SocketException{
		if(!Heartbeat.getMessageMemory().getNewestElement().getArmedState()) {
			return MAV_RESULT.MAV_RESULT_DENIED;
		}
		msg_command_long setHome = new msg_command_long();
		setHome.command = MAV_CMD.MAV_CMD_DO_SET_HOME;
		setHome.param1 = 0;
		setHome.param5 = hp.latitude;
		setHome.param6 = hp.longitude;
		setHome.param7 = hp.altitude;
		int attempts = 3;
		int maxTime = 500;
		for(int i = 0; i < attempts; i++){
			int arraySize = MissionItem.getMessageMemory().size();
			long sendTime = System.currentTimeMillis();
			send(setHome.pack());
			while(System.currentTimeMillis() - sendTime < maxTime){
				waitMillis(20);
				if(MissionItem.getMessageMemory().size() > arraySize){
					if(MissionItem.getMessageMemory().get(arraySize).getCommand() == msg_home_position.MAVLINK_MSG_ID_HOME_POSITION){
						if(MissionItem.getMessageMemory().get(arraySize).getMissionItem().latitude == hp.latitude && MissionItem.getMessageMemory().get(arraySize).getMissionItem().longitude == hp.longitude && MissionItem.getMessageMemory().get(arraySize).getMissionItem().altitude == hp.altitude){
							return MAV_RESULT.MAV_RESULT_ACCEPTED;
						}
						else if(i == 2){
							return MissionItem.getMessageMemory().get(arraySize).getResult();
						}
					}
					if(MissionItem.getMessageMemory().size() > arraySize){
						arraySize += 1;
					}
				}
			}
		}
		return MAV_RESULT.MAV_RESULT_FAILED;
	}
	
	public CustomMissionItem getHomePosition() throws UnknownHostException, SocketException{
		CustomMissionItem failed = new CustomMissionItem(MAV_RESULT.MAV_RESULT_FAILED, 0, 0, 0);
		msg_command_long getHome = new msg_command_long();
		getHome.command = MAV_CMD.MAV_CMD_GET_HOME_POSITION;
		int attempts = 3;
		int maxTime = 500;
		for(int i = 0; i < attempts; i++){
			int arraySize = MissionItem.getMessageMemory().size();
			long sendTime = System.currentTimeMillis();
			send(getHome.pack());
			while(System.currentTimeMillis() - sendTime < maxTime){
				waitMillis(20);
				if(MissionItem.getMessageMemory().size() > arraySize){
					if(MissionItem.getMessageMemory().get(arraySize).getCommand() == msg_home_position.MAVLINK_MSG_ID_HOME_POSITION){
						if(MissionItem.getMessageMemory().get(arraySize).getResult() == MAV_RESULT.MAV_RESULT_ACCEPTED){
							return MissionItem.getMessageMemory().get(arraySize).getMissionItem();
						}
						else if(i == 2){
							return failed;
						}
					}
					if(MissionItem.getMessageMemory().size() > arraySize){
						arraySize += 1;
					}
				}
			}
		}
		return failed;
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
	
	public int sendMission(ArrayList<CustomMissionItem> mission, boolean restart) throws UnknownHostException, SocketException{
		if(mission.get(0).type == MAV_RESULT.MAV_RESULT_FAILED) {
			return MAV_RESULT.MAV_RESULT_FAILED;
		}
		int currentSequence = getSequence();
		if(currentSequence == MAV_RESULT.MAV_RESULT_FAILED){
			return MAV_RESULT.MAV_RESULT_FAILED;
		}
		if(restart || currentSequence >= mission.size()){
			currentSequence = 0;
		}
		
		int missionCount = mission.size();
		msg_mission_item item;
		
		//CLEAR PREVIOUS MISSION
		waitMillis(50);
		msg_mission_clear_all clear = new msg_mission_clear_all();
		send(clear.pack());
		//System.out.println("old mission cleared");
		
		//SEND MISSION COUNT
		waitMillis(50);
		msg_mission_count count = new msg_mission_count();
		count.count = missionCount + 1;
		send(count.pack());
		//System.out.println("mission count sent");
		
		//SEND EMPTY MISSION ITEM
		waitMillis(50);
		item = new msg_mission_item();
		item.command = MAV_CMD.MAV_CMD_NAV_WAYPOINT;
		item.seq = 0;
		send(item.pack());
		//System.out.println("empty mission item sent");
		
		//SEND MISSION ITEMS
		for (int i = 0; i < missionCount; i++) {
			waitMillis(20);
			item = new msg_mission_item();
			item.seq = i + 1;
			item.frame = Settings.getInstance().getFrameOrientation();
			item.x = mission.get(i).latitude;
			item.y = mission.get(i).longitude;
			item.z = mission.get(i).altitude;
			switch(mission.get(i).type){
			case MissionItemTypes.LAND:
				item.command = MAV_CMD.MAV_CMD_NAV_LAND;
				item.param1 = Settings.getInstance().getAbortAltitude(); //abort altitude in meters
			case MissionItemTypes.TAKEOFF:
				item.command = MAV_CMD.MAV_CMD_NAV_TAKEOFF;
				item.param1 = Settings.getInstance().getTakeOffPitch(); //pitch angle in degrees
				break;
			case MissionItemTypes.RTL:
				item.command = MAV_CMD.MAV_CMD_NAV_RETURN_TO_LAUNCH;
				break;
			case MissionItemTypes.WAYPOINT:
				item.command = MAV_CMD.MAV_CMD_NAV_WAYPOINT;
				break;
			default:
				item.command = MAV_CMD.MAV_CMD_NAV_LOITER_TIME;
				item.param1 = mission.get(i).type; //loiter time in seconds
				item.param3 = Settings.getInstance().getLoiterRadius(); //loiter radius in meters
				break;
			}
			send(item.pack());
			//System.out.println("mission item " + (i+1) + " sent");
		}
		
		ArrayList<CustomMissionItem> verification = getMission();
		
		if(verification.get(0).type == MAV_RESULT.MAV_RESULT_FAILED){
			return MAV_RESULT.MAV_RESULT_FAILED;
		}
		
		for(int i = 0; i < verification.size(); i++){
			if(!verification.get(i).equals(mission.get(i))){
				System.out.println("fail at " + i + ": " + mission.get(i).toString() + " ### " + verification.get(i).toString());
				return MAV_RESULT.MAV_RESULT_FAILED;
			}
		}
		
		//SEND SEQUENCE
		if(setSequence(currentSequence) != MAV_RESULT.MAV_RESULT_ACCEPTED){
			return MAV_RESULT.MAV_RESULT_FAILED;
		}
		return MAV_RESULT.MAV_RESULT_ACCEPTED;
	}
	
	public int setSequence(int sequence) throws UnknownHostException, SocketException{
		msg_mission_set_current current = new msg_mission_set_current();
		current.seq = sequence;
		int attempts = 3;
		int maxTime = 500;
		for(int i = 0; i < attempts; i++){
			int arraySize = MissionState.getMessageMemory().size();
			long sendTime = System.currentTimeMillis();
			send(current.pack());
			while(System.currentTimeMillis() - sendTime < maxTime){
				waitMillis(20);
				if(MissionState.getMessageMemory().size() > arraySize){
					if(MissionState.getMessageMemory().get(arraySize).getCurrentSequence() == sequence){
						return MAV_RESULT.MAV_RESULT_ACCEPTED;
					}
					if(MissionState.getMessageMemory().size() > arraySize){
						arraySize += 1;
					}
				}
			}
		}
		return MAV_RESULT.MAV_RESULT_FAILED;
	}
	
	public int getSequence(){
		int timeOut = 3000;
		long start = System.currentTimeMillis();
		while(MissionState.getMessageMemory().size() == 0){
			if(System.currentTimeMillis() - start > timeOut){
				return MAV_RESULT.MAV_RESULT_FAILED;
			}
		}
		return MissionState.getMessageMemory().getNewestElement().getCurrentSequence();
	}
	
	public ArrayList<CustomMissionItem> getMission() throws UnknownHostException, SocketException{
		ArrayList<CustomMissionItem> failed = new ArrayList<>();
		failed.add(new CustomMissionItem(MAV_RESULT.MAV_RESULT_FAILED, 0, 0, 0));
		
		ArrayList<CustomMissionItem> mission = new ArrayList<>();
		
		//send request_list
		int missionCount = getMissionCount();
		if(missionCount > 0){
			return failed;
		}
		missionCount /= -1;
		
		//send request_mission_n
		CustomMissionItem item;
		for(int i = 1; i < missionCount; i++){
			item = getMissionItem(i);
			if(item.type == MAV_RESULT.MAV_RESULT_FAILED){
				return failed;
			}
			mission.add(item);
		}
		return mission;
	}
	
	public CustomMissionItem getMissionItem(int sequence) throws UnknownHostException, SocketException{
		CustomMissionItem failed = new CustomMissionItem(MAV_RESULT.MAV_RESULT_FAILED, 0, 0, 0);
		msg_mission_request request = new msg_mission_request();
		request.seq = sequence;
		int attempts = 50;
		int maxTime = 300;
		for(int i = 0; i < attempts; i++){
			int arraySize = MissionItem.getMessageMemory().size();
			long sendTime = System.currentTimeMillis();
			send(request.pack());
			while(System.currentTimeMillis() - sendTime < maxTime){
				waitMillis(20);
				if(MissionItem.getMessageMemory().size() > arraySize){
					if(MissionItem.getMessageMemory().get(arraySize).getCommand() == msg_mission_item.MAVLINK_MSG_ID_MISSION_ITEM){
						if(MissionItem.getMessageMemory().get(arraySize).getResult() == sequence){
							return MissionItem.getMessageMemory().get(arraySize).getMissionItem();
						}
					}
					if(MissionItem.getMessageMemory().size() > arraySize){
						arraySize += 1;
					}
				}
			}
		}
		return failed;
	}
	
	public int getMissionCount() throws UnknownHostException, SocketException{
		msg_mission_request_list list = new msg_mission_request_list();
		int attempts = 3;
		int maxTime = 500;
		for(int i = 0; i < attempts; i++){
			int arraySize = CommandAck.getMessageMemory().size();
			long sendTime = System.currentTimeMillis();
			send(list.pack());
			while(System.currentTimeMillis() - sendTime < maxTime){
				waitMillis(20);
				if(CommandAck.getMessageMemory().size() > arraySize){
					if(CommandAck.getMessageMemory().get(arraySize).getCommand() == msg_mission_count.MAVLINK_MSG_ID_MISSION_COUNT){
						if(CommandAck.getMessageMemory().get(arraySize).getResult() < 0){
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
	
	public void waitMillis(long t){
		try {
			Thread.sleep(t);
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
