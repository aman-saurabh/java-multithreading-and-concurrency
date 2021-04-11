package com.tp.multithreading;

/*
 * In this example if you remove 'synchronized' keyword from 'sayHello' method.Then you will see irregular output.
 * As in that case all 3 threads will start working on 'sayHello' method simultaneously 
 * and all three will print 'Hello' simultaneouly as 'Hello Hello Hello' and 
 * then all three will start waiting for 2000ms simultaneously and will print their threadname and 'Hello' again 
 * and this process will continue till last iteration and you will get output as follows:
 * -------------------------------------------------------------------------
 * Hello Hello Hello MainThread!
 * Hello SecondChildThread!
 * .................
 * .................
 * Hello MainThread!
 * SecondChildThread!
 * FirstChildThread!
 * -------------------------------------------------------------------------
 * To avoid such problems we should make such methods as synchronized.
 */
class Message {
	public synchronized void sayHello(String threadName) {
		for (int i = 0; i < 5; i++) {
			System.out.print("Hello ");
			try {
				Thread.sleep(2000);
			} catch (Exception e) {
				System.err.println("Interrupted exception occur.");
			}
			
			System.out.println(threadName+"!");
		}
	}
}

public class Part12_SynchronizedMethod {
	public static void main(String[] args) {
		Message msg = new Message();
		Runnable r = () -> {
			msg.sayHello(Thread.currentThread().getName());
		};
		
		Thread t1 = new Thread(r, "FirstChildThread");
		Thread t2 = new Thread(r, "SecondChildThread");
		t1.start();
		t2.start();
		Thread.currentThread().setName("MainThread");
		msg.sayHello(Thread.currentThread().getName());
	}
}
