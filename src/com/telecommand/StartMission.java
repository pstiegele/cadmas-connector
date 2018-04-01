package com.telecommand;

import org.json.JSONObject;

import com.controller.autopilot.Autopilot;

import tools.MessageMemory;

public class StartMission implements TelecommandMessage {

	private static MessageMemory<StartMission> messageMemory = new MessageMemory<>();
	
	int missionID = -1;
	int missionVersion = -1;
	/**
	 * @param arm
	 */
	public StartMission(int missionID, int missionVersion) {
		super();
		this.missionID = missionID;
		this.missionVersion = missionVersion;
		messageMemory.add(this);
	}
	
	public StartMission(JSONObject payload) {
		super();
		this.missionID = payload.getInt("missionID");;
		this.missionVersion = payload.getInt("missionVersion");;
		messageMemory.add(this);
	}
	

	@Override
	public boolean execute() {
		
//		autopilot.send(getMissionCountPacket());
//		int iteration = 0;
//		for (Waypoint waypoint : waypoints) {
//			waitForMissionRequestPacket(iteration);
//			waypoint.execute(autopilot);
//			iteration++;
//		}
//		if(waitForMissionAckPacket()) return false;
//		
		
		return true;
	}

	public static MessageMemory<StartMission> getMessageMemory() {
		return messageMemory;
	}

	@Override
	public String toString() {
		return "StartMission [missionID=" + missionID + ", missionVersion="
				+ missionVersion + "]";
	}

	
	
	
}
