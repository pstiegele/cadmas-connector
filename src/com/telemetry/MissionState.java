package com.telemetry;

import org.json.JSONObject;

import com.MAVLink.common.msg_mission_current;

import tools.MessageMemory;

public class MissionState implements TelemetryMessage{
	int currentSequence;
	long timestamp;
	
	private static MessageMemory<MissionState> messageMemory = new MessageMemory<>();
	public MissionState() {
		currentSequence = 0;
		timestamp = 0;
		messageMemory.add(this);
	}
	public MissionState(msg_mission_current message) {
		timestamp=System.currentTimeMillis();
		currentSequence = -1*message.seq;
		messageMemory.add(this);
	}

	@Override
	public JSONObject getJSON() {
		JSONObject res = new JSONObject();
		res.put("timestamp", timestamp).put("currentItem", currentSequence);
		return res;
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
	
	public int getCurrentSequence(){
		return currentSequence;
	}

}
