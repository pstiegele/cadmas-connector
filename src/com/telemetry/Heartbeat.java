package com.telemetry;


import org.json.JSONObject;

import com.MAVLink.common.msg_heartbeat;
import com.controller.autopilot.FlightMode;

import tools.MessageMemory;

public class Heartbeat implements TelemetryMessage{
	boolean isArmed;
	long customMode;
	long timestamp;
	float messagesLost; // number of lost messages for every received message
	float cpuTemp;
	
	private static MessageMemory<Heartbeat> messageMemory = new MessageMemory<>();
	public Heartbeat() {
		isArmed = false;
		customMode = FlightMode.MANUAL;
		timestamp = 0;
		messagesLost = 0;
		cpuTemp = 0;
		messageMemory.add(this);
	}
	public Heartbeat(msg_heartbeat message, int[] rssi, float temp) {
		timestamp=System.currentTimeMillis();
		isArmed = false;
		if(message.base_mode > 127) {
			isArmed = true;
		}
		customMode = message.custom_mode;
		
		//calculates messages lost
		messagesLost = 0;
		for(int i = 0; i < rssi.length; i++) {
			messagesLost += rssi[i];
		}
		messagesLost = messagesLost/rssi.length;
		cpuTemp = temp;
		messageMemory.add(this);
	}
	
	@Override
	public JSONObject getJSON() {
		JSONObject res = new JSONObject();
		res.put("timestamp", timestamp).put("isArmed", isArmed).put("customMode", customMode).put("messagesLost", messagesLost).put("cpuTemp", cpuTemp);
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
	
	public boolean getArmedState() {
		return isArmed;
	}


}
