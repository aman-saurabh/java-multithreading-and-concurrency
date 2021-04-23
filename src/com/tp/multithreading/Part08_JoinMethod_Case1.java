package com.tp.multithreading;

/*
 * Case1 :- Waiting of parent thread until completion of child thread.
 */
public class Part08_JoinMethod_Case1 {
	public void run() {
		for (int i = 1; i <= 10; i++) {
			System.out.println(Thread.currentThread().getName() + " - Count : "+ i);
		}
	}

	public static void main(String[] args) {
		Part08_JoinMethod_Case1 p = new Part08_JoinMethod_Case1();
		//Creating Runnable object using double colon operator
		Runnable r = p::run;
		Thread t = new Thread(r);
		t.start();
		try {
			t.join();
		} catch (Exception e) {
			e.printStackTrace();
		}
		for (int i = 1; i <= 10; i++) {
			System.out.println(Thread.currentThread().getName() + " - Count : "+ i);
		}
	}
}
