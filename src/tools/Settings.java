package tools;

import com.MAVLink.enums.MAV_FRAME;

public class Settings {
	
	private static Settings instance;
	
	//setup values
	int serialPort = 3; // raspi = 3; yoga: 0
	int baudRate = 115200; // raspi = 115200; yoga = 9600;
	int telemetryRefreshRate = 1000;
	
	//autopilot values
	int loiterRadius = 50;
	int abortAltitude = 10;
	int takeOffPitch = 10;
	short frameOrientation = MAV_FRAME.MAV_FRAME_GLOBAL_RELATIVE_ALT;
	
	//debugging values
	boolean startAutopilot = true;
	boolean startMessageHandler = true;
	boolean startSocketConnection = true;
	boolean startCameraTransmission = true;
	boolean emulateCamera = true;
	boolean emulateCpuTemp = true;
	int cameraIntervall = 2000;		//in ms
	boolean useUDP = true;
	String udpIPAdress = "localhost";
	int udpOutgoingPort = 63091;
	boolean retryOpenArdupilotPort = false;
	
	//socket settings
	//String socketURI = "wss://cadmas.net:8081/connector"; //wss://cadmas.net:8081/connector
	String socketURI = "ws://localhost/connector";
	//String socketURI = "ws://192.168.188.23/connector";
	//String socketAPIKey = "$2a$11$aLcbCzL/19eDRRe7ggLoQeI5nV85mmDoql06uoX4IZBgIZiwP8K5i";
	String socketAPIKey = "myapikey";
	String emulateCameraFilePath = "C:\\Users\\pstiegele\\Documents\\Workspaces\\Git\\cameratest";
	
	private Settings(){
		
	}
	
	//creates settings object as singleton
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
	
	public boolean setBaudRate(int baudRate){
		this.baudRate = baudRate;
		return true;
	}
	
	public int getBaudRate() {
		return baudRate;
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
	public void setStartCameraTransmission(boolean startCameraTransmission) {
		this.startCameraTransmission = startCameraTransmission;
	}
	
	public boolean getStartCameraTransmission() {
		return startCameraTransmission;
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

	public int getUdpOutgoingPort() {
		return udpOutgoingPort;
	}

	public void setUdpOutgoingPort(int udpOutgoingPort) {
		this.udpOutgoingPort = udpOutgoingPort;
	}

	public String getUdpIPAdress() {
		return udpIPAdress;
	}

	public void setUdpIPAdress(String udpIPAdress) {
		this.udpIPAdress = udpIPAdress;
	}

	public int getCameraIntervall() {
		return cameraIntervall;
	}

	public void setCameraIntervall(int cameraIntervall) {
		this.cameraIntervall = cameraIntervall;
	}
	public boolean getRetryOpenArdupilotPort() {
		return retryOpenArdupilotPort;
	}
	public void setRetryOpenArdupilotPort(boolean retryOpenArdupilotPort) {
		this.retryOpenArdupilotPort = retryOpenArdupilotPort;
	}

	public boolean getEmulateCamera() {
		return emulateCamera;
	}

	public void setEmulateCamera(boolean emulateCamera) {
		this.emulateCamera = emulateCamera;
	}

	public int getTelemetryRefreshRate() {
		return telemetryRefreshRate;
	}

	public void setTelemetryRefreshRate(int telemetryRefreshRate) {
		this.telemetryRefreshRate = telemetryRefreshRate;
	}

	public boolean getEmulateCpuTemp() {
		return emulateCpuTemp;
	}

	public void setEmulateCpuTemp(boolean emulateCpuTemp) {
		this.emulateCpuTemp = emulateCpuTemp;
	}

	public String getEmulateCameraFilePath() {
		return emulateCameraFilePath;
	}

	public void setEmulateCameraFilePath(String emulateCameraFilePath) {
		this.emulateCameraFilePath = emulateCameraFilePath;
	}
}
