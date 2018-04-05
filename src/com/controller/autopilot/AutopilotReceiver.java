package com.controller.autopilot;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import com.MAVLink.MAVLinkPacket;
import com.MAVLink.Parser;
import com.MAVLink.Messages.MAVLinkMessage;
import com.MAVLink.common.msg_attitude;
import com.MAVLink.common.msg_command_ack;
import com.MAVLink.common.msg_heartbeat;
import com.MAVLink.common.msg_home_position;
import com.MAVLink.common.msg_mission_ack;
import com.MAVLink.common.msg_mission_count;
import com.MAVLink.common.msg_mission_current;
import com.MAVLink.common.msg_mission_item;
import com.MAVLink.common.msg_mission_request;
import com.MAVLink.common.msg_scaled_pressure;
import com.MAVLink.common.msg_vfr_hud;
import com.fazecast.jSerialComm.SerialPort;
import com.telemetry.Attitude;
import com.telemetry.Heartbeat;

public class AutopilotReceiver extends Thread {

	private SerialPort port;
//	private long start=System.currentTimeMillis();
//	private double counter = 0;
	public AutopilotReceiver(SerialPort port) {
		this.port=port;
		start();
	}

	@Override
	public void run() {
		readUDP();
		while (true) {
			//MAVLinkPacket mavpacket = getPacket(port);
			//handlePacket(mavpacket);
		}
	}
	
	private void handlePacket(MAVLinkPacket mavpacket) {
		//System.out.println("mavpacket id: "+mavpacket.msgid);
		
		switch (mavpacket.msgid) {
		case msg_heartbeat.MAVLINK_MSG_ID_HEARTBEAT:
			Heartbeat heartbeat = new Heartbeat(new msg_heartbeat(mavpacket));
			break;
		case msg_attitude.MAVLINK_MSG_ID_ATTITUDE:
			Attitude attitude = new Attitude(new msg_attitude(mavpacket));
			break;
		case msg_command_ack.MAVLINK_MSG_ID_COMMAND_ACK:
			msg_command_ack ack = new msg_command_ack(mavpacket);
			//System.out.println(ack);
			break;
		case msg_home_position.MAVLINK_MSG_ID_HOME_POSITION:
			msg_home_position hp = new msg_home_position();
			//System.out.println("HOME POINT FOUND!");
			//System.out.println(hp);
			break;
		case msg_mission_ack.MAVLINK_MSG_ID_MISSION_ACK:
			msg_mission_ack ack2 = new msg_mission_ack(mavpacket);
			System.out.println(ack2.toString());
			break;
		case msg_mission_request.MAVLINK_MSG_ID_MISSION_REQUEST:
			msg_mission_request req = new msg_mission_request(mavpacket);
			//System.out.println("FOUND REQUEST!");
			System.out.println(req.toString());
			break;
		case msg_mission_item.MAVLINK_MSG_ID_MISSION_ITEM:
			msg_mission_item item = new msg_mission_item(mavpacket);
			//System.out.println(item);
			break;
		case msg_mission_count.MAVLINK_MSG_ID_MISSION_COUNT:
			msg_mission_count count = new msg_mission_count(mavpacket);
			//System.out.println(count);
			break;
		case msg_mission_current.MAVLINK_MSG_ID_MISSION_CURRENT:
			msg_mission_current current = new msg_mission_current(mavpacket);
			//System.out.println(current);
			break;
		case msg_vfr_hud.MAVLINK_MSG_ID_VFR_HUD:
			msg_vfr_hud hud = new msg_vfr_hud(mavpacket);
			System.out.println("Altitude: " + hud.alt + "m\tGroundspeed: " + hud.groundspeed + "m/s\tHeading: " + hud.heading);
			break;
		default:
			//System.out.println("got: "+mavpacket.msgid);
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
