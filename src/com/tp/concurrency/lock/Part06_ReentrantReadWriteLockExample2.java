package com.tp.concurrency.lock;

import java.text.MessageFormat;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

/*
 * In this part we have tried to solve same problem as in part4 with the help of ReentrantReadWriteLock object.
 * To make sure read opertion can be perfromed simultaneously by several thread but only if no other thread is performing right operation.
 * However this is not the best solution.More appropriate solution is discussed in part8.
 */

class Account2 {
	private int balance = 0;
	static ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
	WriteLock writeLock = lock.writeLock();
	ReadLock readLock = lock.readLock();

	// Write method - hence synchronized
	public void deposit(int amount) {
		try {
			writeLock.lockInterruptibly();
			balance += amount;
			Thread.sleep(2000);
			System.out.printf("Amount %d deposited successfully.Current balance : %d\n",amount, balance);
		} catch (InterruptedException e) {
			System.out.println("Request timeout. Please try again to deposit money.");
		} finally {
			if(writeLock.isHeldByCurrentThread())
				writeLock.unlock();
		}
	}

	// Write method - hence synchronized
	public void withdraw(int amount) {
		try {
			while(balance < amount) {
				System.out.println("Balance is low.Waiting for money to be deposited");
				Thread.sleep(2000);
			}
			writeLock.lockInterruptibly();
			balance -= amount;
			Thread.sleep(2000);
			System.out.println("Withdrawl of " + amount + " successful.Current balance : "+balance);
		} catch (InterruptedException e) {
			System.out.println("Request timeout. Please try again to withdraw money");
		} finally {
			if(writeLock.isHeldByCurrentThread())
				writeLock.unlock();
		}
	}

	// Read method - hence non-synchronized
	public void checkBalance() {
		//Applying readlock here ensures that multiple threads can perform following operations simultaneously but only if when no thread is holding writelock(i.e not performing write operation that time). 
		readLock.lock();
		try {
			String msg = MessageFormat.format("Your balance is Rs. {0, number}/-", balance);
			System.out.println(msg);
		} finally {
			readLock.unlock();
		}
		
	}
}

class DepositRunnable2 implements Runnable {
	int amount;
	Account2 account;

	DepositRunnable2(int amount, Account2 account) {
		this.amount = amount;
		this.account = account;
	}

	@Override
	public void run() {
		account.deposit(amount);
	}
}

class WithdrawRunnable2 implements Runnable {
	int amount;
	Account2 account;

	public WithdrawRunnable2(int amount, Account2 account) {
		this.amount = amount;
		this.account = account;
	}

	@Override
	public void run() {
		account.withdraw(amount);
	}
}

class BalanceRunnable2 implements Runnable {
	Account2 account;

	public BalanceRunnable2(Account2 account) {
		this.account = account;
	}

	@Override
	public void run() {
		account.checkBalance();
	}
}


public class Part06_ReentrantReadWriteLockExample2 {
	public static void main(String[] args) {
		Account2 account = new Account2();
		DepositRunnable2 dr1 = new DepositRunnable2(8000, account);
		WithdrawRunnable2 wr1 = new WithdrawRunnable2(2000, account);
		DepositRunnable2 dr2 = new DepositRunnable2(7000, account);
		WithdrawRunnable2 wr2 = new WithdrawRunnable2(6000, account);
		BalanceRunnable2 br = new BalanceRunnable2(account);
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
