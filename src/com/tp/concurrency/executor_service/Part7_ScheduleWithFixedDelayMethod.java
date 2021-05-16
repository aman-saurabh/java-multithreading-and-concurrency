package com.tp.concurrency.executor_service;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class Part7_ScheduleWithFixedDelayMethod {
	private static int count = 0;
	private static int countCallable = 0;
	public static void main(String[] args) throws InterruptedException {
		Runnable rTask = () -> {
			count++;
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm:ss a");
			System.out.println("Running runnable task. Count : "+count+". Time : "+formatter.format(LocalTime.now()));
			try {
				Thread.sleep(1000);
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
		ScheduledFuture<?> sFuture = sExecutorService.scheduleWithFixedDelay(rTask, 3, 2, TimeUnit.SECONDS);
		// Following code will throw error, since scheduleWithFixedDelay() method accepts only Runnable task and not Callable task.
		// ScheduledFuture<?> sFuture2 = sExecutorService.scheduleWithFixedDelay(cTask, 2, 3, TimeUnit.SECONDS);
		while (true) {
	        Thread.sleep(1000);
	        if (count == 5) {
	            System.out.println("Now count is 5, cancelling the Runnable task!");
	            //Cancelling task assigned to scheduleWithFixedDelay() method.
	            sFuture.cancel(true);
	            break;
	        }
	    }
	    // Shutting down ScheduledExecutorService 
	    sExecutorService.shutdown();
	    /*
	     * It might seems you exactly same as previous one(i.e scheduleAtFixedRate() method)
	     * But if you see the result carefully, you will find that the difference in time between two iterations is 
	     * 2 seconds in case of scheduleAtFixedRate() method(i.e in the previous program), while
	     * 3 seconds in case of scheduleWithFixedDelay() method(i.e in this program)
	     * It is because in case of scheduleAtFixedRate() method, 
	     * next task is started after specified delay of after the previous task is started.
	     * While in case of scheduleWithFixedDelay() method,
	     * next task is started after specified delay of after the previous task is completed.
	     * And in the runnable task since it sleeps for 1 sec.So it takes 1 second for the task to complete.
	     * And hence in case of scheduleWithFixedDelay() method total delay between two tasks becomes 2+1 = 3 seconds.
	     */
	}
}
