package com.telemetry;

import com.MAVLink.common.msg_mission_current;

import tools.MessageMemory;

public class MissionState implements TelemetryMessage{
	int currentItem;
	long timestamp;
	
	private static MessageMemory<MissionState> messageMemory = new MessageMemory<>();
	public MissionState(msg_mission_current message) {
		timestamp=System.currentTimeMillis();
		currentItem = message.seq;
		messageMemory.add(this);
	}

	@Override
	public String getJSON() {
		// TODO Auto-generated method stub
		return null;
	}

	public static MessageMemory<MissionState> getMessageMemory() {
		return messageMemory;
	}

	@Override
	public String getSocketMethodName() {
		return "missionState";
	}

	@Override
	public long getTimestamp() {
		return timestamp;
	}

}
