
package com.telemetry;

import org.json.JSONObject;

import com.MAVLink.common.msg_vfr_hud;

import tools.MessageMemory;

public class Velocity implements TelemetryMessage{
	float groundspeed, airspeed, climbrate;
	long timestamp;
	
	private static MessageMemory<Velocity> messageMemory = new MessageMemory<>();
	public Velocity() {
		groundspeed = airspeed = climbrate = timestamp = 0;
		messageMemory.add(this);
	}
	public Velocity(msg_vfr_hud message) {
		timestamp=System.currentTimeMillis();
		groundspeed = message.groundspeed;
		airspeed = message.airspeed;
		climbrate = message.climb;
		messageMemory.add(this);
	}

	@Override
	public JSONObject getJSON() {
		JSONObject res = new JSONObject();
		res.put("timestamp", timestamp).put("groundspeed", groundspeed).put("airspeed", airspeed).put("climbrate", climbrate);
		return res;
	}

	public static MessageMemory<Velocity> getMessageMemory() {
		return messageMemory;
	}

	@Override
	public String getSocketMethodName() {
		return "velocity";
	}

	@Override
	public long getTimestamp() {
		return timestamp;
	}
}
