package com.telemetry;

import org.json.JSONObject;

import com.MAVLink.MAVLinkPacket;
import com.MAVLink.common.msg_scaled_pressure;

import tools.MessageMemory;

public class ScaledPressure extends msg_scaled_pressure implements TelemetryMessage{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = MAVLINK_MSG_ID_SCALED_PRESSURE;
	long timestamp = -1;
	private static MessageMemory<ScaledPressure> messageMemory = new MessageMemory<>();
	public ScaledPressure() {
		
	}
	
	public ScaledPressure(MAVLinkPacket mavpacket) {
		super(mavpacket);
		timestamp=System.currentTimeMillis();
		messageMemory.add(this);
	}
	
	@Override
	public String getJSON() {
		JSONObject res = new JSONObject();
		res.put("type", "hb").put("timestamp", timestamp).put("press_abs", this.press_abs);
		return res.toString();
	}


	@Override
	public MessageMemory<?> getMessageMemory() {
		return messageMemory;
	}


}
