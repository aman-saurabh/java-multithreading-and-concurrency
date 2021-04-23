package com.tp.multithreading;

public class Part19_DaemonThread {
	public static void main(String[] args) {
		Runnable r = () -> {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
		};
		System.out.format("%s - is thread daemon : %b", Thread.currentThread().getName(), Thread.currentThread().isDaemon());
		Thread t1 = new Thread(r);
		System.out.format("t1 - is thread daemon before setting it manually : %b", t1.isDaemon());
		t1.setDaemon(true);
		System.out.format("t1 - is thread daemon after setting it manually : %b", t1.isDaemon());
	}
}
