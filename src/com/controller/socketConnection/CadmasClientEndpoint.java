package com.controller.socketConnection;

import java.net.URI;

import javax.websocket.ClientEndpoint;
import javax.websocket.CloseReason;
import javax.websocket.ContainerProvider;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;

import org.json.JSONObject;



@ClientEndpoint
public class CadmasClientEndpoint {
	Session userSession = null;
	private SocketMessageHandler socketMessageHandler;

	public CadmasClientEndpoint(URI endpointURI) {
			try {
				WebSocketContainer container = ContainerProvider.getWebSocketContainer();
				container.connectToServer(this, endpointURI);
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("Cannot connect to Cadmas. Try again.");
			}
		
	}

	/**
     * Callback hook for Connection open events.
     * 
     * @param userSession
     *            the userSession which is opened.
     */
    @OnOpen
    public void onOpen(Session userSession) {
        this.userSession = userSession;
    }
 
    /**
     * Callback hook for Connection close events.
     * 
     * @param userSession
     *            the userSession which is getting closed.
     * @param reason
     *            the reason for connection close
     */
    @OnClose
    public void onClose(Session userSession, CloseReason reason) {
    	System.out.println("websocket connection closed");
        this.userSession = null;
    }
 
    /**
     * Callback hook for Message Events. This method will be invoked when a
     * client send a message.
     * 
     * @param message
     *            The text message
     */
    @OnMessage
    public void onMessage(String message) {
        if (this.socketMessageHandler != null)
            this.socketMessageHandler.handleMessage(message);
    }
 
    /**
     * register message handler
     * 
     * @param message
     */
    public void addMessageHandler(SocketMessageHandler msgHandler) {
        this.socketMessageHandler = msgHandler;
    }
 
    /**
     * Send a message.
     * 
     * @param user
     * @param message
     */
    public void sendMessage(String message) {
        this.userSession.getAsyncRemote().sendText(message);
    }
	
	public static interface SocketMessageHandler {
		public void handleMessage(String message);
	}
}
