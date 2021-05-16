package com.tp.concurrency.executor_service;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.locks.ReentrantLock;

public class Part3_DefaultThreadFactory {
	public static void main(String[] args) {	
		MyRunnable runnable = new MyRunnable();
		// Default ThreadFactory
        ThreadFactory threadFactory
            = Executors.defaultThreadFactory();
  
        for (int i = 0; i < 10; i++) {
  
            // Creating new threads with the default
            // ThreadFactory
            Thread thread
                = threadFactory.newThread(runnable);
  
            // run the thread
            thread.start();
        }

	}
}

class MyRunnable implements Runnable {
	ReentrantLock rLock = new ReentrantLock();
	@Override
	public void run() {
		//Making it synchronized so that result of one thread don't get messed with other.
		//synchronized (this) {
		//	System.out.println("Name : "+Thread.currentThread().getName());
		//	System.out.println("Thread group : "+Thread.currentThread().getThreadGroup().getName());
		//	System.out.println("Thread group max priority : "+ Thread.currentThread().getThreadGroup().getMaxPriority());
		//	System.out.println("Thread Priority : "+Thread.currentThread().getPriority());
		//	System.out.println("Is thread daemon : "+Thread.currentThread().isDaemon());
		//	System.out.println("************************************************");
		//}
		
		//We can also use ReentrantLock interface lock() and unlock() method inplace of synchronized block as follows.
		//But please create object of ReentrantLock outside this run() method i.e as instance variable(or as static variable);
		rLock.lock();
		System.out.println("Thread name : "+Thread.currentThread().getName());
		System.out.println("Thread group : "+Thread.currentThread().getThreadGroup().getName());
		System.out.println("Thread group max priority : "+ Thread.currentThread().getThreadGroup().getMaxPriority());
		System.out.println("Thread Priority : "+Thread.currentThread().getPriority());
		System.out.println("Is thread daemon : "+Thread.currentThread().isDaemon());
		System.out.println("************************************************");
		rLock.unlock();
	}
	
}