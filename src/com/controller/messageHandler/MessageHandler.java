package com.controller.messageHandler;

import java.util.concurrent.CountDownLatch;
import com.telecommand.Mission;

import javafx.collections.ListChangeListener;

public class MessageHandler extends Thread {

	
	public MessageHandler() {
		this.setName("MessageHandler");
		start();
	}
	
	@Override
	public void run() {
		CountDownLatch latch = new CountDownLatch(1);
		setListeners();
		
		try {
			latch.await();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void setListeners() {
		Mission.getMessageMemory().addListener((ListChangeListener<? super Mission>)(c ->{
			new Thread(new Runnable() {
			    @Override public void run() {
			    System.out.println("listener thread called from: "+Thread.currentThread().getName());
			    }
			}).start();
			
		}));
		
	}

	

}
