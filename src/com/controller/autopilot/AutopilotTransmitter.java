package com.controller.autopilot;

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

		this.port = port;
		start();

	}

	@Override
	public void run() {
		waitMillis(5000);
		System.out.println("transmitterStart");
		
	}

	//changes flight mode of UAV
	public int setMode(int mode) throws UnknownHostException, SocketException {
		msg_set_mode changeMode = new msg_set_mode();
		changeMode.base_mode = MAV_MODE_FLAG.MAV_MODE_FLAG_CUSTOM_MODE_ENABLED;
		changeMode.custom_mode = mode;
		int attempts = 3;
		int maxTime = 500;
		for (int i = 0; i < attempts; i++) {
			int arraySize = CommandAck.getMessageMemory().size();
			long sendTime = System.currentTimeMillis();
			send(changeMode.pack());
			while (System.currentTimeMillis() - sendTime < maxTime) {
				waitMillis(20);
				if (CommandAck.getMessageMemory().size() > arraySize) {
					if (CommandAck.getMessageMemory().get(arraySize)
							.getCommand() == msg_set_mode.MAVLINK_MSG_ID_SET_MODE) {
						if (CommandAck.getMessageMemory().get(arraySize)
								.getResult() == MAV_RESULT.MAV_RESULT_ACCEPTED) {
							return MAV_RESULT.MAV_RESULT_ACCEPTED;
						} else if (i == attempts - 1) {
							return CommandAck.getMessageMemory().get(arraySize).getResult();
						}
					}
					arraySize += 1;
				}
			}
		}
		return MAV_RESULT.MAV_RESULT_FAILED;
	}

	//performs a preflight calibration
	public int calibrate() throws UnknownHostException, SocketException {
		msg_command_long cali;
		cali = new msg_command_long();
		cali.command = MAV_CMD.MAV_CMD_PREFLIGHT_CALIBRATION;
		cali.param3 = 1;
		int attempts = 3;
		int maxTime = 10000;
		for (int i = 0; i < attempts; i++) {
			int arraySize = CommandAck.getMessageMemory().size();
			long sendTime = System.currentTimeMillis();
			send(cali.pack());
			while (System.currentTimeMillis() - sendTime < maxTime) {
				waitMillis(20);
				if (CommandAck.getMessageMemory().size() > arraySize) {
					if (CommandAck.getMessageMemory().get(arraySize)
							.getCommand() == MAV_CMD.MAV_CMD_PREFLIGHT_CALIBRATION) {
						if (CommandAck.getMessageMemory().get(arraySize)
								.getResult() == MAV_RESULT.MAV_RESULT_ACCEPTED) {
							return MAV_RESULT.MAV_RESULT_ACCEPTED;
						} else if (i == attempts - 1) {
							return CommandAck.getMessageMemory().get(arraySize).getResult();
						}
					}
					arraySize += 1;
				}
			}
		}
		return MAV_RESULT.MAV_RESULT_FAILED;
	}

	//arms the motor of the UAV
	public int arm() throws UnknownHostException, SocketException {
		msg_command_long arm = new msg_command_long();
		arm.command = MAV_CMD.MAV_CMD_COMPONENT_ARM_DISARM;
		arm.param1 = 1;
		int attempts = 3;
		int maxTime = 500;
		for (int i = 0; i < attempts; i++) {
			int arraySize = CommandAck.getMessageMemory().size();
			long sendTime = System.currentTimeMillis();
			send(arm.pack());
			while (System.currentTimeMillis() - sendTime < maxTime) {
				waitMillis(20);
				if (CommandAck.getMessageMemory().size() > arraySize) {
					if (CommandAck.getMessageMemory().get(arraySize)
							.getCommand() == MAV_CMD.MAV_CMD_COMPONENT_ARM_DISARM) {
						if (CommandAck.getMessageMemory().get(arraySize)
								.getResult() == MAV_RESULT.MAV_RESULT_ACCEPTED) {
							return MAV_RESULT.MAV_RESULT_ACCEPTED;
						} else if (i == attempts - 1) {
							return CommandAck.getMessageMemory().get(arraySize).getResult();
						}
					}
					arraySize += 1;
				}
			}
		}
		return MAV_RESULT.MAV_RESULT_FAILED;
	}

	//disarms the motor of the UAV
	public int disarm() throws UnknownHostException, SocketException {
		msg_command_long disarm = new msg_command_long();
		disarm.command = MAV_CMD.MAV_CMD_COMPONENT_ARM_DISARM;
		disarm.param1 = 0;
		int attempts = 3;
		int maxTime = 500;
		for (int i = 0; i < attempts; i++) {
			int arraySize = CommandAck.getMessageMemory().size();
			long sendTime = System.currentTimeMillis();
			send(disarm.pack());
			while (System.currentTimeMillis() - sendTime < maxTime) {
				waitMillis(20);
				if (CommandAck.getMessageMemory().size() > arraySize) {
					if (CommandAck.getMessageMemory().get(arraySize)
							.getCommand() == MAV_CMD.MAV_CMD_COMPONENT_ARM_DISARM) {
						if (CommandAck.getMessageMemory().get(arraySize)
								.getResult() == MAV_RESULT.MAV_RESULT_ACCEPTED) {
							return MAV_RESULT.MAV_RESULT_ACCEPTED;
						} else if (i == attempts - 1) {
							return CommandAck.getMessageMemory().get(arraySize).getResult();
						}
					}
					arraySize += 1;
				}
			}
		}
		return MAV_RESULT.MAV_RESULT_FAILED;
	}

	//sets a new home position by passing a CustomMissionItem
	public int setHomePosition(CustomMissionItem hp) throws UnknownHostException, SocketException {
		//check if UAV is armed, otherwise deny command
		if (!Heartbeat.getMessageMemory().getNewestElement().getArmedState()) {
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
		for (int i = 0; i < attempts; i++) {
			int arraySize = MissionItem.getMessageMemory().size();
			long sendTime = System.currentTimeMillis();
			send(setHome.pack());
			while (System.currentTimeMillis() - sendTime < maxTime) {
				waitMillis(20);
				if (MissionItem.getMessageMemory().size() > arraySize) {
					if (MissionItem.getMessageMemory().get(arraySize)
							.getCommand() == msg_home_position.MAVLINK_MSG_ID_HOME_POSITION) {
						if (MissionItem.getMessageMemory().get(arraySize).getMissionItem().latitude == hp.latitude
								&& MissionItem.getMessageMemory().get(arraySize)
										.getMissionItem().longitude == hp.longitude
								&& MissionItem.getMessageMemory().get(arraySize)
										.getMissionItem().altitude == hp.altitude) {
							return MAV_RESULT.MAV_RESULT_ACCEPTED;
						} else if (i == attempts - 1) {
							return MissionItem.getMessageMemory().get(arraySize).getResult();
						}
					}
					arraySize += 1;
				}
			}
		}
		return MAV_RESULT.MAV_RESULT_FAILED;
	}

	//returns a CustomMissionItem with the coordinates and altitude of the current home position
	public CustomMissionItem getHomePosition() throws UnknownHostException, SocketException {
		CustomMissionItem failed = new CustomMissionItem(MissionItemType.INVALID, 0, 0, 0);
		msg_command_long getHome = new msg_command_long();
		getHome.command = MAV_CMD.MAV_CMD_GET_HOME_POSITION;
		int attempts = 3;
		int maxTime = 500;
		for (int i = 0; i < attempts; i++) {
			int arraySize = MissionItem.getMessageMemory().size();
			long sendTime = System.currentTimeMillis();
			send(getHome.pack());
			while (System.currentTimeMillis() - sendTime < maxTime) {
				waitMillis(20);
				if (MissionItem.getMessageMemory().size() > arraySize) {
					if (MissionItem.getMessageMemory().get(arraySize)
							.getCommand() == msg_home_position.MAVLINK_MSG_ID_HOME_POSITION) {
						if (MissionItem.getMessageMemory().get(arraySize)
								.getResult() == MAV_RESULT.MAV_RESULT_ACCEPTED) {
							return MissionItem.getMessageMemory().get(arraySize).getMissionItem();
						} else if (i == attempts - 1) {
							return failed;
						}
					}
					arraySize += 1;
				}
			}
		}
		return failed;
	}

	//sends a new mission to the UAV; depending on restart value the mission will start at waypoint 0 or continue at the currently active waypoint
	public int sendMission(ArrayList<CustomMissionItem> mission, boolean restart)
			throws UnknownHostException, SocketException {
		if(mission.size() == 0 || mission == null) {
			return MAV_RESULT.MAV_RESULT_FAILED;
		}
		if (mission.get(0).type == MissionItemType.INVALID) {
			return MAV_RESULT.MAV_RESULT_FAILED;
		}
		int currentSequence = getSequence();
		//sendMission fails, if getSequence failed
		if (currentSequence == MAV_RESULT.MAV_RESULT_FAILED) {
			return MAV_RESULT.MAV_RESULT_FAILED;
		}
		//getSequence returns negated sequence. multiplication by -1 is needed.
		currentSequence *= -1;
		if (restart || currentSequence >= mission.size()) {
			currentSequence = 0;
		}

		int missionCount = mission.size();
		msg_mission_item item;

		//clear old mission
		waitMillis(50);
		msg_mission_clear_all clear = new msg_mission_clear_all();
		send(clear.pack());

		//send mission size
		waitMillis(50);
		msg_mission_count count = new msg_mission_count();
		count.count = missionCount + 1;
		send(count.pack());

		//send mission items one by one
		waitMillis(50);
		item = new msg_mission_item();
		item.command = MAV_CMD.MAV_CMD_NAV_WAYPOINT;
		item.seq = 0;
		send(item.pack());

		for (int i = 0; i < missionCount; i++) {
			waitMillis(20);
			item = new msg_mission_item();
			item.seq = i + 1;
			item.frame = Settings.getInstance().getFrameOrientation();
			item.x = mission.get(i).latitude;
			item.y = mission.get(i).longitude;
			item.z = mission.get(i).altitude;
			switch (mission.get(i).type) {
			case MissionItemType.INVALID:
				return MAV_RESULT.MAV_RESULT_FAILED;
			case MissionItemType.LAND:
				item.command = MAV_CMD.MAV_CMD_NAV_LAND;
				item.param1 = Settings.getInstance().getAbortAltitude(); // landing abort altitude in meters
				break;
			case MissionItemType.TAKEOFF:
				item.command = MAV_CMD.MAV_CMD_NAV_TAKEOFF;
				item.param1 = Settings.getInstance().getTakeOffPitch(); // take off pitch angle in degrees
				break;
			case MissionItemType.RTL:
				item.command = MAV_CMD.MAV_CMD_NAV_RETURN_TO_LAUNCH;
				break;
			case MissionItemType.WAYPOINT:
				item.command = MAV_CMD.MAV_CMD_NAV_WAYPOINT;
				break;
			default:
				item.command = MAV_CMD.MAV_CMD_NAV_LOITER_TIME;
				item.param1 = mission.get(i).type; // loiter time in seconds
				item.param3 = Settings.getInstance().getLoiterRadius(); // loiter radius in meters
				break;
			}
			send(item.pack());
		}

		//get verification mission and compare it with the target mission
		ArrayList<CustomMissionItem> verification = getMission();
		
		if (verification.get(0).type == MissionItemType.INVALID) {
			return MAV_RESULT.MAV_RESULT_FAILED;
		}

		for (int i = 0; i < verification.size(); i++) {
			if (!verification.get(i).equals(mission.get(i))) {
				return MAV_RESULT.MAV_RESULT_FAILED;
			}
		}

		//sendMission fails, if setSequence failed
		if (setSequence(currentSequence) != MAV_RESULT.MAV_RESULT_ACCEPTED) {
			return MAV_RESULT.MAV_RESULT_FAILED;
		}
		return MAV_RESULT.MAV_RESULT_ACCEPTED;
	}

	//sets the current waypoint sequence
	public int setSequence(int sequence) throws UnknownHostException, SocketException {
		msg_mission_set_current current = new msg_mission_set_current();
		current.seq = sequence;
		int attempts = 3;
		int maxTime = 500;
		for (int i = 0; i < attempts; i++) {
			int arraySize = MissionState.getMessageMemory().size();
			long sendTime = System.currentTimeMillis();
			send(current.pack());
			while (System.currentTimeMillis() - sendTime < maxTime) {
				waitMillis(20);
				if (MissionState.getMessageMemory().size() > arraySize) {
					if (MissionState.getMessageMemory().get(arraySize).getCurrentSequence() == sequence) {
						return MAV_RESULT.MAV_RESULT_ACCEPTED;
					}
					arraySize += 1;
				}
			}
		}
		return MAV_RESULT.MAV_RESULT_FAILED;
	}

	//returns the currently active waypoint sequence
	public int getSequence() {
		int timeOut = 3000;
		long start = System.currentTimeMillis();
		while (MissionState.getMessageMemory().size() == 0) {
			if (System.currentTimeMillis() - start > timeOut) {
				return MAV_RESULT.MAV_RESULT_FAILED;
			}
		}
		return -1*MissionState.getMessageMemory().getNewestElement().getCurrentSequence();
	}

	//returns the current mission as an ArrayList of CustomMissionItems
	public ArrayList<CustomMissionItem> getMission() throws UnknownHostException, SocketException {
		ArrayList<CustomMissionItem> failed = new ArrayList<>();
		failed.add(new CustomMissionItem(MissionItemType.INVALID, 0, 0, 0));
		ArrayList<CustomMissionItem> mission = new ArrayList<>();
		
		//get mission size
		int missionCount = getMissionCount();
		if (missionCount == MAV_RESULT.MAV_RESULT_FAILED) {
			return failed;
		}
		//getMissionCount returns negated mission size. multiplication by -1 is needed.
		missionCount *= -1;

		//get all mission items
		CustomMissionItem item;
		for (int i = 1; i < missionCount; i++) {
			item = getMissionItem(i);
			if (item.type == MissionItemType.INVALID) {
				return failed;
			}
			mission.add(item);
		}
		return mission;
	}

	//returns the waypoint at a specific sequence as a CustomMissionItem
	public CustomMissionItem getMissionItem(int sequence) throws UnknownHostException, SocketException {
		CustomMissionItem failed = new CustomMissionItem(MissionItemType.INVALID, 0, 0, 0);
		msg_mission_request request = new msg_mission_request();
		request.seq = sequence;
		int attempts = 50;
		int maxTime = 300;
		for (int i = 0; i < attempts; i++) {
			int arraySize = MissionItem.getMessageMemory().size();
			long sendTime = System.currentTimeMillis();
			send(request.pack());
			while (System.currentTimeMillis() - sendTime < maxTime) {
				waitMillis(20);
				if (MissionItem.getMessageMemory().size() > arraySize) {
					if (MissionItem.getMessageMemory().get(arraySize)
							.getCommand() == msg_mission_item.MAVLINK_MSG_ID_MISSION_ITEM) {
						if (MissionItem.getMessageMemory().get(arraySize).getResult() == sequence) {
							return MissionItem.getMessageMemory().get(arraySize).getMissionItem();
						}
					}
					arraySize += 1;
				}
			}
		}
		return failed;
	}

	//returns the number of waypoints contained in the current mission
	public int getMissionCount() throws UnknownHostException, SocketException {
		msg_mission_request_list list = new msg_mission_request_list();
		int attempts = 3;
		int maxTime = 500;
		for (int i = 0; i < attempts; i++) {
			int arraySize = CommandAck.getMessageMemory().size();
			long sendTime = System.currentTimeMillis();
			send(list.pack());
			while (System.currentTimeMillis() - sendTime < maxTime) {
				waitMillis(20);
				if (CommandAck.getMessageMemory().size() > arraySize) {
					if (CommandAck.getMessageMemory().get(arraySize)
							.getCommand() == msg_mission_count.MAVLINK_MSG_ID_MISSION_COUNT) {
						if (CommandAck.getMessageMemory().get(arraySize).getResult() < 0) {
							return CommandAck.getMessageMemory().get(arraySize).getResult();
						}
					}
					arraySize += 1;
				}
			}
		}
		return MAV_RESULT.MAV_RESULT_FAILED;
	}

	//waits for a specified amount of time in milliseconds
	public void waitMillis(long t) {
		try {
			Thread.sleep(t);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	//sends a mavlink packet over UDP or serial connection depending on settings
	public void send(MAVLinkPacket packet) throws UnknownHostException, SocketException {
		if (udpInsteadOfSerial) {
			sendUDP(packet);
		} else {
			sendSerial(packet);
		}
	}

	//sends a mavlink packet over serial connection
	public void sendSerial(MAVLinkPacket packet) {
		try {
			port.getOutputStream().write(packet.encodePacket());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	//sends a mavlink packet over UDP connection
	public void sendUDP(MAVLinkPacket packet) throws UnknownHostException, SocketException {
		int port = Settings.getInstance().getUdpOutgoingPort();
		InetAddress ipAdress = InetAddress.getByName(Settings.getInstance().getUdpIPAdress());
		DatagramSocket dSocket = new DatagramSocket();
		DatagramPacket sendPacket = new DatagramPacket(packet.encodePacket(), packet.encodePacket().length, ipAdress,
				port);
		try {
			dSocket.send(sendPacket);
			System.out.println("sent to " + ipAdress.getHostAddress() + ":" + port);
			dSocket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
