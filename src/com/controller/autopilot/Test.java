package com.controller.autopilot;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;

import com.MAVLink.enums.MAV_RESULT;

public class Test {
	AutopilotTransmitter transmitter;
	
	public Test(AutopilotTransmitter transmitter) {
		this.transmitter = transmitter;
	}
	
	//returns a random mission of a specific size and location
	public ArrayList<CustomMissionItem> randomMissionGenerator(int size, float baseLat, float baseLong, int minAlt,
			int maxAlt) {
		float variance = 0.1f; // in degrees
		CustomMissionItem item;
		ArrayList<CustomMissionItem> mission = new ArrayList<>();
		for (int i = 0; i < size; i++) {
			item = new CustomMissionItem(0, baseLat - variance / 2 + (float) (Math.random() * variance),
					baseLong - variance / 2 + (float) (Math.random() * variance),
					minAlt + (int) (Math.random() * (maxAlt - minAlt)));
			mission.add(item);
		}
		System.out.println("mission with " + size + " items generated");
		return mission;
	}

	//returns a mission from a csv file formatted like this: [waypoint_number];[type];[latitude];[longitude];[altitude] and no header line
	public ArrayList<CustomMissionItem> generateFromCSV(String filename) {
		CustomMissionItem item;
		ArrayList<CustomMissionItem> mission = new ArrayList<>();
		String filepath = "resources/" + filename + ".txt";
		File file = new File(filepath);
		try {
			BufferedReader FileReader = new BufferedReader(new FileReader(file));

			String line = "";

			while (null != (line = FileReader.readLine())) {
				String[] split = line.split(";");
				item = new CustomMissionItem(Integer.parseInt(split[1]), Float.parseFloat(split[2]),
						Float.parseFloat(split[3]), Integer.parseInt(split[4]));
				mission.add(item);
			}
			FileReader.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("waypoint file read");
		return mission;
	}
	
	//performs a full automated test flight and executes most commands
	public int runTestFlight() throws UnknownHostException, SocketException {
		waitMillis(10000);
		long timestamp = 0;
		
		//Counter starten
		System.out.println("counter started.");
		long startTime = System.currentTimeMillis();
		waitMillis(1000);
		timestamp = 10;
		while(System.currentTimeMillis()-startTime < timestamp*1000) {
			waitMillis(10);
		}
		
		//setMode manual
		System.out.println("setMode manual: " + transmitter.setMode(FlightMode.MANUAL));
		waitMillis(1000);
		timestamp = 20;
		while(System.currentTimeMillis()-startTime < timestamp*1000) {
			waitMillis(10);
		}
		
		//preflight calibration
		System.out.println("preflight calibration: " + transmitter.calibrate());
		waitMillis(1000);
		timestamp = 40;
		while(System.currentTimeMillis()-startTime < timestamp*1000) {
			waitMillis(10);
		}
		
		//sendMission1
		System.out.println("sendMission 1: " + transmitter.sendMission(generateFromCSV("mission1"), true));
		waitMillis(1000);
		timestamp = 70;
		while(System.currentTimeMillis()-startTime < timestamp*1000) {
			waitMillis(10);
		}
		
		//arm
		System.out.println("arm: " + transmitter.arm());
		waitMillis(1000);
		timestamp = 80;
		while(System.currentTimeMillis()-startTime < timestamp*1000) {
			waitMillis(10);
		}
		
		//setHome1
		System.out.println("setHome 1: " + transmitter.setHomePosition(generateFromCSV("home1").get(0)));
		waitMillis(1000);
		timestamp = 90;
		while(System.currentTimeMillis()-startTime < timestamp*1000) {
			waitMillis(10);
		}
		
		//setMode auto
		System.out.println("setMode auto: " + transmitter.setMode(FlightMode.AUTO));
		waitMillis(1000);
		timestamp = 150;
		while(System.currentTimeMillis()-startTime < timestamp*1000) {
			waitMillis(10);
		}
		
		//sendMission2
		System.out.println("sendMission 2: " + transmitter.sendMission(generateFromCSV("mission4"), false));
		waitMillis(1000);
		timestamp = 180;
		while(System.currentTimeMillis()-startTime < timestamp*1000) {
			waitMillis(10);
		}
		
		//setMode rtl
		System.out.println("setMode rtl: " + transmitter.setMode(FlightMode.RTL));
		waitMillis(1000);
		timestamp = 240;
		while(System.currentTimeMillis()-startTime < timestamp*1000) {
			waitMillis(10);
		}
		
		//sendMission3
		System.out.println("sendMission 3: " + transmitter.sendMission(generateFromCSV("mission3"), true));
		waitMillis(1000);
		timestamp = 270;
		while(System.currentTimeMillis()-startTime < timestamp*1000) {
			waitMillis(10);
		}
		
		//setMode auto
		System.out.println("setMode auto: " + transmitter.setMode(FlightMode.AUTO));
		waitMillis(1000);
		timestamp = 300;
		while(System.currentTimeMillis()-startTime < timestamp*1000) {
			waitMillis(10);
		}
		
		//setHome2
		System.out.println("setHome 2: " + transmitter.setHomePosition(generateFromCSV("home2").get(0)));
		waitMillis(1000);
		timestamp = 310;
		while(System.currentTimeMillis()-startTime < timestamp*1000) {
			waitMillis(10);
		}
		
		//setMode rtl
		System.out.println("setMode rtl: " + transmitter.setMode(FlightMode.RTL));
		waitMillis(1000);
		timestamp = 340;
		while(System.currentTimeMillis()-startTime < timestamp*1000) {
			waitMillis(10);
		}
		
		//setMode auto
		System.out.println("setMode auto: " + transmitter.setMode(FlightMode.AUTO));
		
		return MAV_RESULT.MAV_RESULT_ACCEPTED;
	}
	
	//performs a full automated ground test of every command and returns a bitmask to identify every failed test number
	public int runGroundTest() throws UnknownHostException, SocketException {
		waitMillis(10000);
		int result = 0;
		
		System.out.println("running test..");
		
		waitMillis(1000);
		System.out.println("Test: disarm, but already disarmed. expecting fail.");
		if(transmitter.disarm() == 0) {
			result += 1;
		}
		
		waitMillis(2000);
		System.out.println("Test: setHome, but disarmed. expecting fail.");
		if(transmitter.setHomePosition(new CustomMissionItem(0, 50.123456f, 10.123456f, 123)) == 0) {
			result += 2;
		}
		
		waitMillis(2000);
		System.out.println("Test: setMode stabilize. expecting success.");
		if(transmitter.setMode(FlightMode.STABILIZE) != 0) {
			result += 4;
		}
		
		waitMillis(2000);
		System.out.println("Test: setMode manual. expecting success.");
		if(transmitter.setMode(FlightMode.MANUAL) != 0) {
			result += 8;
		}
		
		waitMillis(2000);
		System.out.println("Test: arm. expecting success." );
		if(transmitter.arm() != 0) {
			result += 16;
		}
		
		waitMillis(2000);
		System.out.println("Test: setHome. expecting success.");
		if(transmitter.setHomePosition(new CustomMissionItem(0, 50.123456f, 10.123456f, 123)) != 0) {
			result += 32;
		}
		
		waitMillis(1000);
		System.out.println("Test: getHome. expecting success.");
		if(!transmitter.getHomePosition().equals(new CustomMissionItem(0, 50.123456f, 10.123456f, 123))) {
			System.out.println(transmitter.getHomePosition().toString());
			result += 64;
		}
		
		waitMillis(2000);
		System.out.println("Test: disarm. expecting success.");
		if(transmitter.disarm() != 0) {
			result += 128;
		}
		
		waitMillis(2000);
		System.out.println("Test: calibrate. expecting success.");
		if(transmitter.calibrate() != 0) {
			result += 256;
		}
		
		waitMillis(2000);
		System.out.println("Test: sendMission. expecting success.");
		ArrayList<CustomMissionItem> mission = randomMissionGenerator(20, 50, 10, 100, 200);
		
		if(transmitter.sendMission(mission, true) != 0) {
			result += 512;
		}
		
		waitMillis(2000);
		System.out.println("Test: getMission. expecting success.");
		ArrayList<CustomMissionItem> receivedMission = transmitter.getMission();
		if(receivedMission.size() != mission.size()) {
			result += 1024;
		}
		else {
			for(int i = 0; i < mission.size(); i++) {
				if(!mission.get(i).equals(receivedMission.get(i))) {
					result += 1024;
					System.out.println("test finished.. result: " + result);
					return result;
				}
			}
		}
		
		System.out.println("test finished.. result: " + result);
		return result;
	}
	
	//waits for a specified amount of time in milliseconds
	public void waitMillis(long t) {
		try {
			Thread.sleep(t);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

}
