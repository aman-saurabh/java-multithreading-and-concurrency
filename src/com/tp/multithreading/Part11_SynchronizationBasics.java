package com.tp.multithreading;

/*
 * In this example you will see all three sayHello message gets printed simultaneously since it is not a synchronized method.
 * So all 3 threads will start waiting for 2000ms at same time and after that they all will print sayHello message almost at the same time.
 * But you will see that's not the case with wishGoodLuck method or sayGoodBye.
 * In these methods you will see if one thread starts printing message then only that thread prints messages until all 4 message of wishGoodLuck method & sayGoodBye method got printed.
 * And then same scenario happen for next two threads also i.e no 2 thread works simultaneously on these methods.
 * But Please note that in some cases you might see that any thread prints only 1 or 2 message of both methods and then next thread starts printing its all 4 messages and remaining message of first thread gets printed after second thread prints all 4 messages or even in the last i.e after third thread also prints all of its 4 messages. 
 * It happens because in sleeping period a thread can loose the lock of the object and in that scenario the lock will be given to next thread.So the first thread will have to wait until its gets the lock again.
 * And when it will get the lock again there is no gurantee of it. 
 */
class Greeting {
	public void sayHello(String threadName) {
		System.out.println("Starts waiting in sayHello method : "+ threadName);
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("Hello "+threadName+"!");
	}

	public synchronized void wishGoodLuck(String threadName) {
		System.out.println("Starts waiting in wishGoodLuck method : "+threadName);
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("Wishes you all the best "+ threadName+"!");
	}

	public synchronized void sayGoodBye(String threadName) {
		System.out.println("Starts waiting in sayGoodBye method : "+threadName);
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("Good Bye "+threadName);
	}
}

public class Part11_SynchronizationBasics {
	public static void main(String[] args) {
		Greeting g = new Greeting();
		Runnable r = () -> {
			String threadName = Thread.currentThread().getName();
			g.sayHello(threadName);
			g.wishGoodLuck(threadName);
			g.sayGoodBye(threadName);
		};
		
		Thread t1 = new Thread(r, "FirstThread");
		Thread t2 = new Thread(r, "SecondThread");
		Thread t3 = new Thread(r, "ThirdThread");
		t1.start();
		t2.start();
		t3.start();
	}
}
