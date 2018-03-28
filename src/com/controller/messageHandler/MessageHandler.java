package com.controller.messageHandler;

import java.util.concurrent.CountDownLatch;

public class MessageHandler extends Thread {

	
	public MessageHandler() {
		this.setName("MessageHandler");
		start();
	}
	
	@Override
	public void run() {
		CountDownLatch latch = new CountDownLatch(1);
		
		try {
			latch.await();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	

}
