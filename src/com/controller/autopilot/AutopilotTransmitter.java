package com.controller.autopilot;

import com.fazecast.jSerialComm.SerialPort;

public class AutopilotTransmitter extends Thread{
	
	private SerialPort port;
	public AutopilotTransmitter(SerialPort port) {
		this.port=port;
		start();
	}
	
	@Override
	public void run() {
		
	}

}
