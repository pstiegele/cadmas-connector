package com.telemetry;

import org.json.JSONObject;

import com.MAVLink.common.msg_battery_status;
import com.MAVLink.common.msg_sys_status;

import tools.MessageMemory;

public class Battery implements TelemetryMessage{
	float voltage, current;
	int percentage;
	long timestamp;
	
	private static MessageMemory<Battery> messageMemory = new MessageMemory<>();
	public Battery() {
		voltage = current = percentage = 0;
		timestamp = 0;
		messageMemory.add(this);
	}
	//creates battery telemetry from mavlink battery status message
	public Battery(msg_battery_status message){
		timestamp=System.currentTimeMillis();
		
		//calculates total voltage
		int avgVoltage = 0;
		for (int i = 0; i < message.voltages.length; i++) {
			avgVoltage += message.voltages[i];
		}
		voltage = avgVoltage/message.voltages.length;
		current = message.current_battery;
		percentage = message.battery_remaining;
		messageMemory.add(this);
	}
	
	//creates battery telemetry from mavlink system status message (more reliable during testing)
	public Battery(msg_sys_status message) {
		timestamp=System.currentTimeMillis();
		voltage = (float) message.voltage_battery/1000;
		current = (float) message.current_battery/100;
		percentage = message.battery_remaining;
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
	
	public float getVoltage() {
		return voltage;
	}
	
	public float getCurrent() {
		return current;
	}

}
