package com.telemetry;

import org.json.JSONObject;

public interface TelemetryMessage{
	
	public abstract JSONObject getJSON();
	public abstract String getSocketMethodName();
	public abstract long getTimestamp();

}
