package com.tp.concurrency;

import java.text.MessageFormat;
import java.util.concurrent.locks.ReentrantLock;

/*
 * In this part we will see how to use lock lockInterruptibly method in ReentrantLock object.
 * This example works fine but you will find even though we created a thread to check balance after every deposit and withdrawl.
 * But all check balance method call will be executed before the amount is deposited or withdrawl.
 * Since checkBalance method is no synchronized hecne its get executed without waiting.
 * To make it work as it should be we need to apply something like wait, notify() and notifyAll() methods but these methods works only in synchronized method or block(i.e synchronized using 'synchronized' keyword). 
 * But alternatively we have java.util.concurrent.locks.Condition class, which provides inter-thread communication methods similar to wait, notify and notifyAll e.g. await(), signal(), and signalAll().
 * We will see basic example of java.util.concurrent.locks.Condition class in part 7 and then how to apply that in this example to call checkBalance only after deposit or withdraw is completed in Part8.
 * But before that we will basics of 'ReentrantReadWriteLock' in part5. 
 * And we will also see how we can make sure that read operation get performed simultaneously by multiple threads but only if no thread is performing write operation at that time with the help of ReentrantReadWriteLock in Part 6.
 */

class Account {
	private int balance = 0;
	static ReentrantLock lock = new ReentrantLock();

	// Write method - hence synchronized
	public void deposit(int amount) {
		try {
			lock.lockInterruptibly();
			Thread.sleep(2000);
			balance += amount;
			System.out.printf("Amount %d deposited successfully.Current balance : %d\n",amount, balance);
		} catch (InterruptedException e) {
			System.out.println("Request timeout. Please try again to deposit money.");
		} finally {
			if(lock.isHeldByCurrentThread())
			lock.unlock();
		}
	}

	// Write method - hence synchronized
	public void withdraw(int amount) {
		try {
			while(balance < amount) {
				System.out.println("Balance is low.Waiting for money to be deposited");
				Thread.sleep(2000);
			}
			lock.lockInterruptibly();
			Thread.sleep(2000);
			balance -= amount;
			System.out.println("Withdrawl of " + amount + " successful.Current balance : "+balance);
		} catch (InterruptedException e) {
			System.out.println("Request timeout. Please try again to withdraw money");
		} finally {
			if(lock.isHeldByCurrentThread())
			lock.unlock();
		}
	}

	// Read method - hence non-synchronized
	public void checkBalance() {
		String msg = MessageFormat.format("Your balance is Rs. {0, number}/-", balance);
		System.out.println(msg);
	}
}

class DepositRunnable implements Runnable {
	int amount;
	Account account;

	DepositRunnable(int amount, Account account) {
		this.amount = amount;
		this.account = account;
	}

	@Override
	public void run() {
		account.deposit(amount);
	}
}

class WithdrawRunnable implements Runnable {
	int amount;
	Account account;

	public WithdrawRunnable(int amount, Account account) {
		this.amount = amount;
		this.account = account;
	}

	@Override
	public void run() {
		account.withdraw(amount);
	}
}

class BalanceRunnable implements Runnable {
	Account account;

	public BalanceRunnable(Account account) {
		this.account = account;
	}

	@Override
	public void run() {
		account.checkBalance();
	}
}

public class Part04_ReentrantLockExample3 {
	public static void main(String[] args) {
		Account account = new Account();
		DepositRunnable dr1 = new DepositRunnable(8000, account);
		WithdrawRunnable wr1 = new WithdrawRunnable(2000, account);
		DepositRunnable dr2 = new DepositRunnable(7000, account);
		WithdrawRunnable wr2 = new WithdrawRunnable(6000, account);
		BalanceRunnable br = new BalanceRunnable(account);
		Thread t1 = new Thread(dr1);
		Thread t2 = new Thread(br);
		Thread t3 = new Thread(wr1);
		Thread t4 = new Thread(br);
		Thread t5 = new Thread(dr2);
		Thread t6 = new Thread(br);
		Thread t7 = new Thread(wr2);
		Thread t8 = new Thread(br);
		t1.start();
		t2.start();
		t3.start();
		t4.start();
		t5.start();
		t6.start();
		t7.start();
		t8.start();
		try {
			Thread.sleep(8000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		t1.interrupt();
		t3.interrupt();
		t5.interrupt();
		t7.interrupt();
	}
}
