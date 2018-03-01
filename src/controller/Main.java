package controller;

public class Main {
	
	private static Autopilot autopilot = new Autopilot();
	private static SocketConnection socketConnection = new SocketConnection();

	public static void main(String[] args) {
		//connect to server
		socketConnection.connect(autopilot);
		//connect to autopilot
		autopilot.connect(socketConnection);
		
		
		
		

	}

}
