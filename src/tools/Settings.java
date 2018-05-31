package tools;

import com.MAVLink.enums.MAV_FRAME;

public class Settings {
	
	private static Settings instance;
	
	int serialPort = 0; // raspi = 0; surfacePro4 = 2
	int loiterRadius = 50;
	int abortAltitude = 10;
	int takeOffPitch = 10;
	short frameOrientation = MAV_FRAME.MAV_FRAME_GLOBAL_RELATIVE_ALT;
	
	boolean startAutopilot = true;
	boolean startMessageHandler = true;
	boolean startSocketConnection = true;
	boolean useUDP = true;
	
	String socketURI = "ws://localhost/connector";
	String socketAPIKey = "myapikey";
	
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

	public boolean getStartAutopilot() {
		return startAutopilot;
	}

	public void setStartAutopilot(boolean startAutopilot) {
		this.startAutopilot = startAutopilot;
	}

	public boolean getStartMessageHandler() {
		return startMessageHandler;
	}

	public void setStartMessageHandler(boolean startMessageHandler) {
		this.startMessageHandler = startMessageHandler;
	}

	public boolean getStartSocketConnection() {
		return startSocketConnection;
	}

	public void setStartSocketConnection(boolean startSocketConnection) {
		this.startSocketConnection = startSocketConnection;
	}

	public boolean getUseUDP() {
		return useUDP;
	}

	public void setUseUDP(boolean useUDP) {
		this.useUDP = useUDP;
	}

	public String getSocketURI() {
		return socketURI+"?apikey="+this.getSocketAPIKey();
	}
	public String getSocketAPIKey() {
		return socketAPIKey;
	}
	
	public void setSocketURI(String socketURI) {
		this.socketURI = socketURI;
	}
	public void setSocketAPIKey(String socketAPIKey) {
		this.socketAPIKey = socketAPIKey;
	}
}
