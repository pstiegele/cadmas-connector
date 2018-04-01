package com.telecommand;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.MAVLink.MAVLinkPacket;
import com.MAVLink.common.msg_mission_count;
import com.controller.autopilot.Autopilot;
import com.telemetry.MissionRequest;

import javafx.collections.ListChangeListener;
import tools.MessageMemory;

public class Mission implements TelecommandMessage {

	private static MessageMemory<Mission> messageMemory = new MessageMemory<>();
	
	Autopilot autopilot;
	int missionID = 0;
	OnConnectionLostMode onConnectionLostMode = OnConnectionLostMode.RTL;
	List<Waypoint> waypoints = new ArrayList<>();
	/**
	 * @param missionID
	 * @param onConnectionLostMode
	 * @param waypoints
	 */
	public Mission(int missionID,OnConnectionLostMode onConnectionLostMode,
			List<Waypoint> waypoints) {
		super();
		this.missionID = missionID;
		this.onConnectionLostMode = onConnectionLostMode;
		this.waypoints = waypoints;
		messageMemory.add(this);
	}
	public Mission(List<Waypoint> waypoints) {
		super();
		this.waypoints = waypoints;
		messageMemory.add(this);
	}
	
	public Mission(JSONObject payload) {
		super();
		missionID = payload.getInt("missionID");
		onConnectionLostMode = OnConnectionLostMode.valueOf(payload.getString("onConnectionLostMode"));
		parseJSONWaypoints(payload.getJSONArray("waypoints"));
		messageMemory.add(this);
	}
	

	@Override
	public boolean execute(Autopilot autopilot) {
		this.autopilot = autopilot;
		
		autopilot.send(getMissionCountPacket());
		int iteration = 0;
		for (Waypoint waypoint : waypoints) {
			waitForMissionRequestPacket(iteration);
			waypoint.execute(autopilot);
			iteration++;
		}
		if(waitForMissionAckPacket()) return false;
		
		
		return true;
	}
	private MAVLinkPacket getMissionCountPacket() {
		msg_mission_count mission_count = new msg_mission_count();
//		mission_count.target_component = autopilot.droneCompID;
//		mission_count.target_system = autopilot.droneSysID;
//		mission_count.count = waypoints.length;
//		mission_count.compid = autopilot.connectorCompID;
//		mission_count.sysid = autopilot.connectorSysID;
		MAVLinkPacket packet = mission_count.pack();
//		packet.seq = autopilot.getSeq();
		return packet;
	}


	private void parseJSONWaypoints(JSONArray jsonWaypoints) {
		jsonWaypoints.forEach(waypoint -> {
			JSONObject wp = (JSONObject) waypoint;
			waypoints.add(new Waypoint(wp.getString("type"),wp.getFloat("longitude"), wp.getFloat("latitude"),wp.getInt("altitude")));
		});
	}

	private boolean waitForMissionAckPacket() {
//		try {
//			autopilot.receiver.missionAckCondition.await(1, TimeUnit.SECONDS);
//		} catch (InterruptedException e) {
//			return false;
//		}
		return true;
	}

	private boolean waitForMissionRequestPacket(int seq) {
		// TODO: was ist wenn ein weiteres Request Element zwischen dem warten und
		// auslesen ins MessageMemory eingefügt wurde, welches nicht mit der ID
		// übereinstimmt.
		// Dann müsste man sich die vorherigen Elemente auch noch anschauen, aber auch
		// vergleichen, nicht dass es ein altes Element noch vor dem absenden des Count
		// ist
		// Und es müsste dann evtl alles nochmal probiert werden, falls das nicht eine
		// Ebene weiter oben durchgeführt wird (z.b. für die ganze Mission)
//		try {
//			autopilot.receiver.missionRequestCondition.await(1, TimeUnit.SECONDS);
//		} catch (InterruptedException e) {
//			return false;
//		}
		if (MissionRequest.messageMemory.getNewestElement().seq == seq) {
			return true;
		} else {
			return false;
		}

	}
	public static MessageMemory<Mission> getMessageMemory() {
		return messageMemory;
	}
	@Override
	public String toString() {
		final int maxLen = 10;
		return "Mission [autopilot=" + autopilot + ", missionID=" + missionID + ", onConnectionLostMode="
				+ onConnectionLostMode + ", waypoints="
				+ (waypoints != null ? waypoints.subList(0, Math.min(waypoints.size(), maxLen)) : null) + "]";
	}

	
}
