package com.telecommand;

import com.MAVLink.MAVLinkPacket;
import com.MAVLink.common.msg_mission_item;
import com.controller.autopilot.Autopilot;

public class Waypoint implements TelecommandMessage {
	
	float latitude,longitude;
	String type;
	int altitude;
	public Waypoint(String type, float latitude, float longitude, int altitude) {
		this.type = type;
		this.latitude=latitude;
		this.longitude=longitude;
		this.altitude=altitude;
	}

	@Override
	public boolean execute(Autopilot autopilot) {
		msg_mission_item waypoint = new msg_mission_item();
		waypoint.x=latitude;
		waypoint.y=longitude;
		waypoint.z=altitude;
		
		MAVLinkPacket packet = waypoint.pack();
		//packet.seq=autopilot.getSeq();
		autopilot.send(packet);
		return true;
	}

}
