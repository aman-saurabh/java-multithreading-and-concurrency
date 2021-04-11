package com.tp.multithreading;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

/*
 * Similar to synchronized block, static synchronized block also exist.Infact both are same and the only difference is :
 * In synchronized block we pass object reference as argument to synchronized() method which in static synchronized block we pass class reference.
 * In this part again we will repeat same example as in part13.But this time we will use seperate object of MyReservationSystem class for every thread.
 * And again since we can't create two classes with Same name hence we are changing class name here from MyReservationSystem to MyMyReservationSystem. 
 * And here we have made 'availableSeats' variable also as static.As every thread is working on different objets hence if we don't make this variable as static then every thread will have its own informaton about seats also.
 * So to avoid this we changed 'availableSeats' variable also as static.To get class level lock we changed "synchronized this" to "synchronized (MyReservationSystem.class)". 
 */

class MyReservationSystem {
	private static Map<ReservationClass, List<Integer>> availableSeats;

	public MyReservationSystem() {
		availableSeats = new HashMap<ReservationClass, List<Integer>>();
		availableSeats.put(ReservationClass.Economy, Arrays.asList(11, 17, 23));
		availableSeats.put(ReservationClass.Business, Arrays.asList(107, 129));
	}

	public void checkAvailablity(ReservationClass className) {
		int seatCount = availableSeats.get(className).size();
		System.out.println(seatCount > 0 ? "Steat is available" : "Sorry, no seat is available.");
	}

	public int bookTicket(ReservationClass className) throws Exception {
		for (int i = 1; i <= 3; i++) {
			System.out.println(MessageFormat.format("Mandatory activity {0} for booking ticket, Thread : {1}",i, Thread.currentThread().getName()));
		}
		synchronized (MyReservationSystem.class) {
			Thread.sleep(2000);
			List<Integer> availableSeatsInCLass = availableSeats.get(className);
			int seatCount = availableSeatsInCLass.size();
			if (seatCount > 0) {
				// To generate a random number in range (0 to seatCount)
				int num = ThreadLocalRandom.current().nextInt(0, seatCount);
				int bookedSeatNum = availableSeatsInCLass.get(num);
				List<Integer> newAvailSeatsInCLass = availableSeatsInCLass.stream().filter(n -> n != bookedSeatNum)
						.collect(Collectors.toList());
				availableSeats.replace(className, newAvailSeatsInCLass);
				System.out.println(
						MessageFormat.format("Your seat is confirmed in {0} class.Seat number - {1}", className.getLabel(), bookedSeatNum));
				return bookedSeatNum;
			} else {
				throw new Exception("No seats available");
			}
		}
	}

	public int changeReservedClass(int currentSeatNumber) throws Exception {
		for (int i = 1; i <= 3; i++) {
			System.out.println(MessageFormat.format("Mandatory activity {0} for upgrading class, Thread : {1}",i, Thread.currentThread().getName()));
		}
		ReservationClass previousClass = currentSeatNumber > 100 ? ReservationClass.Business : ReservationClass.Economy;
		ReservationClass newClass = previousClass == ReservationClass.Business ? ReservationClass.Economy
				: ReservationClass.Business;
		synchronized (MyReservationSystem.class) {
			List<Integer> availableSeatsInCLass = availableSeats.get(newClass);
			int seatCount = availableSeatsInCLass.size();
			if (seatCount > 0) {
				// To generate a random number in range (0 to seatCount)
				int num = ThreadLocalRandom.current().nextInt(0, seatCount);
				int bookedSeatNum = availableSeatsInCLass.get(num);
				List<Integer> newAvailSeatsInCLass = availableSeatsInCLass.stream().filter(n -> n != bookedSeatNum)
						.collect(Collectors.toList());
				availableSeats.replace(newClass, newAvailSeatsInCLass);
				List<Integer> availableSeatsInPreviousCLass = availableSeats.get(previousClass);
				availableSeatsInPreviousCLass.add(currentSeatNumber);
				availableSeats.replace(previousClass, availableSeatsInPreviousCLass);
				System.out.println(
						MessageFormat.format("Your seat is successfully changed to {0} class.New seat number - {1}", newClass.getLabel(), bookedSeatNum));
				return bookedSeatNum;
			} else {
				throw new Exception("Updation not possible as no seats available in "+newClass+" class.");
			}
		}
	}
}

public class Part15_StaticSynchronizedBlock {
	public static void main(String[] args) {
		Runnable r1 = () -> {
			try {
				//To create own copy of MyReservationSystem object for every thread.
				MyReservationSystem rs = new MyReservationSystem();
				rs.bookTicket(ReservationClass.Economy);
			} catch (Exception e) {
				System.err.println(e.getMessage());
			}
		};
		
		Runnable r2 = () -> {
			try {
				//To create own copy of MyReservationSystem object for every thread.
				MyReservationSystem rs = new MyReservationSystem();
				int seatNum = rs.bookTicket(ReservationClass.Business);
				Thread.sleep(5000);
				rs.changeReservedClass(seatNum);
			} catch (Exception e) {
				System.err.println(e.getMessage());
			}
		};
		
		Thread t1 = new Thread(r1, "FirstChildThread");
		Thread t2 = new Thread(r1, "SecondChildThread");
		Thread t3 = new Thread(r2, "ThirdChildThread");
		Thread t4 = new Thread(r2, "FourthChildThread");
		t1.start();
		t2.start();
		t3.start();
		t4.start();
	}
}
