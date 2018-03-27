package com.telemetry;

import org.json.JSONObject;

import com.MAVLink.MAVLinkPacket;
import com.MAVLink.common.msg_attitude;

import tools.MessageMemory;

public class Attitude extends msg_attitude implements TelemetryMessage{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = MAVLINK_MSG_ID_ATTITUDE;
	long timestamp = -1;
	private static MessageMemory<Attitude> messageMemory = new MessageMemory<>();
	public Attitude() {
		
	}
	
	public Attitude(MAVLinkPacket mavpacket) {
		super(mavpacket);
		timestamp=System.currentTimeMillis();
		messageMemory.add(this);
	}
	
	@Override
	public String getJSON() {
		JSONObject res = new JSONObject();
		res.put("type", "hb").put("timestamp", timestamp).put("pitch", this.pitch);
		return res.toString();
	}


	@Override
	public MessageMemory<?> getMessageMemory() {
		return messageMemory;
	}


}
