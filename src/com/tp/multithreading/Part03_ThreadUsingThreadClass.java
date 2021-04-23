package com.tp.multithreading;

/*
 * We can create a thread by subclass of Thread class also where we can define our own implementation of run() method also.
 * In this part we will see how to do that.
 * But please note that however it works fine but still using Runnable interface is recommended as we can extend only one class to a class so if we extend Thread class we won't be able to extend any other class if required and thus we will miss inheritance feature
 * But we can implement any number of interfaces in a class so implementing Runnable interface won't create any issue.  
 */

class FirstThread extends Thread{
	@Override
	public void run() {
		for (int i = 0; i < 3; i++) {
			System.out.println("Executed by thread : "+ Thread.currentThread().getName()+", count : "+(i+1));
		}
	}
}

public class Part03_ThreadUsingThreadClass {
	public static void main(String[] args) {
		//Thread object by Thread class subclass.
		Thread t1 = new FirstThread();
		t1.setName("FirstChild");
		t1.start();
		
		//Thread object by anonymous inner class.
		Thread t2 = new Thread() {
			@Override
			public void run() {
				for (int i = 0; i < 3; i++) {
					System.out.println("Executed by thread : "+ Thread.currentThread().getName()+", count : "+(i+1));
				}
			}
		};
		t2.setName("SecondChild");
		t2.start();
		
		//Run by main thread.
		for (int i = 0; i < 3; i++) {
			System.out.println("Executed by thread : "+ Thread.currentThread().getName()+", count : "+(i+1));
		}
	}
}
