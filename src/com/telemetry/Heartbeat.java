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
		JSONObject res = new JSONObject();
		res.put("type", "hb").put("timestamp", timestamp).put("autopilot", this.autopilot);
		return res.toString();
	}

	
	public static MessageMemory<Heartbeat> getMessageMemory() {
		return messageMemory;
	}


}
