package com.tp.concurrency.executor_service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class Part6_ScheduleAtFixedRateMethod {
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
         * In this example task execution takes less time than the specified delay,
         * So the delay between two iteration is same as specified delay (i.e 2 seconds),
         * But if the task execution will take more time then the specified delay then
         * the delay between two iteration will not be same as specified delay(i.e 2 seconds)
         * in such cases, the next task will start after the previous one is completed.
         * We will see its example later but before that let's see the difference bertween 
         * scheduleAtFixedRate() method and scheduleWithFixedDelay() method   
         */
	}
}
