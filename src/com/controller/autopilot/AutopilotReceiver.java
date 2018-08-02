package com.controller.autopilot;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import com.MAVLink.MAVLinkPacket;
import com.MAVLink.Parser;
import com.MAVLink.common.msg_attitude;
import com.MAVLink.common.msg_command_ack;
import com.MAVLink.common.msg_global_position_int;
import com.MAVLink.common.msg_heartbeat;
import com.MAVLink.common.msg_home_position;
import com.MAVLink.common.msg_mission_ack;
import com.MAVLink.common.msg_mission_count;
import com.MAVLink.common.msg_mission_current;
import com.MAVLink.common.msg_mission_item;
import com.MAVLink.common.msg_mission_request;
import com.MAVLink.common.msg_statustext;
import com.MAVLink.common.msg_sys_status;
import com.MAVLink.common.msg_vfr_hud;
import com.fazecast.jSerialComm.SerialPort;
import com.telemetry.Attitude;
import com.telemetry.Battery;
import com.telemetry.CommandAck;
import com.telemetry.Heartbeat;
import com.telemetry.MissionItem;
import com.telemetry.MissionState;
import com.telemetry.Position;
import com.telemetry.Velocity;

import tools.Settings;

public class AutopilotReceiver extends Thread {

	boolean udpInsteadOfSerial = Settings.getInstance().getUseUDP();
	private SerialPort port;
	private int previousSequence;
	int sequenceLogSize = 5;
	int[] sequenceLog = new int[sequenceLogSize];
	int arrayIndex = 0;
	public AutopilotReceiver(SerialPort port) {
		this.port=port;
		start();
	}

	@Override
	public void run() {
		previousSequence = 0;
		waitMillis(2000);
		System.out.println("receiverStart");
		
		if(udpInsteadOfSerial){
			try {
				readUDP();
			} catch (SocketException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else{
			while (true) {
				MAVLinkPacket mavpacket = getPacket(port);
				handlePacket(mavpacket);
			}
		}
	}
	
	//creates different telemetry objects based on message id of mavlink packets
	private void handlePacket(MAVLinkPacket mavpacket) {
		calcRSSI(mavpacket);
		
		switch (mavpacket.msgid) {
		case msg_sys_status.MAVLINK_MSG_ID_SYS_STATUS:
			new Battery(new msg_sys_status(mavpacket));
			break;
		case msg_statustext.MAVLINK_MSG_ID_STATUSTEXT:
			new CommandAck(new msg_statustext(mavpacket));
			break;
		case msg_heartbeat.MAVLINK_MSG_ID_HEARTBEAT:
			new Heartbeat(new msg_heartbeat(mavpacket), sequenceLog, getCpuTemp());
			break;
		case msg_attitude.MAVLINK_MSG_ID_ATTITUDE:
			new Attitude(new msg_attitude(mavpacket));
			break;
		case msg_global_position_int.MAVLINK_MSG_ID_GLOBAL_POSITION_INT:
			new Position(new msg_global_position_int(mavpacket));
			break;
		case msg_vfr_hud.MAVLINK_MSG_ID_VFR_HUD:
			new Velocity(new msg_vfr_hud(mavpacket));
			break;
		case msg_command_ack.MAVLINK_MSG_ID_COMMAND_ACK:
			new CommandAck(new msg_command_ack(mavpacket));
			break;
		case msg_home_position.MAVLINK_MSG_ID_HOME_POSITION:
			new MissionItem(new msg_home_position(mavpacket));
			break;
		case msg_mission_ack.MAVLINK_MSG_ID_MISSION_ACK:
			new CommandAck(new msg_mission_ack(mavpacket));
			break;
		case msg_mission_request.MAVLINK_MSG_ID_MISSION_REQUEST:
			new CommandAck(new msg_mission_request(mavpacket));
			break;
		case msg_mission_item.MAVLINK_MSG_ID_MISSION_ITEM:
			new MissionItem(new msg_mission_item(mavpacket));
			break;
		case msg_mission_count.MAVLINK_MSG_ID_MISSION_COUNT:
			new CommandAck(new msg_mission_count(mavpacket));
			break;
		case msg_mission_current.MAVLINK_MSG_ID_MISSION_CURRENT:
			new MissionState(new msg_mission_current(mavpacket));
			break;
		default:
			break;
		}

	}
	
	//calculates connection quality based on messages lost
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
	
	//reads CPU temperature in celsius
		private float getCpuTemp() {
			float temp = 0;
			String fileName = "/sys/class/thermal/thermal_zone0/temp";
	        String line = null;

	        try {
	            FileReader fileReader = new FileReader(fileName);

	            BufferedReader bufferedReader = new BufferedReader(fileReader);

	            while((line = bufferedReader.readLine()) != null) {
	                temp = (Integer.parseInt(line) / 1000);
	            }

	            bufferedReader.close();
	        }
	        catch(FileNotFoundException ex) {
	            //System.out.println("Unable to open file '" + fileName + "'");
	        }
	        catch(IOException ex) {
	            //System.out.println("Error reading file '" + fileName + "'");
	        }
			return temp;
		}

	//returns a mavlink packet when it was received over serial connection
	private MAVLinkPacket getPacket(SerialPort port) {
		Parser parser = new Parser();
		try {
			while (true) {
				while (port.bytesAvailable() == 0){
				}
				int bytesToRead = port.bytesAvailable();
				while(bytesToRead < 0) {
					waitMillis(50);
				}
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
	
	//continuously reads incoming mavlink packets over UDP connection and passes packets to handlePacket() method
	public void readUDP() throws SocketException {
		Parser parser = new Parser();
		int port = 14551;
		DatagramSocket dSocket = new DatagramSocket(port);
	      try {
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
								Settings.getInstance().setUdpOutgoingPort(receivePacket.getPort());
								handlePacket(mavpacket);
							}
						}
		        }
	      } 
	      catch (IOException e) {
	              System.out.println(e);
	      }
	      dSocket.close();
	}

	//converts unsigned bytes to signed bytes
	private static int unsignedToBytes(byte a) {
		int b = a & 0xFF;
		return b;
	}
	
	//waits for a specified amount of time in milliseconds
	public void waitMillis(long t){
		try {
			Thread.sleep(t);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
}
