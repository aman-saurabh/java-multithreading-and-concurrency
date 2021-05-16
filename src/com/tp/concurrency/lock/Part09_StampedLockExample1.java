package com.tp.concurrency.lock;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.locks.*;
import java.util.stream.Collectors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;

class IncomeTaxDept {
	private List<TaxPayer> taxPayersList;
	private int totalRevenue;
	private final StampedLock sl = new StampedLock();

	public IncomeTaxDept(int revenue, int numberOfTaxPayers) {
		this.totalRevenue = revenue;
		taxPayersList = new ArrayList<TaxPayer>(numberOfTaxPayers);
	}

	/**
	 * This method is to show the feature of writeLock() method
	 */
	public void payTax(TaxPayer taxPayer) {
		int taxAmount = taxPayer.getTaxAmount();
		long stamp = sl.writeLock();
		try {
			totalRevenue += taxAmount;
		} finally {
			sl.unlockWrite(stamp);
		}
	}

	/**
	 * This method is to show the feature of readLock() method
	 */
	public int getTotalRevenue() {
		long stamp = sl.readLock();
		try {
			return this.totalRevenue;
		} finally {
			sl.unlockRead(stamp);
		}
	}

	/**
	 * This method is to show the feature of tryOptimisticRead() method
	 */
	public List<TaxPayer> getTopTaxPayersList() {
		long stamp = sl.tryOptimisticRead();
		List<TaxPayer> sortedTopTaxPayersList = null;
		// calling validate(stamp) method to ensure that stamp is valid, if not then
		// acquiring the read lock
		if (!sl.validate(stamp)) {
			stamp = sl.readLock();
		}

		try {
			sortedTopTaxPayersList = taxPayersList.stream().sorted().limit(10).collect(Collectors.toList());
		} finally {
			// While using tryOptimisticRead() and readLock() (in fail condition) then use following condition before applying unlockRead() method.
			// This is to ensure that unlockread() method is called only if lock was acquired with isRead() method and not with tryOptimisticRead().
			// Otherwise it might throw IllegalMonitorStateException.
			if (StampedLock.isReadLockStamp(stamp)) {
				sl.unlockRead(stamp);
			}
		}
		return sortedTopTaxPayersList;
	}

	/**
	 * This method is to show the feature of writeLock() method
	 */
	public int getFederalTaxReturn(TaxPayer taxPayer) {
		int incomeTaxRetunAmount = (int) Math.floor(taxPayer.getTaxAmount() * 10 / 100);
		long stamp = sl.writeLock();
		try {
			this.totalRevenue -= incomeTaxRetunAmount;
		} finally {
			sl.unlockWrite(stamp);
		}
		return incomeTaxRetunAmount;
	}

	/**
	 * This method is to show the feature of tryConvertToWriteLock() method
	 */
	public int getStateTaxReturn(TaxPayer taxPayer) {
		int incomeTaxRetunAmount = (int) Math.floor(taxPayer.getTaxAmount() * 10 / 100);
		long stamp = sl.readLock();

		// Trying to upgrade the lock from read to write
		stamp = sl.tryConvertToWriteLock(stamp);

		// Checking if tryConvertToWriteLock got success otherwise call writeLock method
		if (stamp == 0L) {
			stamp = sl.writeLock();
		}
		try {
			this.totalRevenue -= incomeTaxRetunAmount;
		} finally {
			sl.unlockWrite(stamp);
		}
		return incomeTaxRetunAmount;
	}

	public void registerTaxPayer(TaxPayer taxPayer) {
		taxPayersList.add(taxPayer);
	}

	public List<TaxPayer> getTaxPayersList() {
		return taxPayersList;
	}
}

class TaxPayer implements Comparable<TaxPayer> {
	private String taxPayerName;
	private String taxPayerSsn;
	private int taxAmount;

	public String getTaxPayerName() {
		return taxPayerName;
	}

	public void setTaxPayerName(String taxPayerName) {
		this.taxPayerName = taxPayerName;
	}

