package com.tp.concurrency.executor_service;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

class CustomThreadFactory implements ThreadFactory {
	// To store all created threads.
	public List<Thread> threadList = new ArrayList<>();

	@Override
	public Thread newThread(Runnable runnableObj) {
		//Condition to limit number of threads created to 5.
		if (threadList.size() < 5) {
			// To create a pattern for naming of threads.
			String threadName = "MyThread" + (threadList.size() + 1);
			Thread thread = new Thread(runnableObj, threadName);
			threadList.add(thread);
			return thread;
		} else {
			throw new IllegalStateException("Maximum thread limit reached.Can't create new one.");
		}
	}
}

public class Part2_ThreadFactoryBasics {
	public static void main(String[] args) {
		// Creating a CustomThreadFactory object
        CustomThreadFactory threadFactory
            = new CustomThreadFactory();
  
        // Creaing Runnable objects using the lambda
        // expression
        Runnable command1 = ()
            -> System.out.println("Command 1 executed");
        Runnable command2 = ()
            -> System.out.println("Command 2 executed");
        Runnable command3 = ()
            -> System.out.println("Command 3 executed");
        Runnable command4 = ()
            -> System.out.println("Command 4 executed");
        Runnable command5 = ()
            -> System.out.println("Command 5 executed");
        Runnable command6 = ()
                -> System.out.println("Command 6 executed");
  
        // Putting the commands in an ArrayList
        ArrayList<Runnable> array = new ArrayList<>(5);
        array.add(command1);
        array.add(command2);
        array.add(command3);
        array.add(command4);
        array.add(command5);
        array.add(command6);
  
        // creating threads and running them
        for (Runnable command : array) {
        	try {
                threadFactory.newThread(command).start();
			} catch (Exception e) {
				System.err.println("\n"+e.getMessage()+"\n");
			}
        }
        try {
			TimeUnit.MILLISECONDS.sleep(1000);
		} catch (InterruptedException e) {
			System.out.println("\n"+e.getMessage()+"\n");
		}
        
        System.out.println("Total created threads : "+threadFactory.threadList.size());
        ListIterator<Thread> li = threadFactory.threadList.listIterator();
        while (li.hasNext()) {
			Thread t = li.next();
			System.out.println("Thread name : "+t.getName());
		}
        
	}
}
