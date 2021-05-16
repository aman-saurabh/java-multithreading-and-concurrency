package com.tp.concurrency.lock;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/*
 * In this part we will see two features of Reentrant lock - 
 * 1.) How one thread can get multiple locks of same object
 * 2.) tryLock() method of Lock interface 
 */

public class Part02_ReentrantLockExample1 {
	public static void main(String[] args) {
		ReentrantLock rlock = new ReentrantLock();
		Runnable r = () -> {
			boolean getLock = false;
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh-mm-ss");
			// Trying for outer lock
			try {
				getLock = rlock.tryLock(8, TimeUnit.SECONDS);
				if (getLock) {
					System.out.format("Outer lock acquired by thread %s at %s, hold count : %d \n",
							Thread.currentThread().getName(), LocalTime.now().format(formatter), rlock.getHoldCount());
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					// Getting inner lock
					rlock.lock();
					try {
						System.out.format("Inner lock acquired by thread %s at %s, hold count : %d \n",
								Thread.currentThread().getName(), LocalTime.now().format(formatter),
								rlock.getHoldCount());
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						System.out.format("Thread %s releasing inner lock \n", Thread.currentThread().getName());
						Thread.sleep(1500);
						rlock.unlock();
					}
					System.out.format("Inner lock released by thread %s at %s, hold count : %d \n",
							Thread.currentThread().getName(), LocalTime.now().format(formatter), rlock.getHoldCount());
				} else {
					System.out.format("Thread %s can't get the lock,Hence performing alternate operations. \n",
							Thread.currentThread().getName());
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			} finally {
				//Applying condition is necessary.Otherwise it might throw IllegalMonitorStateException
				if(rlock.isHeldByCurrentThread()) {
					try {
						System.out.format("Thread %s releasing outer lock \n", Thread.currentThread().getName());
						Thread.sleep(1500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					rlock.unlock();
				}	
			}
		};
		Thread t1 = new Thread(r, "first");
		Thread t2 = new Thread(r, "second");
		Thread t3 = new Thread(r, "third");
		t1.start();
		t2.start();
		t3.start();
	}
}