	public String getTaxPayerSsn() {
		return taxPayerSsn;
	}

	public void setTaxPayerSsn(String taxPayerSsn) {
		this.taxPayerSsn = taxPayerSsn;
	}

	public int getTaxAmount() {
		return taxAmount;
	}

	public void setTaxAmount(int taxAmount) {
		this.taxAmount = taxAmount;
	}

	// Implemention compareTo() method to use it while sorting list of taxpayers.
	@Override
	public int compareTo(TaxPayer o) {
		return this.taxAmount > o.taxAmount ? -1 : this.taxAmount < o.taxAmount ? +1 : 0;
	}
}

public class Part09_StampedLockExample1 {
	private static final int taxPayerNum = 100;
	private static IncomeTaxDept incomeTaxDept = new IncomeTaxDept(100000, taxPayerNum);

	public static void main(String[] args) {

		registerTaxPayers();
		ExecutorService executor = Executors.newFixedThreadPool(30);

		for (TaxPayer taxPayer : incomeTaxDept.getTaxPayersList()) {
			executor.submit(() -> {
				try {
					Thread.sleep(100);
					incomeTaxDept.payTax(taxPayer);
					int revenue = incomeTaxDept.getTotalRevenue();
					System.out.printf("Tax payment process for taxpayer Name - %s, SSN - %s started.\n",
							taxPayer.getTaxPayerName(), taxPayer.getTaxPayerSsn());
					System.out.println("IncomeTax Department's total revenue after paying tax : " + revenue);

					int returnAmount = incomeTaxDept.getFederalTaxReturn(taxPayer);
					System.out.println(
							taxPayer.getTaxPayerName() + " received the Federal return of amount = " + returnAmount);

					revenue = incomeTaxDept.getTotalRevenue();

					System.out.println(
							"IncomeTax Department's total revenue after getting Federal tax return : " + revenue);
					int stateReturnAmount = incomeTaxDept.getStateTaxReturn(taxPayer);

					System.out.println(taxPayer.getTaxPayerName() + " received the State tax return of amount = "
							+ stateReturnAmount);
					revenue = incomeTaxDept.getTotalRevenue();

					System.out.println(
							"IncomeTax Department's total revenue after getting State tax return : " + revenue);
					System.out.printf("*************Tax paid for user %s successfully*****************\n",taxPayer.getTaxPayerName());
					Thread.sleep(100);
				} catch (Exception e) {
					e.printStackTrace();
				}
			});
		}
		executor.shutdown();

		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println();
		//To list out top tax payers.
		System.out.println("Now listing top 10 taxpayers.");
		List<TaxPayer> topTaxPayers = incomeTaxDept.getTopTaxPayersList();
		int count = 1;
		for (TaxPayer tp : topTaxPayers) {
			System.out.printf("S.No. : %d, Tax payer name : %s, SSN : %s, paid tax amount : %s\n",count++, tp.getTaxPayerName(),
					tp.getTaxPayerSsn(), tp.getTaxAmount());
		}
	}

	private static void registerTaxPayers() {
		for (int i = 1; i < taxPayerNum + 1; i++) {
			TaxPayer taxPayer = new TaxPayer();
			// Generating a random number to use as tax amount
			int taxAmount = ThreadLocalRandom.current().nextInt(1000, 100000);
			taxPayer.setTaxAmount(taxAmount);
			taxPayer.setTaxPayerName("Payer-" + (i <= 9 ? "0" + i : i));
			// Generating a random string to use as SSN
			int leftLimit = 97; // letter 'a'
			int rightLimit = 122; // letter 'z'
			int targetStringLength = 10;
			Random random = new Random();
			String generatedString = random.ints(leftLimit, rightLimit + 1).limit(targetStringLength)
					.collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append).toString();
			taxPayer.setTaxPayerSsn(generatedString);
			incomeTaxDept.registerTaxPayer(taxPayer);
		}
	}
}
