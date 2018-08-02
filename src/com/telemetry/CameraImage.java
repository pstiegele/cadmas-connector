package com.telemetry;

import org.json.JSONObject;

import com.MAVLink.common.msg_attitude;

import tools.MessageMemory;

public class CameraImage implements TelemetryMessage{


	long timestamp;
	byte[] img;
	
	private static MessageMemory<CameraImage> messageMemory = new MessageMemory<>();
	public CameraImage(byte[] img) {
		timestamp = System.currentTimeMillis();
		this.img=img;
		messageMemory.add(this);
	}
	
	
	@Override
	public JSONObject getJSON() {
		JSONObject res = new JSONObject();
		res.put("img", img).put("timestamp", timestamp);
		return res;
	}


	public static MessageMemory<CameraImage> getMessageMemory() {
		return messageMemory;
	}

	@Override
	public String getSocketMethodName() {
		return "cameraImage";
	}

	@Override
	public long getTimestamp() {
		return timestamp;
	}


}
