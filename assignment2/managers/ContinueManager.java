package assignment2.managers;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Condition;
import java.util.LinkedList;

import assignment2.clients.ClientA;
import assignment2.clients.ClientB;

/**
 * This class implements a manager according to the semantics Signal-and-Continue, 
 * as specified in Assignment 2.1. Spurious wake-ups must be handled at this level
 * because they are not managed by the Java implementation
 */
public class ContinueManager implements SingleManager {

	private final Lock lock;
	private final Condition waitsetA;
	private final Condition waitsetB;
	private Thread owner;						// actual owner of the thread
	private LinkedList<Thread> waitingA;		// blocked A clients
	private LinkedList<Thread> waitingB;		// blocked B clients

	/**
	 * Default constuctor, used to initlize class variables
	 */
	public ContinueManager() {
		lock = new ReentrantLock();
		waitsetA = lock.newCondition();
		waitsetB = lock.newCondition();
		owner = null;
		waitingA = new LinkedList<>();
		waitingB = new LinkedList<>();
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
					waitingA.add(t);
					System.out.println(" > " + t.getName() + " must block on waitsetA");
					while(owner != t)		// to prevent spurious wake-ups
						waitsetA.await();
				} else if(t instanceof ClientB) {
					waitingB.add(t);
					System.out.println(" > " + t.getName() + " must block on waitsetB");
					while(owner != t)		// to prevent spurious wake-ups
						waitsetB.await();
				} else {
					new IllegalMonitorStateException("> EX: This type of client is not supported");
				}
			} else if(owner == t) {
				new IllegalMonitorStateException("> EX: " + t.getName() + " already owns the resource");
			} else {
				owner = t;
			}

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
				if(waitingB.size() > 0) {
					owner = waitingB.poll();
					System.out.println(" > " + t.getName() + " awakes " + owner.getName() + " from waitsetB");
					waitsetB.signalAll();
				} else if(waitingA.size() > 0) {
					owner = waitingA.poll();
					System.out.println(" > " + t.getName() + " awakes " + owner.getName() + " from waitsetA");
					waitsetA.signalAll();
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