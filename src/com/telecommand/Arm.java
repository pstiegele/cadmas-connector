package com.telecommand;

import org.json.JSONObject;

import com.controller.autopilot.Autopilot;

import tools.MessageMemory;

public class Arm implements TelecommandMessage {

	private static MessageMemory<Arm> messageMemory = new MessageMemory<>();
	
	Autopilot autopilot;
	boolean arm = false;
	/**
	 * @param arm
	 */
	public Arm(boolean arm) {
		super();
		this.arm = arm;
		messageMemory.add(this);
	}
	
	public Arm(JSONObject payload) {
		super();
		arm = payload.getBoolean("arm");
		messageMemory.add(this);
	}
	

	@Override
	public boolean execute(Autopilot autopilot) {
		this.autopilot = autopilot;
		
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

	public static MessageMemory<Arm> getMessageMemory() {
		return messageMemory;
	}

	@Override
	public String toString() {
		return "Arm [autopilot=" + autopilot + ", arm=" + arm + "]";
	}
	
	
}
