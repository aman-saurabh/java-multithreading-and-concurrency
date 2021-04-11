package com.tp.multithreading;

/*
 * Thread interrupt method is mainly used to interrupt any waiting or sleeping thread.
 */
public class Part10_ThreadInterruptMethod {
	public static Thread mainThreadRef;
	public static void childrun() {
		for (int i = 1; i <= 10; i++) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				System.err.println(Thread.currentThread().getName()+" got interrupted.\n");
				//To skip current iteration where exception occur.
				continue;
			}
			System.out.println(Thread.currentThread().getName()+" - Count : "+i);
		}
	}
	
	public static void main(String[] args) {
		mainThreadRef = Thread.currentThread();
		Runnable r1 = Part10_ThreadInterruptMethod:: childrun;
		Runnable r2 = () -> {
			for (int i = 1; i <= 10; i++) {
				System.out.println(Thread.currentThread().getName()+" - Count : "+i);
				if(i ==5) {
					try {
						mainThreadRef.join();
					} catch (InterruptedException e) {
						System.err.println(Thread.currentThread().getName()+" got interrupted.\n");
						//To skip all iterations from where exception occur.
						break;
					}
				}
			}
		};
		Thread t1 = new Thread(r1, "FirstChild");
		Thread t2 = new Thread(r2, "SecondChild");
		t1.start();
		t2.start();
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		t1.interrupt();
		t2.interrupt();
		System.out.println("Main thread execution completes");
	}
}
