package com.tp.concurrency.executor_service;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class Part4_FutureInterfaceBasics {
	public static void main(String[] args) throws InterruptedException, ExecutionException, TimeoutException {
		Callable<String> callableTask = () -> {
			TimeUnit.MILLISECONDS.sleep(2000);
			return "Hello world!";
		};
		ExecutorService eService = Executors.newCachedThreadPool();
		Future<String> future1 = eService.submit(callableTask);
		String str1 = future1.get();
		System.out.println("Future1 value : "+str1);
		
		Future<String> future2 = eService.submit(callableTask);
		String str2 = null;
		try {
			str2 = future2.get(1000, TimeUnit.MILLISECONDS);
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		if(future2.isDone()) {
			System.out.println("future2 value : "+str2);
		} else {
			System.out.println("Could not get future2 value.");
			System.out.println("Is future2 cancelled before manually cancelling : "+future2.isCancelled());
			future2.cancel(true);
			System.out.println("Is future2 cancelled after manually cancelling : "+future2.isCancelled());
		}
		eService.shutdown();
	}
}
