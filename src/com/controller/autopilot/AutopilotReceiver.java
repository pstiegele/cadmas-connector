package com.controller.autopilot;

import com.MAVLink.MAVLinkPacket;
import com.MAVLink.Parser;
import com.MAVLink.common.msg_attitude;
import com.MAVLink.common.msg_command_ack;
import com.MAVLink.common.msg_heartbeat;
import com.MAVLink.common.msg_home_position;
import com.MAVLink.common.msg_mission_ack;
import com.MAVLink.common.msg_mission_count;
import com.MAVLink.common.msg_mission_item;
import com.MAVLink.common.msg_mission_request;
import com.MAVLink.common.msg_scaled_pressure;
import com.fazecast.jSerialComm.SerialPort;
import com.telemetry.Attitude;
import com.telemetry.CommandAck;
import com.telemetry.Heartbeat;
import com.telemetry.ScaledPressure;

public class AutopilotReceiver extends Thread {

	private SerialPort port;
	private Autopilot autopilot;
	public AutopilotReceiver(SerialPort port, Autopilot autopilot) {
		this.port=port;
		this.autopilot=autopilot;
		start();
	}

	@Override
	public void run() {
		while (true) {
			MAVLinkPacket mavpacket = getPacket(port);
			handlePacket(mavpacket);
		}
	}

	private void handlePacket(MAVLinkPacket mavpacket) {
		
		
		switch (mavpacket.msgid) {
		case msg_heartbeat.MAVLINK_MSG_ID_HEARTBEAT:
			Heartbeat heartbeat = new Heartbeat(mavpacket);
			
			break;
		case msg_attitude.MAVLINK_MSG_ID_ATTITUDE:
			Attitude attitude = new Attitude(mavpacket);
			break;
		case msg_scaled_pressure.MAVLINK_MSG_ID_SCALED_PRESSURE:
			ScaledPressure pressure = new ScaledPressure(mavpacket);
			break;
		case msg_command_ack.MAVLINK_MSG_ID_COMMAND_ACK:
			CommandAck commandAck = new CommandAck(mavpacket);
			break;
		case msg_home_position.MAVLINK_MSG_ID_HOME_POSITION:
			msg_home_position hp = new msg_home_position();
			System.out.println("HOME POINT FOUND!");
			System.out.println(hp);
			break;
		case msg_mission_ack.MAVLINK_MSG_ID_MISSION_ACK:
			msg_mission_ack ack2 = new msg_mission_ack();
			System.out.println(ack2.toString());
			break;
		case msg_mission_request.MAVLINK_MSG_ID_MISSION_REQUEST:
			//msg_mission_request req = new msg_mission_request(mavpacket);
			System.out.println("FOUND REQUEST!");
			//System.out.println(mavpacket);
			break;
		case msg_mission_item.MAVLINK_MSG_ID_MISSION_ITEM:
			msg_mission_item i = new msg_mission_item(mavpacket);
			System.out.println("FOUND Mission Item!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
			System.out.println(i);
		case msg_mission_count.MAVLINK_MSG_ID_MISSION_COUNT:
			msg_mission_count count = new msg_mission_count(mavpacket);
			System.out.println(count);
			break;
		default:
			//System.out.println("get: "+mavpacket.msgid);
			break;
		}

	}

	/**
	 * @param port
	 *            The SerialPort
	 * @return return MAVLinkPacket
	 */
	private MAVLinkPacket getPacket(SerialPort port) {
		Parser parser = new Parser();
		try {
			while (true) {
				while (port.bytesAvailable() == 0)
					Thread.sleep(20);
				int bytesToRead = port.bytesAvailable();
				byte[] readBuffer = new byte[bytesToRead];
				port.readBytes(readBuffer, bytesToRead);
				int readarr[] = new int[bytesToRead];
				for (int i = 0; i < readBuffer.length; i++) {
					readarr[i] = unsignedToBytes(readBuffer[i]);
				}
				MAVLinkPacket mavpacket = null;
				for (int i = 0; i < readarr.length; i++) {
					mavpacket = parser.mavlink_parse_char(readarr[i]);
					if (mavpacket != null) {
						return mavpacket;

					}
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;

	}

	private static int unsignedToBytes(byte a) {
		int b = a & 0xFF;
		return b;
	}
}
