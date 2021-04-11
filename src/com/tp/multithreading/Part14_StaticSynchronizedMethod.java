package com.tp.multithreading;
/*
 * We will used same example as Part12 but here we will use a seperate Message object for every thread.
 * And since we can't create two classes with same name in same package hence we have changed name of Message class to PrintMessage.
 * Here with synchronized keyword also we will see similar problem as we were facing in Part12 without synchronized keyword.
 * This is bacause synchronized keyword works if multiple threads try to  work on synchronized method of same object.
 * But here since every thread is working on its own copy of PrintMessage object, hence synchronized keyword is not working here.
 * So here we need class level lock inplace of object level lock.And for that we need to use 'static synchronized' keyword together.  
 */

class PrintMessage {
	public static synchronized void sayHello(String threadName) {
		for (int i = 0; i < 5; i++) {
			System.out.print("Hello ");
			try {
				Thread.sleep(2000);
			} catch (Exception e) {
				System.err.println("Interrupted exception occur.");
			}
			
			System.out.println(threadName+"!");
		}
	}
}

public class Part14_StaticSynchronizedMethod {
	public static void main(String[] args) {
		Runnable r = () -> {
			//Created PrintMessage object inside Runnable interface run() method.
			//So that every thread get's its own copy of PrintMessage object.
			//PrintMessage msg = new PrintMessage();
			//msg.sayHello(Thread.currentThread().getName());
			
			//Now since sayHello method is static.Hence its better to acess that in static way.However using above method will also work fine.
			PrintMessage.sayHello(Thread.currentThread().getName());
		};
		
		Thread t1 = new Thread(r, "FirstChildThread");
		Thread t2 = new Thread(r, "SecondChildThread");
		t1.start();
		t2.start();
		Thread.currentThread().setName("MainThread");
		//new PrintMessage().sayHello(Thread.currentThread().getName());
		
		//Now since sayHello method is static.Hence its better to acess that in static way.However using above method will also work fine.
		PrintMessage.sayHello(Thread.currentThread().getName());
	}
}
