package com.tp.concurrency.lock;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

/*
 * Check details about Lock interface in the documents.
 * In this part we will see a basic example of how to use Lock interface with the help of its one implementation class name 'Reentrant class'.
 */

class Greet1 {
	ReentrantLock l = new ReentrantLock();

	public void sayGoodMorning(String name) {
		l.lock();
		try {
			for (int i = 0; i < 10; i++) {
				System.out.print("Good Morning ");
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					System.err.println(e.getMessage());
				}
				System.out.println(name);
			}
		} finally {
			System.out.println("Finally block executed by thread : " + Thread.currentThread().getName());
			l.unlock();
		}
	}
}

public class Part01_LockInterfaceBasics {
	public static void main(String[] args) {
		Greet1 greet = new Greet1();
		Map<String, String> threadUserMap = new HashMap<String, String>();

		Runnable r = () -> {
			String username = threadUserMap.get(Thread.currentThread().getName());
			greet.sayGoodMorning(username);
		};
		Thread t1 = new Thread(r, "first");
		threadUserMap.put(t1.getName(), "Aman");
		Thread t2 = new Thread(r, "second");
		threadUserMap.put(t2.getName(), "Saurabh");
		Thread t3 = new Thread(r, "third");
		threadUserMap.put(t3.getName(), "Manish");
		t1.start();
		t2.start();
		t3.start();
	}
}
