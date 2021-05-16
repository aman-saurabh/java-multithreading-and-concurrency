package com.tp.concurrency.lock;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

class MyThread extends Thread {
	private static final ReentrantLock l = new ReentrantLock();
	
	public MyThread(String name) {
		//To set name of the thread.
		super(name);
	}
	
	@Override
	public void run() {
		boolean isCompleted = false;
		int count = 0;
		while (!isCompleted) {
			try {
				count++;
				boolean getLock = l.tryLock(1, TimeUnit.SECONDS);
				if(getLock) {
					System.out.format("%s thread got the lock in attempt - %d.\n", Thread.currentThread().getName(), count);
					Thread.sleep(3000);
					System.out.format("%s thread has completed its task and now releasing the lock.\n",Thread.currentThread().getName());
					l.unlock();
					isCompleted = true;
				} else {
					System.out.format("%s thread is unable to get the lock in attempt - %d.Will retry.\n",Thread.currentThread().getName(), count);
				}
			} catch (Exception e) {
				System.err.println(e.getMessage());
			}
		}
	}
}

public class Part03_ReentrantLockExample2 {
	public static void main(String[] args) {
		MyThread t1 = new MyThread("first");
		MyThread t2 = new MyThread("second");
		MyThread t3 = new MyThread("third");
		t1.start();
		t2.start();
		t3.start();
		System.out.println("Main method called");
	}
}
