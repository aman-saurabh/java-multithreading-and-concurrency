package com.tp.concurrency.lock;

import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.StampedLock;

/*
 * Another example to show functionality of StampedLock class
 */

class Loan {
	private StampedLock sl = new StampedLock();
	private final double rate = 8.75;
	String username;
	String userId;
	int loanAmount;
	int totalEmi;
	int paidEmi;
	int emiAmount;
	boolean inactiveLoan;

	public Loan(String name, int loanAmount, int totalEmi) {
		this.username = name;
		this.loanAmount = loanAmount;
		this.totalEmi = totalEmi;
		this.paidEmi = 0;
		this.inactiveLoan = false;
		this.userId = UUID.randomUUID().toString().replace("-", "");
		this.emiAmount = (int) (Math.floor(loanAmount * rate * Math.pow(1 + rate, totalEmi))
				/ (Math.pow(1 + rate, totalEmi) - 1));
	}

	/**
	 * This method is to show the feature of writeLock() method
	 */
	public String payEmi() throws Exception {
		long stamp = 0;
		try {
			stamp = sl.tryWriteLock(1000, TimeUnit.MILLISECONDS);
			if (stamp == 0) {
				throw new Exception("Request time out.Please try again to pay your emi.");
			}
			if (this.inactiveLoan) {
				throw new Exception("Loan is already closed.No need to pay anymore.");
			}
			Thread.sleep(3000);
			paidEmi++;
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			if (StampedLock.isWriteLockStamp(stamp))
				sl.unlockWrite(stamp);
		}
		return "Emi paid successfully.";
	}

	/**
	 * This method is to show the feature of readLock() method
	 */
	public int getRemainingLoanAmount() throws Exception {
		long stamp = 0;
		int remainingAmount = 0;
		try {
			stamp = sl.tryReadLock(1000, TimeUnit.MILLISECONDS);
			if (stamp == 0) {
				throw new Exception("Request time out.Please try again to get remaining amount.");
			}
			if (this.inactiveLoan) {
				throw new Exception("Loan is already closed.No amount is pending.");
			}
			remainingAmount = emiAmount * (totalEmi - paidEmi);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			if (StampedLock.isReadLockStamp(stamp))
				sl.unlockRead(stamp);
		}
		return remainingAmount;
	}

	/**
	 * This method is to show the feature of tryOptimisticRead() method
	 * 
	 * @throws Exception
	 */
	public int getRemainingEmi() throws Exception {
		long stamp = sl.tryOptimisticRead();
		int remainingEmi = 0;
		if (!sl.validate(stamp)) {
			stamp = sl.readLock();
		}
		try {
			if (this.inactiveLoan) {
				throw new Exception("Loan is already closed.No emi is remaining.");
			}
			remainingEmi = totalEmi - paidEmi;
		} finally {
			if (StampedLock.isReadLockStamp(stamp)) {
				sl.unlock(stamp);
			}
		}
		return remainingEmi;
	}

	/**
	 * This method is to show the feature of tryConvertToWriteLock() method
	 */
	public String closeLoan(int amount) throws Exception {
		long stamp = sl.readLock();
		try {
			int remainingAmount = getRemainingLoanAmount();
			if (remainingAmount > amount) {
				throw new Exception("Loan can't be closed now.Inadequate Amount.");
			}
			stamp = sl.tryConvertToWriteLock(stamp);
			inactiveLoan = true;
			return "Loan closed successfully.";
		} finally {
			sl.unlock(stamp);
		}
	}

}

public class Part10_StampedLockExample2 {
	public static void main(String[] args) {
		Loan loan = new Loan("Aman", 50000, 12);
		Runnable r1 = () -> {
			try {
				for (int i = 0; i < 10; i++) {
					System.out.println(loan.payEmi());
				}
			} catch (Exception e) {
				System.err.println("\n" + e.getMessage() + "\n");
			}
		};
		Runnable r2 = () -> {
			try {
				for (int i = 0; i < 3; i++) {
					Thread.sleep(5000);
					int remainingAmount = loan.getRemainingEmi();
					System.out.println("Remaining emi : " + remainingAmount);
				}
			} catch (Exception e) {
				System.err.println("\n" + e.getMessage() + "\n");
			}
		};
		Runnable r3 = () -> {
			try {
				Thread.sleep(3000);
				System.out.println(loan.closeLoan(5000));
			} catch (Exception e) {
				System.err.println("\n" + e.getMessage() + "\n");
			}
		};

		Runnable r4 = () -> {
			try {
				Thread.sleep(5000);
				int remainingAmount = 0;
				while (true) {
					try {
						remainingAmount = loan.getRemainingLoanAmount();
						break;
					} catch (Exception e2) {
						System.err.println("\n" + e2.getMessage() + "\n");
						Thread.sleep(1000);
					}
				}
				System.out.println(loan.closeLoan(remainingAmount));
			} catch (Exception e) {
				System.err.println("\n" + e.getMessage() + "\n");
			}
		};

		Thread t1 = new Thread(r1);
		Thread t2 = new Thread(r2);
		Thread t3 = new Thread(r3);
		Thread t4 = new Thread(r4);
		t1.start();
		t2.start();
		t3.start();
		t4.start();
	}
}
