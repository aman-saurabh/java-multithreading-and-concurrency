package com.tp.multithreading;

public class Part5_ThreadName {
	public static void main(String[] args) {
		Runnable r = () -> {
			System.out.println("Child thread name : "+Thread.currentThread().getName());
			//Intentionally creating DivisionByZeroException to check the name of thread in stack trace.
			System.out.println(10/0);
		};
		
		Thread t = new Thread(r); 
		//To get name if you have Thread object.
		System.out.println(t.getName());
		//To set name if you have Thread object.
		t.setName("MyHeroThread");
		t.start();
		
		//To get name if you don't have Thread object.
		Thread.currentThread().setName("MyMainThread");
		//To get name if you don't have Thread object.
		System.out.println(Thread.currentThread().getName());
		//Actually "Thread.currentThread()" method is used to get the current thread object.So on that we can perform any operation which we can do on any Thread class object.
	}
}
