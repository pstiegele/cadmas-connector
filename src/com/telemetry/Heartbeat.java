package com.telemetry;

import org.json.JSONObject;

import com.MAVLink.MAVLinkPacket;
import com.MAVLink.common.msg_heartbeat;

import tools.MessageMemory;

public class Heartbeat extends msg_heartbeat implements TelemetryMessage{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = MAVLINK_MSG_ID_HEARTBEAT;
	long timestamp = -1;
	public static MessageMemory<Heartbeat> messageMemory = new MessageMemory<>();
	public Heartbeat() {
		
	}
	
	public Heartbeat(MAVLinkPacket mavpacket) {
		super(mavpacket);
		timestamp=System.currentTimeMillis();
		messageMemory.add(this);
	}
	
	@Override
	public String getJSON() {
		JSONObject payload = new JSONObject();
		payload.put("autopilot_connected", true).put("autopilot_mode", "AUTO").put("armed", false);
		return payload.toString();
	}

	
	public static MessageMemory<Heartbeat> getMessageMemory() {
		return messageMemory;
	}

	@Override
	public String getSocketMethodName() {
		return "heartbeat";
	}


}
