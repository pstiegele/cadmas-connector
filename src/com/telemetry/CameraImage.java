package com.telemetry;

import java.nio.ByteBuffer;

import org.json.JSONObject;

import tools.MessageMemory;

public class CameraImage implements TelemetryMessage {

	long timestamp;
	ByteBuffer img;

	private static MessageMemory<CameraImage> messageMemory = new MessageMemory<>();

	public CameraImage(ByteBuffer img) {
		timestamp = System.currentTimeMillis();
		this.img = img;
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

	public ByteBuffer getByteBuffer() {
		return img;
	}

}
