package com.tp.concurrency.lock;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

class SynchronizedHashMapWithReadWriteLock {

    Map<String,String> syncHashMap = new HashMap<>();
    ReentrantReadWriteLock lock = new ReentrantReadWriteLock(true);
    //To create Lock object from ReadWriteLock object capable of performing write operations.
    WriteLock writeLock = lock.writeLock();
    //To create Lock object from ReadWriteLock object capable of performing read operations.
    ReadLock readLock = lock.readLock();
    //Note :- We can also hold lock object here as ReadWriteLock type object as ReentrantReadWriteLock implements ReadWriteLock interface. 
    //But in that case we would have to define writeLock and readLock as Lock type property instead of WriteLock and ReadLock.
    //Even now also, i.e even though we have defined lock as ReentrantReadWriteLock type variable, we can define these properties as Lock type as follows.
    //Lock writeLock = lock.writeLock();
    //Lock readLock = lock.readLock();
    
    //Now we can use these lock objects as normal lock objects and call all methods of 'Lock' interface.
    //Also note that "ReentrantReadWriteLock" class also supports almost all methods of normal "ReentrantLock" class.    

    //Write methods :- put() and remove()
    public void put(String key, String value) {
        try {
        	//Using tryLock() instead of simple lock() method.Just to show we can call methods of ReentrantLock class also. 
            if(writeLock.tryLock(2000, TimeUnit.MILLISECONDS)) {
            	syncHashMap.put(key, value);
            	System.out.println(key+" inserted");
            }
        } catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
            writeLock.unlock();
        }
    }
    
    public String remove(String key){
        try {
        	//Using lockInterruptibly() instead of simple lock() method.Just to show we can call methods of ReentrantLock class also.
            writeLock.lockInterruptibly();
            System.out.println(key+" removed");
            return syncHashMap.remove(key);
        } catch (InterruptedException e) {
			e.printStackTrace();
			return null;
		} finally {
            writeLock.unlock();
        }
    }
    
    //For both the write methods, we need to surround the critical section with the write lock, only one thread can get access to it:
    
    //Read methods :- get() and containsKey()
    public String get(String key){
        try {
            readLock.lock();
            return syncHashMap.get(key);
        } finally {
            readLock.unlock();
        }
    }

    public boolean containsKey(String key) {
        try {
            readLock.lock();
            return syncHashMap.containsKey(key);
        } finally {
            readLock.unlock();
        }
    }
    
    //For both read methods, we need to surround the critical section with the read lock. Multiple threads can get access to this section if no write operation is in progress.
    @Override
    public String toString() {
    	return syncHashMap.toString();
    }
}

public class Part05_ReentrantReadWriteLockExample1 {
	public static void main(String[] args) {
		SynchronizedHashMapWithReadWriteLock syncMap = new SynchronizedHashMapWithReadWriteLock();
		Runnable r1 = () -> {
			syncMap.put("First", "Aman");
			syncMap.put("Second", "Saurabh");
			syncMap.put("Third", "Manish");
			syncMap.put("Fourth", "Vijay");
			syncMap.put("Fifth", "Aryan");
		};
		Runnable r2 = () -> {
			System.out.println("Value of First : "+ syncMap.get("First"));
			System.out.println("Value of Second : "+ syncMap.get("Second"));
			System.out.println("Value of Third : "+ syncMap.get("Third"));
			System.out.println("Value of Fourth : "+ syncMap.get("Fourth"));
			System.out.println("Value of Fifth : "+ syncMap.get("Fifth"));
		};
		Runnable r3 = () -> {
			syncMap.remove("First");
			syncMap.remove("Second");
			syncMap.remove("Third");
			syncMap.remove("Fourth");
			syncMap.remove("Fifth");
		};
		Runnable r4 = () -> {
			System.out.println("SyncMap contains First : "+syncMap.containsKey("First"));
			System.out.println("SyncMap contains Second : "+syncMap.containsKey("Second"));
			System.out.println("SyncMap contains Third : "+syncMap.containsKey("Third"));
			System.out.println("SyncMap contains Fourth : "+syncMap.containsKey("Fourth"));
			System.out.println("SyncMap contains Fifth : "+syncMap.containsKey("Fifth"));
		};
		Thread t1 = new Thread(r1);
		Thread t2 = new Thread(r2);
		Thread t3 = new Thread(r3);
		Thread t4 = new Thread(r4);
		t1.start();
		t2.start();
		t3.start();
		t4.start();
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println(syncMap);
	}
}
