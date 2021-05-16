package com.tp.concurrency.executor_service;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/*
 * This example is same as the example in Part6.
 * The only difference is we have increased the sleep time from 1 second to 5 second in this case.
 * So that the task execution time becomes more than the specified delay.
 */
public class Part8_ScheduledAtFixedRateWithInvalidDelay {
	private static int count = 0;
	private static int countCallable = 0;
	public static void main(String[] args) throws InterruptedException {
		Runnable rTask = () -> {
			count++;
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm:ss a");
			System.out.println("Running runnable task. Count : "+count+". Time : "+formatter.format(LocalTime.now()));
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				System.err.println(e.getMessage());
			}
		};
		Callable<Integer> cTask = () -> {
			countCallable++;
			System.out.println("Running callable task. Count : "+countCallable);
			return countCallable;
		};
		ScheduledExecutorService sExecutorService = Executors.newScheduledThreadPool(2);
		ScheduledFuture<?> sFuture = sExecutorService.scheduleAtFixedRate(rTask, 3, 2, TimeUnit.SECONDS);
		// Following code will throw error, since scheduleAtFixedRate() method accepts only Runnable task and not Callable task.
		// ScheduledFuture<?> sFuture2 = sExecutorService.scheduleAtFixedRate(cTask, 2, 3, TimeUnit.SECONDS);
		while (true) {
            Thread.sleep(1000);
            if (count == 5) {
                System.out.println("Now count is 5, cancelling the Runnable task!");
                //Cancelling task assigned to scheduleAtFixedRate() method.
                sFuture.cancel(true);
                break;
            }
        }
        // Shutting down ScheduledExecutorService 
        sExecutorService.shutdown();
        
        /*
         * In this example task execution takes more time than the specified delay,
         * So in this case the delay between two iteration is not same as specified delay(i.e 2 seconds)
         * The actual delay between two iterations in this case is 5 seconds
         * i.e the time, given Runnable task takes to be completed.    
         */
	}
}
