package com.telemetry;


import org.json.JSONObject;

import com.MAVLink.common.msg_heartbeat;

import jdk.nashorn.internal.parser.JSONParser;
import jdk.nashorn.internal.runtime.JSONFunctions;
import tools.MessageMemory;

public class Heartbeat implements TelemetryMessage{
	int baseMode;
	long customMode;
	long timestamp;
	
	private static MessageMemory<Heartbeat> messageMemory = new MessageMemory<>();
	public Heartbeat(msg_heartbeat message) {
		timestamp=System.currentTimeMillis();
		baseMode = message.base_mode;
		customMode = message.custom_mode;
		messageMemory.add(this);
	}
	
	@Override
	public JSONObject getJSON() {
		JSONObject res = new JSONObject();
		res.put("timestamp", timestamp).put("baseMode", baseMode).put("customMode", customMode);
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


}
