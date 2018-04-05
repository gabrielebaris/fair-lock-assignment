package assignment3.implementation;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Condition;

/**
 * This class is the implementation of MANAGER designed in assignment3/models/design_model.ltsa
 */
public class ModelManager {

	private static final boolean D = false;	// true for debug messages, false otherwise

	private final ReentrantLock lock;		// for mutual exclusion
	private final Condition waitAccess;		// to avoid that a new thread could "surpass" an awakened one
	private final Condition waitA;			// to suspend clients of type A
	private final Condition waitB;			// to suspend clients of type B

	private byte state;						// to encode the internal state
	/**
	 * Sates encoding:
	 * FREE				0
	 * MANAGE[0][0]		1
	 * MANAGE[1][0]		2
	 * MANAGE[2][0]		3
	 * MANAGE[0][1]		4
	 * MANAGE[1][1]		5
	 * MANAGE[2][1]		IMPOSSIBLE
	 * PASS_B[0][1]		6
	 * PASS_B[1][1]		7
	 * PASS_B[2][1]		IMPOSSIBLE
	 * PASS_A[1][0]		8
	 * PASS_A[2][0]		9
	 */

	/**
	 * Default constructor, used to initialize class variables
	 */
	public ModelManager(){
		lock = new ReentrantLock();
		waitAccess = lock.newCondition();	
		waitA = lock.newCondition();
		waitB = lock.newCondition();
		state = 0;
	}

	/**
	 * This method prints on the console some debug information when the D flag is true
	 * @param message message to be printed
	 */
	private void debug(String message) {
		if(D) System.out.println(" > " + message);
	}

	/**
	 * This method is called from clients of type A to request the resource. If the resource is free,
	 * the thread acquires it; otherwise, the thread is suspended
	 */
	public void request_a() {
		lock.lock();
		
		try {

			// According to the assumption of the problem, this situations are not possible
			if(state == 3 || state == 5 || state == 9)
				throw new RuntimeException("This should not happen");
			
			// If the resource is passing from the releasing thread to a previous waiting one, 
			// this thread must wait
			while(state >= 6)			// cyclic scheme - also avoid spurious wake-ups
				waitAccess.await();

			//set new state
			debug(Thread.currentThread().getName() + " - request_a - accepted in state " + state);
			state++;
			debug(Thread.currentThread().getName() + " - request_a - new state is " + state);

			if(state != 1){
				// Since the resource is not free, the thread must wait
				debug(Thread.currentThread().getName() + " - request_a - added to wait condition");
				while(state != 8 && state != 9)		// cyclic scheme - also avoid spurious wake-ups
					waitA.await();
				state -= 7;
			}

			// Resource acquired
			debug(Thread.currentThread().getName() + " - request_a - resource acquired");

			waitAccess.signalAll();
		} catch(InterruptedException e) {}
		
		finally {
			lock.unlock();
		}

	}

	/**
	 * This method is called from clients of type B to request the resource. If the resource is free,
	 * the thread acquires it; otherwise, the thread is suspended
	 */
	public void request_b() {
		
		lock.lock();
		
		try {

			// According to the assumption of the problem, this situations are not possible
			if(state >= 3 && state <= 7)
				throw new RuntimeException("This should not happen");

			// If the resource is passing from the releasing thread to a previous waiting one, 
			// this thread must wait
			while(state >= 8)			// cyclic scheme - also avoid spurious wake-ups
				waitAccess.await();

			//set new state
			debug(Thread.currentThread().getName() + " - request_b - accepted in state " + state);
			if(state == 0)
				state = 1;
			else
				state += 3;
			debug(Thread.currentThread().getName() + " - request_b - new state is " + state);

			if(state != 1){
				// Since the resource is not free, the thread must wait
				debug(Thread.currentThread().getName() + " - request_b - added to wait condition");
				while(state != 6 && state != 7)		// cyclic scheme - also avoid spurious wake-ups
					waitB.await();
				state -= 5;
			}

			// Resource acquired
			debug(Thread.currentThread().getName() + " - request_b - resource acquired");

			waitAccess.signalAll();
		} catch(InterruptedException e) {}
		
		finally {
			lock.unlock();
		}
	}

	/**
	 * This method is called from both clients of type A and B to release the resource. If there is at lease one 
	 * waiting thread, it is awakened according to the policy specified in Assignment 2.0
	 */
	public void release() {
		lock.lock();
		
		try {

			// According to the assumption of the problem, this situations are not possible
			if(state == 0 || state >= 6)
				throw new RuntimeException("This should not happen");

			//set new state
			debug(Thread.currentThread().getName() + " - release - accepted in state " + state);
			if(state == 2 || state == 3) 
				state += 6;
			else if(state == 4 || state == 5)
				state += 2;
			else
				state = 0;
			debug(Thread.currentThread().getName() + " - release - new state is " + state);
			
			//awake next thread
			if(state == 6 || state == 7){
				debug(Thread.currentThread().getName() + " - release - waking up a type B client");
				waitB.signal();
			} else if(state == 8 || state == 9){
				debug(Thread.currentThread().getName() + " - release - waking up a type A client");
				waitA.signal();
			}

			// Resource released
			debug(Thread.currentThread().getName() + " - release - resource released");

			waitAccess.signalAll();
		}

		finally {
			lock.unlock();
		}
	}
}