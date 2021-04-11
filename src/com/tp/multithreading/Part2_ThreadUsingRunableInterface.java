package com.tp.multithreading;

class FirstRunnable implements Runnable {
	@Override
	public void run() {
		for (int i = 0; i < 3; i++) {
			System.out.println("Executed by thread : "+ Thread.currentThread().getName()+", count : "+(i+1));
		}
	}
}

public class Part2_ThreadUsingRunableInterface {
	public static void main(String[] args) {
		//Runnable object using implementation class
		Runnable r1 = new FirstRunnable();
		Thread t1 = new Thread(r1, "FirstChild");
		t1.start();
		
		//Runnable object using anonymous inner class
		Runnable r2 = new Runnable() {
			@Override
			public void run() {
				for (int i = 0; i < 3; i++) {
					System.out.println("Executed by thread : "+ Thread.currentThread().getName()+", count : "+(i+1));
				}
			}
		};
		Thread t2 =  new Thread(r2, "SecondChild");
		t2.start();
		
		//Runnable object using Lambda Expression
		Runnable r3 = () -> {
			for (int i = 0; i < 3; i++) {
				System.out.println("Executed by thread : "+ Thread.currentThread().getName()+", count : "+(i+1));
			}
		};
		Thread t3 = new Thread(r3, "ThirdChild"); 
		t3.start();
		
		//Run by main thread
		for (int i = 0; i < 3; i++) {
			System.out.println("Executed by thread : "+ Thread.currentThread().getName()+", count : "+(i+1));
		}
	}
}
