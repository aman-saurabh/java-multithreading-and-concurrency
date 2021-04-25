package com.tp.concurrency;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class SyncQueue {
	// producer consumer problem data
	private static final int CAPACITY = 10;
	private final Queue<Integer> queue = new LinkedList<Integer>();
	// lock and condition variables
	private final Lock aLock = new ReentrantLock();
	private final Condition bufferHasNoSpace = aLock.newCondition();
	private final Condition bufferHasNoData = aLock.newCondition();

	public void put() throws InterruptedException {
		aLock.lock();
		try {
			while (queue.size() == CAPACITY) {
				System.out.println(Thread.currentThread().getName() + " : Buffer is full, waiting");
				bufferHasNoSpace.await();
			}
			int number = ThreadLocalRandom.current().nextInt(1000, 10000);
			boolean isAdded = queue.offer(number);
			if (isAdded) {
				System.out.printf("%s added %d into queue %n", Thread.currentThread().getName(), number);
				// signal consumer thread that, buffer has data now
				bufferHasNoData.signalAll();
			}
		} finally {
			aLock.unlock();
		}
	}

	public void get() throws InterruptedException {
		aLock.lock();
		try {
			while (queue.size() == 0) {
				System.out.println(Thread.currentThread().getName() + " : Buffer is empty, waiting");
				bufferHasNoData.await();
			}
			Integer value = queue.poll();
			if (value != null) {
				System.out.printf("%s consumed %d from queue %n", Thread.currentThread().getName(), value);
				// signal producer thread that, buffer has space now
				bufferHasNoSpace.signalAll();
			}
		} finally {
			aLock.unlock();
		}
	}
}

public class Part07_LockConditionClassExample1 {
	public static void main(String[] args) {
		SyncQueue queue = new SyncQueue();
		Runnable r1 = () -> {
			try {
				for (int i = 0; i < 20; i++) {
					queue.put();
					Thread.sleep(1000);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		};
		Runnable r2 = () -> {
			try {
				for (int i = 0; i < 10; i++) {
					queue.get();
					Thread.sleep(4000);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		};
		Thread t1 = new Thread(r1);
		t1.setName("Producer");
		Thread t2 = new Thread(r2);
		t2.setName("Consumer");
		t1.start();
		t2.start();
	}
}
