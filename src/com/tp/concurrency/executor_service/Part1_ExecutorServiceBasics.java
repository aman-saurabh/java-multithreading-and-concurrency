package com.tp.concurrency.executor_service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/*
 * For theory check online document.
 */

public class Part1_ExecutorServiceBasics {
	static int count = 0;
	
	public static void main(String[] args) throws InterruptedException, ExecutionException {
		Runnable runnableTask = () -> {
		    try {
		        TimeUnit.MILLISECONDS.sleep(300);
		        System.out.println("Runnable called");
		    } catch (InterruptedException e) {
		        e.printStackTrace();
		    }
		};

		Callable<String> callableTask = () -> {
		    TimeUnit.MILLISECONDS.sleep(300);
		    String str = "Callable-"+ count++;
		    return str;
		};

		List<Callable<String>> callableTasks = new ArrayList<>();
		callableTasks.add(callableTask);
		callableTasks.add(callableTask);
		callableTasks.add(callableTask);
		
		List<Runnable> runnableTasks = new ArrayList<>();
		runnableTasks.add(runnableTask);
		runnableTasks.add(runnableTask);
		runnableTasks.add(runnableTask);
		
		ExecutorService executorService = Executors.newFixedThreadPool(3);
		//execute() method accepts only Runnable object and not callable object
		executorService.execute(runnableTask);
		Future<?> future = executorService.submit(runnableTask);
		System.out.println(future.get());
		//It will return null.So we can omit catching value in a variable in case of Runnable tasks 
		
		Future<String> futureStr = executorService.submit(callableTask);
		//It will return a value of Future type.In this case since return type of callableTask is String so it is returning Future<String>.
		
		try {
			//It will execute all tasks of the collection but will return a single value of return type of any one Callable task.
			String str = executorService.invokeAny(callableTasks);
			System.out.println(str);
			//It throws 'InterruptedException' as well as 'ExecutionException' which are checked exceptions.hence we must handle them either with try-catch or throws keyword.
			//It accepts only Callable tasks and not Runnable tasks.So following line will throw error.
			//executorService.invokeAny(runnableTasks);
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
		
		
		try {
			//It will execute all tasks and returns 
			List<Future<String>> listFutureStr = executorService.invokeAll(callableTasks);
			//It throws 'InterruptedException' which is a checked exception.hence we must handle it either with try-catch or throws keyword.
			
			//It also accepts only Callable tasks and not Runnable tasks.So following line will throw error.
			//executorService.invokeAny(runnableTasks);
			
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		executorService.shutdown();
		System.out.println(executorService.isShutdown());
		System.out.println(executorService.isTerminated());
		Thread.sleep(3000);
		System.out.println(executorService.isTerminated());
	}
}
