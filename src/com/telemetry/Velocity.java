
package com.telemetry;

import org.json.JSONObject;

import com.MAVLink.common.msg_vfr_hud;

import tools.MessageMemory;
import tools.Settings;

public class Velocity implements TelemetryMessage{
	float groundspeed, airspeed, climbrate, altitude; //altitude = relative to homePoint; only use after manually setting homepoint; DO NOT USE when value is -100
	long timestamp;
	
	private static MessageMemory<Velocity> messageMemory = new MessageMemory<>();
	public Velocity(msg_vfr_hud message) {
		timestamp=System.currentTimeMillis();
		groundspeed = message.groundspeed;
		airspeed = message.airspeed;
		climbrate = message.climb;
		if(Settings.getInstance().getHomeAltitude() == -100) {
			altitude = -100;
		}
		else {
			altitude = message.alt - Settings.getInstance().getHomeAltitude();
		}
		messageMemory.add(this);
	}

	@Override
	public JSONObject getJSON() {
		JSONObject res = new JSONObject();
		res.put("timestamp", timestamp).put("groundspeed", groundspeed).put("airspeed", airspeed).put("climbrate", climbrate).put("altitude", altitude);
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
	
	public float getAltitude() {
		return altitude;
	}

}
