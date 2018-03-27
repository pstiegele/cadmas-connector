package com.telecommand;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import com.MAVLink.MAVLinkPacket;
import com.MAVLink.Messages.MAVLinkMessage;
import com.MAVLink.common.msg_mission_count;
import com.controller.autopilot.Autopilot;
import com.telemetry.Heartbeat;
import com.telemetry.MissionRequest;

public class Mission implements TelecommandMessage {

	Waypoint[] waypoints;
	boolean clearExistingMission = true;
	Autopilot autopilot;

	public Mission(Waypoint[] waypoints, boolean clearExistingMission) {
		this.waypoints = waypoints;
		this.clearExistingMission = clearExistingMission;
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


	private boolean waitForMissionAckPacket() {
		try {
			autopilot.receiver.missionAckCondition.await(1, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			return false;
		}
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
		try {
			autopilot.receiver.missionRequestCondition.await(1, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			return false;
		}
		if (MissionRequest.messageMemory.getNewestElement().seq == seq) {
			return true;
		} else {
			return false;
		}

	}

	private MAVLinkPacket getMissionCountPacket() {
		msg_mission_count mission_count = new msg_mission_count();
		mission_count.target_component = autopilot.droneCompID;
		mission_count.target_system = autopilot.droneSysID;
		mission_count.count = waypoints.length;
		mission_count.compid = autopilot.connectorCompID;
		mission_count.sysid = autopilot.connectorSysID;
		MAVLinkPacket packet = mission_count.pack();
		packet.seq = autopilot.getSeq();
		return packet;
	}

}
