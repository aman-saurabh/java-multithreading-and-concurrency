package com.tp.multithreading;

class First {
	public synchronized void m1(Second second) {
		System.out.println("First class m1() method start");
		try {
			Thread.sleep(2000);
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
		System.out.println("First class m1() method is calling Second Class n2() method");
		second.n2();
	}
	
	public synchronized void m2() {
		System.out.println("First class m2() method start");
	}
}

class Second {
	public synchronized void n1(First first) {
		System.out.println("Second class n1() method start");
		try {
			Thread.sleep(2000);
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
		System.out.println("Second class n1() method is calling Second Class m2() method");
		first.m2();
	}
	
	public synchronized void n2() {
		System.out.println("Second class n2() method start");
	}
}

public class Part16_DeadLock {
	public static void main(String[] args) {
		First firstClassObj = new First();
		Second secondClassObj = new Second();
		Runnable r1 = () -> {
			firstClassObj.m1(secondClassObj);
		};
		
		Runnable r2 = () -> {
			secondClassObj.n1(firstClassObj);
		};
		
		Thread t1 = new Thread(r1);
		Thread t2 = new Thread(r2);
		t1.start();
		t2.start();
	}
}
