package com.telemetry;

import com.MAVLink.common.msg_vfr_hud;

import tools.MessageMemory;

public class Velocity implements TelemetryMessage{
	float groundspeed, airspeed, climbrate, altitude;
	long timestamp;
	
	private static MessageMemory<Velocity> messageMemory = new MessageMemory<>();
	public Velocity(msg_vfr_hud message) {
		timestamp=System.currentTimeMillis();
		groundspeed = message.groundspeed;
		airspeed = message.airspeed;
		climbrate = message.climb;
		altitude = message.alt;
		messageMemory.add(this);
	}

	@Override
	public String getJSON() {
		// TODO Auto-generated method stub
		return null;
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
