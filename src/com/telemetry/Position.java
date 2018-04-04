package com.telemetry;

import com.MAVLink.common.msg_global_position_int;

import tools.MessageMemory;

public class Position implements TelemetryMessage{
	
	float latitude, longitude, altitude;
	long timestamp;

	private static MessageMemory<Position> messageMemory = new MessageMemory<>();
	public Position(msg_global_position_int message){
		timestamp=System.currentTimeMillis();
		latitude = message.lat;
		longitude = message.lon;
		altitude = message.alt;
		messageMemory.add(this);
	}
	@Override
	public String getJSON() {
		// TODO Auto-generated method stub
		return null;
	}

	public static MessageMemory<Position> getMessageMemory() {
		return messageMemory;
	}
	
	@Override
	public String getSocketMethodName() {
		return "position";
	}
	@Override
	public long getTimestamp() {
		return timestamp;
	}

}
