package com.tp.multithreading;

enum InterviewStatus{
	Started, Questioned, Answwered
}

class Interview {
	   InterviewStatus currentInterviewStatus = null;
	   
	   public void startInterview() {
		   currentInterviewStatus = InterviewStatus.Started;
	   }

	   public synchronized void Question(String ques) throws InterruptedException {
	      if (currentInterviewStatus != InterviewStatus.Started || currentInterviewStatus != InterviewStatus.Answwered) {
	         try {
	            wait();
	         } catch (InterruptedException e) {
	            e.printStackTrace();
	         }
	      }
	      if(currentInterviewStatus != InterviewStatus.Started)
	      Thread.sleep(2000);
	      System.out.println(ques);
	      currentInterviewStatus = InterviewStatus.Questioned;
	      notifyAll();
	   }

	   public synchronized void Answer(String msg) throws InterruptedException {
	      if (currentInterviewStatus != InterviewStatus.Questioned) {
	         try {
	            wait();
	         } catch (InterruptedException e) {
	            e.printStackTrace();
	         }
	      }
	      
	      Thread.sleep(2000);
	      System.out.println(msg);
	      currentInterviewStatus = InterviewStatus.Answwered;
	      notifyAll();
	   }
	}


public class Part18_JoinAndNotifyAllMethod2 {
	public static void main(String[] args) {
		Interview interview = new Interview();
		new Thread(new Runnable() {
			String[] questions = {"Hi Aman, How are you?","I am also fine Aman.\nTell me about youself?", "What is your expected CTC?"};
			@Override
			public void run() {
				for (String q : questions) {
					try {
						interview.Question(q);
					} catch (InterruptedException e) {
						System.err.println(e.getMessage());
					}
				}
			}
		}).start();
		new Thread(new Runnable() {
			String[] answers = {"Good sir.How about you?","I am Aman Saurabh.\nI have completed B.E in CS and currently working as senior software developer in ABC Ltd.","Sir, I am expecting around 15 Lakhs."};
			@Override
			public void run() {
				for (String a : answers) {
					try {
						interview.Answer(a);
					} catch (InterruptedException e) {
						System.err.println(e.getMessage());
					}
				}
				
			}
		}).start();
		
		synchronized (interview) {
			interview.startInterview();
			interview.notifyAll();
		}
	}
}
