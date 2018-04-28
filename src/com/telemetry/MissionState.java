package com.telemetry;

import org.json.JSONObject;

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
	public JSONObject getJSON() {
		JSONObject res = new JSONObject();
		res.put("timestamp", timestamp).put("currentItem", currentItem);
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

}
