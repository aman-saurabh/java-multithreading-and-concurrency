package com.tp.concurrency.executor_service;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
 
/*
 * In this example we will see how to run a Runnable as well as Callable task after an initial delay of specified time. 
 */
public class Part5_ScheduledExecutorServiceBasics {
	public static void main(String[] args) throws InterruptedException {
		Runnable rTask = () -> {
			System.out.println("Runnable task is executed");
		};
		Callable<String> cTask = () -> {
			TimeUnit.MILLISECONDS.sleep(10000);
			return "Returned from Callable task";
		};
		ScheduledExecutorService sExecutorService = Executors.newScheduledThreadPool(2);
		// To execute a Runnable task after delay of 3 seconds
		sExecutorService.schedule(rTask, 3, TimeUnit.SECONDS);
		// To execute a Callable task after delay of 7 seconds
		ScheduledFuture<String> sFuture = sExecutorService.schedule(cTask, 7, TimeUnit.SECONDS);
		// To execute a task by main thread without any delay.
		// It should be executed first.Which will verify synchronous execution of these tasks
		mainThreadTask();
		// Printing sFuture task
		try {
			System.out.println(sFuture.get());
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
		sExecutorService.shutdown();
	}
	
	public static void mainThreadTask() {
		System.out.println("Main thread task executed");
	}
}
