package com.telemetry;


import org.json.JSONObject;

import com.MAVLink.common.msg_heartbeat;

import tools.MessageMemory;

public class Heartbeat implements TelemetryMessage{
	int baseMode;
	long customMode;
	long timestamp;
	float messagesLost;
	
	private static MessageMemory<Heartbeat> messageMemory = new MessageMemory<>();
	public Heartbeat(msg_heartbeat message, int[] rssi) {
		timestamp=System.currentTimeMillis();
		baseMode = message.base_mode;
		customMode = message.custom_mode;
		messagesLost = 0;
		for(int i = 0; i < rssi.length; i++) {
			messagesLost += rssi[i];
		}
		messagesLost = messagesLost/rssi.length;
		messageMemory.add(this);
	}
	
	@Override
	public JSONObject getJSON() {
		JSONObject res = new JSONObject();
		res.put("timestamp", timestamp).put("baseMode", baseMode).put("customMode", customMode).put("messagesLost", messagesLost);
		return res;
	}

	public static MessageMemory<Heartbeat> getMessageMemory() {
		return messageMemory;
	}

	@Override
	public String getSocketMethodName() {
		return "heartbeat";
	}

	@Override
	public long getTimestamp() {
		return timestamp;
	}
	
	public float getRSSI() {
		return messagesLost;
	}


}
