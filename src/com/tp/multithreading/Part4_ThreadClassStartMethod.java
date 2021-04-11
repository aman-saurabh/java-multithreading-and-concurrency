package com.tp.multithreading;

/*
 * Thread class start method is responsible to register the thread with thread-scheduler, perform all other mandatory activities and invoke run() method.So it is also referred to as 'Heart of multi-threading'.
 * If we override start method the it won't be able to perform all these activities.So overriding of start method is certainly not recommended.
 * However still if you want to override start() method then call 'super.start()' method from the overridden method.Otherwise no new thread will be created.
 */

class CustomThread1 extends Thread {
	@Override
	public synchronized void start() {
		//Not calling super class start() method.
		System.out.println("CustomThread1 start method called");
	};
	
	@Override
	public void run() {
		System.out.println("CustomThread1 run method called");
	};
}

class CustomThread2 extends Thread {
	@Override
	public synchronized void start() {
		//Calling super class start() method.
		super.start();
		System.out.println("CustomThread2 start method called");
	};
	
	@Override
	public void run() {
		System.out.println("CustomThread2 run method called");
	};
}

public class Part4_ThreadClassStartMethod {
	public static void main(String[] args) {
		Thread t1 = new CustomThread1();
		Thread t2 = new CustomThread2();
		t1.start();
		t2.start();
		System.out.println("Main thread execution ends");
	}
}
