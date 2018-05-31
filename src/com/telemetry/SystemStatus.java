package com.telemetry;

import org.json.JSONObject;

import com.MAVLink.common.msg_sys_status;

import tools.MessageMemory;

public class SystemStatus implements TelemetryMessage{
	float voltage, current;
	int messageDropRate;
	long timestamp;
	
	private static MessageMemory<SystemStatus> messageMemory = new MessageMemory<>();
	public SystemStatus() {
		voltage = current = messageDropRate = 0;
		timestamp = 0;
		messageMemory.add(this);
	}
	public SystemStatus(msg_sys_status message){
		timestamp=System.currentTimeMillis();
		voltage = (float) message.voltage_battery/1000; //in V
		current = (float) message.current_battery/100; // in A
		messageDropRate = message.drop_rate_comm;
		messageMemory.add(this);
	}

	@Override
	public JSONObject getJSON() {
		JSONObject res = new JSONObject();
		res.put("timestamp", timestamp).put("voltage", voltage).put("current", current).put("messageDropRate", messageDropRate);
		return res;
	}

	public static MessageMemory<SystemStatus> getMessageMemory() {
		return messageMemory;
	}

	@Override
	public String getSocketMethodName() {
		return "SystemStatus";
	}

	@Override
	public long getTimestamp() {
		return timestamp;
	}
	
	public float getVoltage() {
		return voltage;
	}
	
	public float getCurrent() {
		return current;
	}

}
