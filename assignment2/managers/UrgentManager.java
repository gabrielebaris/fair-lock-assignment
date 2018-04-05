package assignment2.managers;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.Condition;

import assignment1.locks.FairLock;
import assignment2.clients.ClientA;
import assignment2.clients.ClientB;

/**
 * This class implements a manager according to the semantics Signal-and-Urgent, 
 * as specified in Assignment 2.0. Spurious wake-ups are not handled because
 * they are managed in a low level way by the FairLock itself
 */
public class UrgentManager implements SingleManager {

	private final Lock lock;
	private final Condition waitsetA;
	private final Condition waitsetB;
	private Thread owner;			// actual owner of the thread
	private int waitingA;			// number of blocked A clients
	private int waitingB;			// number of blocked B clients

	/**
	 * Default constuctor, used to initlize class variables
	 */
	public UrgentManager() {
		lock = new FairLock();
		waitsetA = lock.newCondition();
		waitsetB = lock.newCondition();
		owner = null;
		waitingA = 0;
		waitingB = 0;
	}

	/**
	 * This method implements the request() of the SingleManager interface. This is used by the
	 * client to ask for a resource: if it's free, the client gets it; otherwise, it blocks until it can 
	 * get the resource, as specified in Assignemt 2.0
	 */
	public void request() {
		Thread t = Thread.currentThread();
		System.out.println(" > " + t.getName() + " calls request");
		
		lock.lock();
		
		try {
			
			if(owner != null) {
				if(t instanceof ClientA) {
					waitingA++;
					System.out.println(" > " + t.getName() + " must block on waitsetA");
					waitsetA.await();
				} else if(t instanceof ClientB) {
					waitingB++;
					System.out.println(" > " + t.getName() + " must block on waitsetB");
					waitsetB.await();
				} else {
					new IllegalMonitorStateException("> EX: This type of client is not supported");
				}
			} else if(owner == t) {
				new IllegalMonitorStateException("> EX: " + t.getName() + " already owns the resource");
			}
			
			// If the resource is free, t becomes soon the owner; othwerwise, it becomes the owner as soon
			// as it is awakened from the condition
			owner = t;
			
			System.out.println(" > " + t.getName() + " gets the resource");
		
		} catch(InterruptedException e) {}

		finally {
			lock.unlock();
		}
	}

	/**
	 * This method implements the release() of the SingleManager interface. This is used by the
	 * client to release a resource: if some threads are waiting to acquire the resource, one of 
	 * them is awakened according to the policy specified in Assignemt 2.0
	 */
	public void release(){
		Thread t = Thread.currentThread();
		System.out.println(" > " + t.getName() + " calls release");
		
		lock.lock();
		
		try {
			if(owner != t)
				new IllegalMonitorStateException(" > EX: " + t.getName() + " is trying to release a resource it does not own");
			else{
				if(waitingB > 0) {
					System.out.println(" > " + t.getName() + " awakes a thread from waitsetB");
					waitingB--;
					waitsetB.signal();
				} else if(waitingA > 0) {
					System.out.println(" > " + t.getName() + " awakes a thread from waitsetA");
					waitingA--;
					waitsetA.signal();
				} else {
					System.out.println(" > " + t.getName() + " releases the resource");
					owner = null;
				}
			}
		}

		finally {
			lock.unlock();
		}

	}
}