package com.telemetry;

import org.json.JSONObject;

import com.MAVLink.common.msg_battery_status;

import tools.MessageMemory;

public class Battery implements TelemetryMessage{
	float voltage, current;
	int percentage;
	long timestamp;
	
	private static MessageMemory<Battery> messageMemory = new MessageMemory<>();
	public Battery(msg_battery_status message){
		timestamp=System.currentTimeMillis();
		int avgVoltage = 0;
		for (int i = 0; i < message.voltages.length; i++) {
			avgVoltage += message.voltages[i];
		}
		voltage = avgVoltage/message.voltages.length;
		current = message.current_battery;
		percentage = message.battery_remaining; //TODO byte to integer conversion
		messageMemory.add(this);
	}

	@Override
	public JSONObject getJSON() {
		JSONObject res = new JSONObject();
		res.put("timestamp", timestamp).put("voltage", voltage).put("current", current).put("percentage", percentage);
		return res;
	}

	public static MessageMemory<Battery> getMessageMemory() {
		return messageMemory;
	}

	@Override
	public String getSocketMethodName() {
		return "battery";
	}

	@Override
	public long getTimestamp() {
		return timestamp;
	}

}
