package com.controller.autopilot;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import com.MAVLink.MAVLinkPacket;
import com.MAVLink.Parser;
import com.MAVLink.common.msg_altitude;
import com.MAVLink.common.msg_attitude;
import com.MAVLink.common.msg_battery_status;
import com.MAVLink.common.msg_command_ack;
import com.MAVLink.common.msg_global_position_int;
import com.MAVLink.common.msg_heartbeat;
import com.MAVLink.common.msg_home_position;
import com.MAVLink.common.msg_mission_ack;
import com.MAVLink.common.msg_mission_count;
import com.MAVLink.common.msg_mission_current;
import com.MAVLink.common.msg_mission_item;
import com.MAVLink.common.msg_mission_request;
import com.MAVLink.common.msg_position_target_global_int;
import com.MAVLink.common.msg_statustext;
import com.MAVLink.common.msg_vfr_hud;
import com.fazecast.jSerialComm.SerialPort;
import com.telemetry.Altitude;
import com.telemetry.Attitude;
import com.telemetry.Battery;
import com.telemetry.CommandAck;
import com.telemetry.Heartbeat;
import com.telemetry.MissionItem;
import com.telemetry.MissionState;
import com.telemetry.Position;
import com.telemetry.Velocity;

public class AutopilotReceiver extends Thread {

	boolean udpInsteadOfSerial = false;
	private SerialPort port;
	private int previousSequence = 0;
	int sequenceLogSize = 5;
	int[] sequenceLog = new int[sequenceLogSize];
	int arrayIndex = 0;
//	private long start=System.currentTimeMillis();
//	private double counter = 0;
	public AutopilotReceiver(SerialPort port) {
		this.port=port;
		start();
	}

	@Override
	public void run() {
		//udpInsteadOfSerial = true;
		
		if(udpInsteadOfSerial){
			readUDP();
		}
		else{
			while (true) {
				MAVLinkPacket mavpacket = getPacket(port);
				handlePacket(mavpacket);
			}
		}
	}
	
	private void handlePacket(MAVLinkPacket mavpacket) {
		//System.out.println("mavpacket id: "+mavpacket.msgid);
		//System.out.println("Packet sequence: " + mavpacket.seq);
		calcRSSI(mavpacket);
		
		switch (mavpacket.msgid) {
		case msg_statustext.MAVLINK_MSG_ID_STATUSTEXT:
			new CommandAck(new msg_statustext(mavpacket));
			//System.out.println(new String(new msg_statustext(mavpacket).text));
			break;
		case msg_heartbeat.MAVLINK_MSG_ID_HEARTBEAT:
			new Heartbeat(new msg_heartbeat(mavpacket), sequenceLog);
			break;
		case msg_battery_status.MAVLINK_MSG_ID_BATTERY_STATUS:
			new Battery(new msg_battery_status(mavpacket));
			break;
		case msg_attitude.MAVLINK_MSG_ID_ATTITUDE:
			new Attitude(new msg_attitude(mavpacket));
			break;
		case msg_global_position_int.MAVLINK_MSG_ID_GLOBAL_POSITION_INT:
			new Position(new msg_global_position_int(mavpacket));
			break;
		case msg_altitude.MAVLINK_MSG_ID_ALTITUDE:
			//System.out.println("altitude");
			new Altitude(new msg_altitude(mavpacket));
			break;
		case msg_vfr_hud.MAVLINK_MSG_ID_VFR_HUD:
			new Velocity(new msg_vfr_hud(mavpacket));
			//System.out.println("connected...");
			//System.out.println("Altitude: " + hud.alt + "m\tGroundspeed: " + hud.groundspeed + "m/s\tHeading: " + hud.heading);
			break;
		case msg_command_ack.MAVLINK_MSG_ID_COMMAND_ACK:
			new CommandAck(new msg_command_ack(mavpacket));
			//System.out.println(new msg_command_ack(mavpacket).toString());
			break;
		case msg_home_position.MAVLINK_MSG_ID_HOME_POSITION:
			new MissionItem(new msg_home_position(mavpacket));
			//msg_home_position hp = new msg_home_position(mavpacket);
			//System.out.println(hp);
			break;
		case msg_mission_ack.MAVLINK_MSG_ID_MISSION_ACK:
			//System.out.println(new msg_mission_ack(mavpacket).toString());
			new CommandAck(new msg_mission_ack(mavpacket));
			break;
		case msg_mission_request.MAVLINK_MSG_ID_MISSION_REQUEST:
			//System.out.println(new msg_mission_request(mavpacket).toString());
			new CommandAck(new msg_mission_request(mavpacket));
			break;
		case msg_mission_item.MAVLINK_MSG_ID_MISSION_ITEM:
			new MissionItem(new msg_mission_item(mavpacket));
			//msg_mission_item item = new msg_mission_item(mavpacket);
			//System.out.println(item);
			break;
		case msg_mission_count.MAVLINK_MSG_ID_MISSION_COUNT:
			new CommandAck(new msg_mission_count(mavpacket));
			//msg_mission_count count = new msg_mission_count(mavpacket);
			//System.out.println(count);
			break;
		case msg_mission_current.MAVLINK_MSG_ID_MISSION_CURRENT:
			new MissionState(new msg_mission_current(mavpacket));
			break;
		default:
			//System.out.println("got: "+mavpacket.msgid);
			break;
		}

	}
	
