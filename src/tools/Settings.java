package tools;

import com.MAVLink.enums.MAV_FRAME;

public class Settings {
	
	private static Settings instance;
	
	int serialPort = 2;
	int loiterRadius = 50;
	int abortAltitude = 10;
	int takeOffPitch = 10;
	short frameOrientation = MAV_FRAME.MAV_FRAME_GLOBAL_RELATIVE_ALT;
	
	private Settings(){
		
	}
	
	public static Settings getInstance(){
		if(Settings.instance == null){
			Settings.instance = new Settings();
		}
		return Settings.instance;
	}
	
	public boolean setSerialPort(int port){
		serialPort = port;
		return true;
	}
	
	public boolean setLoiterRadius(int radius){
		loiterRadius = radius;
		return true;
	}
	
	public boolean setTakeOffPitch(int pitch){
		takeOffPitch = pitch;
		return true;
	}
	
	public boolean setAbortAltitude(int altitude){
		abortAltitude = altitude;
		return true;
	}
	
	public boolean setFrameOrientation(short frame) {
		frameOrientation = frame;
		return true;
	}
	
	public int getSerialPort(){
		return serialPort;
	}
	
	public int getLoiterRadius(){
		return loiterRadius;
	}
	
	public int getAbortAltitude(){
		return abortAltitude;
	}
	
	public int getTakeOffPitch(){
		return takeOffPitch;
	}
	
	public short getFrameOrientation(){
		return frameOrientation;
	}
}
