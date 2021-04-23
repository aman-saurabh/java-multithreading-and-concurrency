package com.tp.multithreading;

/*
 * Join and notify methods are used for interthread communication.
 * In this program three threads are working on object 'c' and which thread will get the lock that object first we don't know.
 * So to make everything systematic we have called 'wait()' method from two places.
 * If the thread 't1' get's the lock of 'c' object first and starts executing 'withdraw' method then it must wait until the deposit process is completed(i.e t2 execute 'deposit' method) 
 * and hence in 'withdraw' method we have checked if the the money has been deposited or not.And if the money is not deposited then t1 called wait() method on current object (i.e object 'c') and releases the lock of that object.
 * But now since 't2' and 'main', both threads are waiting to get the lock, so which thread will get the lock, again we can't expect. If 't2' gets the lock then it is ok as 't1' called 'wait()' for 't2' only to complete its job.
 * But what if 'main' thread gets the lock. We want main thread to call 'checkBalance' method only after withdrawl process is completed(i.e 't1' completes execution of withdrawl method).So to achieve that, we should have checked if the withdrawl process is completed or not.
 * But in main method's synchronized area for object c we have used while loop instead of simple if condition.It is because main thread can get chance to execute its code several times in this entire process 
 * 1st - in the beginning (i.e at first position), 2nd - when t1 calls wait() method, 3rd - when t2 calls notifyAll() method, 4th - when t1 call notifyAll() method etc.
 * And we wait it to call wait() method again and again until 't1' completes its execution and call notifyAll() method And that's the reason why we have used while loop in main method instead of simple if condition.
 * Here also note that we have used notifyAll() inplace of notify() method.It is because as we know that notify() method randomly picks any one thread and notify only that thread and ignore others.
 * So suppose t2() calls notify() method inplace of notifyAll() method and only 'main' thread get's notified. So it checked if withdrawl process is completed and since still withdrawl process is not completed, so it also calls wait() method.
 * Now since t1 is still waiting as it is not yet notified and main also called wait() method and enters into waiting state and t2 has already completed its execution called notify() method.So in such scenario 't1' and 'main' threads will stay into waiting position for ever and thus stuck into deadlock problem.
 * So it is not recommended to use especially in situation when more than 2 threads are working on the object.     
 * And even to avoid such unexpected situations also pass some time limit(i.e maximum waiting period) in wait() method as argument so that due to any reason waiting thread don't get notified in that time period than the waiting thread can continue its execution.
 * And that's the reason we have pass 3000ms as argument in wait() method as that much time will be enough in this situation.You have to calculate and think for your requirement.  
 */

class Customer {
	int amount = 10000;
	//Will be executed when thread 't1' gets the lock of 'c' object
	synchronized void withdraw(int amount) {
		Part17_JoinAndNotifyAllMethod1.isWithdrawlCompleted = false;
		System.out.println("Withdrawl process started...");

		if (this.amount < amount) {
			System.out.println("Less balance; waiting for deposit...");
			try {
				wait(5000);
			} catch (Exception e) {
				System.err.println(e.getMessage());
			}
		}
		this.amount -= amount;
		System.out.println("Withdrawl completed...");
		Part17_JoinAndNotifyAllMethod1.isWithdrawlCompleted = true;
		notifyAll();
	}

	//Will be executed when thread 't2' gets the lock of 'c' object
	synchronized void deposit(int amount) {
		System.out.println("Deposit process started...");
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			System.err.println(e.getMessage());
		}
		this.amount += amount;
		System.out.println("Deposit completed... ");
		notifyAll();
	}
	
	void checkBalance() {
		System.out.format("Current balance is Rs. %d\n", amount);
	}
}

public class Part17_JoinAndNotifyAllMethod1 {
	public static boolean isWithdrawlCompleted = false;
	public static void main(String args[]) {
		final Customer c = new Customer();
		
		//Anonymous Inner class that defines inside method argument 
		Thread t1 = new Thread() {
			public void run() {
				c.withdraw(15000);
			}
		};
		t1.start();
		//Anonymous Inner class that defines inside method argument 
		Thread t2 = new Thread() {
			public void run() {
				c.deposit(10000);
			}
		};
		t2.start();
		//Will execute this part when the main thread will get the lock of object 'c'.
		synchronized (c) {
			while(isWithdrawlCompleted == false)
				try {
					c.wait(3000);
				} catch (InterruptedException e) {
					System.err.println(e.getMessage());
				}
			c.checkBalance();
		}		
	}
}
