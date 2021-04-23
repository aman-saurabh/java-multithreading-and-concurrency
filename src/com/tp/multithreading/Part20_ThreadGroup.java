package com.tp.multithreading;

public class Part20_ThreadGroup {
	public static void main(String[] args) {
		//We will create 2 types of threads. We want both type of threads to be active for few seconds or until intrupted.
		//We will also see how to stop all threads in a thread group if threads are still active at the time we want to destroy the thread-group.
		//But for that we need to create threads in appropriate manner.Following are two implementaions of Runnable interface for such threads. 
		//Using this first type(i.e r1) we will see how to stop threads if the thread is in sleeping or waiting state.    
		Runnable r1 = new Runnable() {
			public void run() {
				try {
					//Empty implementation.We just want it to be in sleeping state till the time we want to destroy thread-group.So that we can manually stop it by interrupting it.
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					System.err.println(Thread.currentThread().getName()+" got interrupted.");
				}
			}
		};
		//Using this second type we will see how to stop threads if thread is still busy in performing some tasks.
		//But for that, as you can see in r2, we have to manage condition beforehand while defining run() method itself.Based on your requirement you can use simple if condition as well instead of while loop.
		Runnable r2 = () -> {
			while (!Thread.interrupted()) {
				//Empty implementation.We just want it to be active until it is interrupted.
			}
		};
		ThreadGroup parentTg = new ThreadGroup("parent-thread-group");
		Thread t1 = new Thread(parentTg, r1, "parent-first");
		Thread t2 = new Thread(parentTg, r2, "parent-second");
		System.out.println("t2 priority before : "+t2.getPriority());
		
		parentTg.setMaxPriority(3);
		Thread t3 = new Thread(parentTg, r1, "parent-third");
		System.out.println("t3 priority : "+t3.getPriority());
		System.out.println("t2 priority after : "+t2.getPriority());
		
		ThreadGroup childTg1 = new ThreadGroup(parentTg, "child-thread-group1");
		Thread ct1 = new Thread(childTg1, r2, "child-first");
		System.out.println("ct1 priority before: "+ct1.getPriority());
		
		childTg1.setMaxPriority(7);
		System.out.println("childTg1 max priority : "+childTg1.getMaxPriority());
		Thread ct2 = new Thread(childTg1, r2, "child-second");
		System.out.println("ct2 priority : "+ct2.getPriority());
		System.out.println("ct1 priority after: "+ct1.getPriority());
		System.out.println("childTg1 parent name : "+childTg1.getParent().getName());
		//Not created any thread in this thread-group.Just to check how such subthread-groups are listed and counted in parent thread-group 
		ThreadGroup childTg2 = new ThreadGroup(parentTg, "child-thread-group2");
		
		ThreadGroup nestedChildTg = new ThreadGroup(childTg1, "nested-child-thread-group");
		Thread nct1 = new Thread(nestedChildTg, r2, "nested-child-thread-group-child");
		
		t1.start();
		t2.start();
		t3.start();
		ct1.start();
		ct2.start();
		nct1.start();
		//It will print information about parent-thread-group as well as its child thread-groups i.e child-thread-group as well as its child thread-groups i.e nested-child-thread-group
		//Enclosing to indentify the result
		System.out.println("***********************************************************");
		parentTg.list();
		System.out.println("***********************************************************");		
		
		System.out.println("parentTg active count : "+parentTg.activeCount());
		System.out.println("parentTg active group count : "+parentTg.activeGroupCount());
		
		Thread[] activeThreadEnumerate = new Thread[parentTg.activeCount()];
		parentTg.enumerate(activeThreadEnumerate);
		
		
		//Enclosing to indentify the result
		System.out.println("-------------------------------------------------------------");
		for (Thread thread : activeThreadEnumerate) {
			System.out.println(thread.getName());
		}
		System.out.println("-------------------------------------------------------------");

		
		ThreadGroup[] activeThreadGroupEnumerate = new ThreadGroup[parentTg.activeGroupCount()];
		parentTg.enumerate(activeThreadGroupEnumerate);
		
		//Enclosing to indentify the result
		System.out.println("*************************************************************");
		for (ThreadGroup tg : activeThreadGroupEnumerate) {
			System.out.println(tg.getName());
		}
		System.out.println("*************************************************************");

		//Before calling destroy() method the thread group must be empty i.e all threads of the thread group must be stopped.So we are calling interrupt.
		//As we have already defined run() method such that if thread gets interrupted it terminates the execution. 
		parentTg.interrupt();
		
		//Calling sleep method here so that all threads can complete its interrupt call.
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			System.err.println(e.getMessage());
		}
		parentTg.destroy();
		System.out.println("Thread group parentTg has been destroyed successfully.");
		parentTg.list();
	}
}
