package com.tp.multithreading;

/*
 * The yield() method is a static method of Thread class and it can stop the currently executing thread and will give a chance to other waiting threads of the same priority. 
 * In case, if there are no waiting threads or if all the waiting threads have low priority then the same thread will continue its execution.
 * It will not work here as it is a 'native' method i.e depends on OS and some OS(including ubuntu) don't provide support for yield() method by default.
 */
public class Part7_YieldMethod {
	public static void main(String[] args) {
		Thread t = new Thread() {
			@Override
			public void run() {
				for (int i = 1; i <= 5; i++) {
					System.out.println(Thread.currentThread().getName()+" - count - "+i);
					Thread.yield();
				}
				System.out.println(Thread.currentThread().getName()+" execution completed.");
			}
		};
		
		t.start();
		
		for (int i = 0; i < 5; i++) {
			System.out.println(Thread.currentThread().getName()+" - count - "+i);
		}
	}
}
