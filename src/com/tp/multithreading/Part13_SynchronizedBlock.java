package com.tp.multithreading;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

/*
 * In previous example we have made entire method as synchronized but what if we don't want to make entire method as synchronized 
 * and we want to make only a few part as synchronized then we should go for 'synchronized block'.
 * In this part we will see how to make a specific part of a method as synchronized.
 * Run this example several times.You will see no seat is booked twice for any class.
 * But comment synchronized block code and then run the program again.You might see that same seat is booked twice i.e data consistency error occured.
 * So to prevent such errors we must declare necessary code block as synchronized block as we did in this example.  
 */

enum ReservationClass {
	Economy("economy"), Business("business");

	private final String label;

	private ReservationClass(String label) {
		this.label = label;
	}
	
	public String getLabel() {
		return label;
	}
}


class ReservationSystem {
	private Map<ReservationClass, List<Integer>> availableSeats;

	public ReservationSystem() {
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
		synchronized (this) {
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
		synchronized (this) {
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

public class Part13_SynchronizedBlock {
	public static void main(String[] args) {
		ReservationSystem rs = new ReservationSystem();
		Runnable r1 = () -> {
			try {
				rs.bookTicket(ReservationClass.Economy);
			} catch (Exception e) {
				System.err.println(e.getMessage());
			}
		};
		
		Runnable r2 = () -> {
			try {
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
