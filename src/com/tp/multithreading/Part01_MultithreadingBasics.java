package com.tp.multithreading;

/*
 * Basic example of how to create new thread and define the task that will be performed by that thread.
 * In this example we have never called run method explicitly but when we run the program we see it get's called.
 * Actually it is called internally by newly created Child thread.
 * Here main method is executed by main thread and there in main method we created a new thread which executed run method.
 * Both methods were executed simultaneously by different threads.This is an example of concurrency and multithreading.
 * But please note that as we have learned in theory part actually both methods were executed simultaneously, they just shared same resources to perform their tasks at the same time using the concept of time-slicing of OS.     
 */
public class Part01_MultithreadingBasics implements Runnable {
	@Override
	public void run() {
		System.out.println("Thread executing run method : "+Thread.currentThread().getName());
	}
	
	public static void main(String[] args) {
		Runnable r = new Part01_MultithreadingBasics();
		Thread t = new Thread(r);
		t.start();
		System.out.println("Thread executing main method : "+Thread.currentThread().getName());
	}
}
