package com.tp.concurrency;

/*
 * This is one of the most appropriate solution to the problem discussed in Part4 and part6.
 * Solved the same problem of part4 with the help of 'Condition' class.
 * It should be executed in the following sequence :
 * First one deposit() method gets executed, followed by checkBalance(),then one withdrawl(), again followed by checkBalance(),
 * then second deposit() method gets executed, followed by checkBalance(),then second withdrawl(), finally checkBalance() in the last.
 */

import java.text.MessageFormat;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

class Account3 {
	private int balance = 0;
	private int writeCounter = 0;
	private boolean readRequired = false;
	static ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
	WriteLock writeLock = lock.writeLock();
	ReadLock readLock = lock.readLock();
	// Below Conditions are to be used with checkBalance method hence we should use readLock.
	// But readLock don't have "newCondition" method.So used writeLock here as well as in checkBalance() method to manage conditions.
	public Condition firstDeposit = writeLock.newCondition();
	public Condition secondDeposit = writeLock.newCondition();
	public Condition firstWithdrawl = writeLock.newCondition();
	public Condition secondWithdrawl = writeLock.newCondition();
	public Condition balanceCheck = writeLock.newCondition();
	public Condition depositOrWithdraw = writeLock.newCondition();

	// Write method - hence synchronized
	public void deposit(int amount) {
		try {
			writeLock.lockInterruptibly();
			if(readRequired == true) {
				balanceCheck.await();
			}
			if(writeCounter == 1) {
				firstWithdrawl.await();
			}			
			
			Thread.sleep(2000);
			balance += amount;
			System.out.printf("Amount %d deposited successfully.Current balance : %d\n", amount, balance);
			
			if(writeCounter == 0) {
				firstDeposit.signalAll();
			} else if(writeCounter == 2){
				secondDeposit.signalAll();
			}
			depositOrWithdraw.signalAll();
			readRequired = true;
			writeCounter++;
		} catch (InterruptedException e) {
			System.out.println("Request timeout. Please try again to deposit money.");
		} finally {
			if (writeLock.isHeldByCurrentThread())
				writeLock.unlock();
		}
	}

	// Write method - hence synchronized
	public void withdraw(int amount) {
		try {
			writeLock.lockInterruptibly();
			if(readRequired == true) {
				balanceCheck.await();
			}
			if(writeCounter == 0) {
				firstDeposit.await();
			} else if (writeCounter == 2) {
				secondDeposit.await();
			}
			
			Thread.sleep(2000);
			balance -= amount;
			System.out.println("Withdrawl of " + amount + " successful.Current balance : " + balance);
			if(writeCounter == 1) {
				firstWithdrawl.signalAll();
			} else if (writeCounter == 3) {
				secondWithdrawl.signalAll();
			}
			
			depositOrWithdraw.signalAll();
			readRequired = true;
			writeCounter++;
		} catch (InterruptedException e) {
			System.out.println("Request timeout. Please try again to withdraw money");
		} finally {
			if (writeLock.isHeldByCurrentThread())
				writeLock.unlock();
		}
	}

	// Read method - hence non-synchronized
	public void checkBalance() throws InterruptedException {
		try {
			writeLock.lock();
			while(readRequired == false) {
				depositOrWithdraw.await();
			}
			String msg = MessageFormat.format("Your balance is Rs. {0, number}/-", balance);
			System.out.println(msg);
			readRequired = false;
			balanceCheck.signalAll();
		} finally {
			writeLock.unlock();
		}
	}
}

class DepositRunnable3 implements Runnable {
	int amount;
	Account3 account;

	DepositRunnable3(int amount, Account3 account) {
		this.amount = amount;
		this.account = account;
	}

	@Override
	public void run() {
		account.deposit(amount);
	}
}

class WithdrawRunnable3 implements Runnable {
	int amount;
	Account3 account;

	public WithdrawRunnable3(int amount, Account3 account) {
		this.amount = amount;
		this.account = account;
	}

	@Override
	public void run() {
		account.withdraw(amount);
	}
}

class BalanceRunnable3 implements Runnable {
	Account3 account;

	public BalanceRunnable3(Account3 account) {
		this.account = account;
	}

	@Override
	public void run() {
		try {
			account.checkBalance();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}

public class Part08_LockConditionClassExample2 {
	public static void main(String[] args) {
		Account3 account = new Account3();
		DepositRunnable3 dr1 = new DepositRunnable3(8000, account);
		WithdrawRunnable3 wr1 = new WithdrawRunnable3(2000, account);
		DepositRunnable3 dr2 = new DepositRunnable3(7000, account);
		WithdrawRunnable3 wr2 = new WithdrawRunnable3(6000, account);
		Thread t1 = new Thread(dr1);
		Thread t2 = new Thread(new BalanceRunnable3(account));
		Thread t3 = new Thread(wr1);
		Thread t4 = new Thread(new BalanceRunnable3(account));
		Thread t5 = new Thread(dr2);
		Thread t6 = new Thread(new BalanceRunnable3(account));
		Thread t7 = new Thread(wr2);
		Thread t8 = new Thread(new BalanceRunnable3(account));
		t1.start();
		t2.start();
		t3.start();
		t4.start();
		t5.start();
		t6.start();
		t7.start();
		t8.start();
		
		//If you want to check full execution, comment following lines. 
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
