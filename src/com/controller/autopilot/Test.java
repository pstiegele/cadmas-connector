package com.controller.autopilot;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class Test {
	AutopilotTransmitter transmitter;
	
	public Test(AutopilotTransmitter transmitter) {
		this.transmitter = transmitter;
	}
	
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

	public ArrayList<CustomMissionItem> generateFromCSV() {
		CustomMissionItem item;
		ArrayList<CustomMissionItem> mission = new ArrayList<>();
		File file = new File("resources/mission1.txt");
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
	
	public int runTest() throws UnknownHostException, SocketException {
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
	
	public void waitMillis(long t) {
		try {
			Thread.sleep(t);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

}
