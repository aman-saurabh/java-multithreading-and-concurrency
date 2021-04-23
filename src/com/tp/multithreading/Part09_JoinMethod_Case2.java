package com.tp.multithreading;

/*
 * Case2 :- Waiting of child thread until completion of parent thread.
 * It seems like we can achieve it by using similar method as in previous case.But the problem is main(i.e parent) thread have reference to child thread but child thread don't have reference of main thread.
 * So we have to create reference of main thread in child thread somehow like by creating a static variable and by setting its value with main thread object.
 * In the following example we will see how to achieve that.
 * Note :- If we call join() method from first thread on second thread and from second thread on first thread.Then both threads will wait for eachother forever and enters into deadlock situation. 
 * Same situation can occur if we call join() method from a thread on itself.
 */

class JoinChildThread extends Thread{
	public static Thread mainThreadRef;
	public static Thread firstChildThreadRef;
	@Override
	public void run() {
		try {
			mainThreadRef.join();
			firstChildThreadRef.join();
		} catch (Exception e) {
			e.printStackTrace();
		}
		for (int i = 1; i <= 10; i++) {
			System.out.println(Thread.currentThread().getName()+" - Count :"+i);
		}
	};
}

public class Part09_JoinMethod_Case2 {
	public static Thread mainThreadObj;
	public static void main(String[] args) {
		//Setting main thread object in an static variables which we can use inside Runnable object as well as JoinChildThread object to get reference of main thread object. 
		mainThreadObj = JoinChildThread.mainThreadRef = Thread.currentThread();
		 
		Runnable r = new Runnable() {
			@Override
			public void run() {
				try {
					//accessing outer class instance variable 'mainThreadObj' inside anonymous inner class and calling join() method on that.
					mainThreadObj.join();
				} catch (Exception e) {
					e.printStackTrace();
				}
				for (int i = 1; i <= 10; i++) {
					System.out.println(Thread.currentThread().getName()+" - Count :"+i);
				}
			}
		};
		Thread t = new Thread(r);
		//setting first child thread object in an static variable of JoinChildThread which we can use inside run method to call join() method on it.
		JoinChildThread.firstChildThreadRef = t;
		JoinChildThread j = new JoinChildThread();
		t.start();
		j.start();
		for (int i = 1; i <= 10; i++) {
			System.out.println(Thread.currentThread().getName()+" - Count :"+i);
		}
	}
}
