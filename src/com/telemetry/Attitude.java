package com.telemetry;

import org.json.JSONObject;

import com.MAVLink.common.msg_attitude;

import tools.MessageMemory;

public class Attitude implements TelemetryMessage{
	
	float pitch, roll, yaw;
	long timestamp;
	
	private static MessageMemory<Attitude> messageMemory = new MessageMemory<>();
	public Attitude() {
		pitch = roll = yaw = timestamp = 0;
		messageMemory.add(this);
	}
	public Attitude(msg_attitude message) {
		timestamp=System.currentTimeMillis();
		pitch = message.pitch;
		roll = message.roll;
		yaw = message.yaw;
		messageMemory.add(this);
	}
	
	@Override
	public JSONObject getJSON() {
		JSONObject res = new JSONObject();
		res.put("pitch", pitch).put("roll", roll).put("heading", yaw);
		return res;
	}


	public static MessageMemory<Attitude> getMessageMemory() {
		return messageMemory;
	}

	@Override
	public String getSocketMethodName() {
		return "attitude";
	}

	@Override
	public long getTimestamp() {
		return timestamp;
	}


}
