package com.controller.autopilot;

public class CustomMissionItem {
	int type = 0; // 0=Waypoint -1=RTL -2=TakeOff -3=Land n=Loiter for n secs ...
	float latitude = 0;
	float longitude = 0;
	int height = 0;
	
	public CustomMissionItem(int type, float latitude, float longitude, int height){
		this.type = type;
		this.latitude = latitude;
		this.longitude = longitude;
		this.height = height;
	}

	@Override
	public String toString() {
		String typeString;
		switch(type){
		case -3:
			typeString = "Land";
			break;
		case -2:
			typeString = "Take Off";
			break;
		case -1:
			typeString = "RTL";
			break;
		case 0:
			typeString = "Waypoint";
			break;
		default:
			typeString = "Loiter(" + type +"s)";
			break;
		}
		return typeString + "\tLat: " + latitude + "\tLong: " + longitude + "\tAlt: " + height;
	}
	
	
}