	private void calcRSSI(MAVLinkPacket mavpacket) {
		sequenceLog[arrayIndex] = Math.min(Math.abs(mavpacket.seq - previousSequence), mavpacket.seq + 255 - previousSequence) - 1;
		previousSequence = mavpacket.seq;
		
		if(arrayIndex < sequenceLogSize - 1) {
			arrayIndex++;
		}
		else {
			arrayIndex = 0;
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
				while (port.bytesAvailable() == 0){
					//Thread.sleep(20);
				}
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
	
	public void readUDP() {  
		Parser parser = new Parser();
		int port = 14551;
	      try {
		        DatagramSocket dSocket = new DatagramSocket(port);
		        byte[] buffer = new byte[2048];
	
		        System.out.printf("Listening on udp:%s:%d%n", InetAddress.getLocalHost().getHostAddress(), port);     
		        DatagramPacket receivePacket = new DatagramPacket(buffer, buffer.length);
	
		        while(true) {
		              dSocket.receive(receivePacket);
		              byte[] receiveArray = new byte[receivePacket.getLength()];
		              for (int i = 0; i < receiveArray.length; i++) {
						receiveArray[i] = buffer[i];
		              }
		              int readarr[] = new int[receiveArray.length];
						for (int i = 0; i < receiveArray.length; i++) {
							readarr[i] = unsignedToBytes(receiveArray[i]);
						}
						MAVLinkPacket mavpacket = null;
						for (int i = 0; i < readarr.length; i++) {
							mavpacket = parser.mavlink_parse_char(readarr[i]);
							if (mavpacket != null) {
								if(mavpacket.msgid == msg_mission_ack.MAVLINK_MSG_ID_MISSION_ACK){
									System.out.println("received on port: " + port);
								}
								//System.out.println("received message id: " + mavpacket.msgid + "\t size: " + mavpacket.len);
								//System.out.println(receivePacket.getAddress() + " port: " + receivePacket.getPort());
								handlePacket(mavpacket);
							}
						}
		              
		              // now send acknowledgement packet back to sender     
		              //InetAddress IPAddress = receivePacket.getAddress();
		              //String sendString = "polo";
		              //byte[] sendData = sendString.getBytes("UTF-8");
		              //DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, receivePacket.getPort());
		              //serverSocket.send(sendPacket);
		        }
	      } 
	      catch (IOException e) {
	              System.out.println(e);
	      }
	}

	private static int unsignedToBytes(byte a) {
		int b = a & 0xFF;
		return b;
	}
}
