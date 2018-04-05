package assignment1.test;

import java.util.concurrent.locks.Lock;
import assignment1.locks.FairLock;

/**
 * A simple test class to check the implementation of lock() and unlock().
 */
class TestLock {

	public static void main(String[] args) {
		TestLock test = new TestLock();
		test.test1();
		test.test2();
	}

	/**
	 * Simple method to check that an unlock() withous a lock() is not allowed
	 */
	public void test1(){
		System.out.println(" === Test1 === ");
		System.out.println("This should return 'Caught IllegalMonitorStateException':");

		Lock l = new FairLock();
		
		try {
			l.unlock();
		} catch (IllegalMonitorStateException e) {
			System.out.println("Caught IllegalMonitorStateException");
		}
		
		System.out.println(" === End Test1 === \n");
	}

	/**
	 * Simple method to check mutual exclusion and FIFO order for the FairLock
	 */
	public void test2(){
		System.out.println(" === Test2 === ");
		System.out.println("Check if the locks is acquired in mutual exclusion and in FIFO order:");
		
		Lock lock = new FairLock();
		final int N = 10;
		Thread[] threads = new Thread[N];
		for(int i = 0; i < N; ++i) {
			threads[i] = new MyThread("Thread-" + i, lock);
			threads[i].start();
		}

		try {
			for(int i = 0; i < N; ++i) {
				threads[i].join();
			}
		} catch(InterruptedException e) {
			System.out.println("Interrupted!");
		}
		
		System.out.println(" === End Test2 === \n");
	}

	
}

/**
 * Custom Thread class used in the test2() method
 */
class MyThread extends Thread {
	private int delay;
	private Lock lock;

	public MyThread(String name, Lock l){
		super(name);
		delay = (int) (Math.random() * 2000 + 1000);
		lock = l;
	}

	public void run() {
		busy();
		try {
			System.out.println(" > " + getName() + " calls lock()");
			lock.lock();
			System.out.println(" > " + getName() + " acquires the lock");
			busy();
		} finally {
			System.out.println(" > " + getName() + " releases the lock");
			lock.unlock();
		}
	}

	private void busy(){
		try {
			sleep(delay);
		}catch(InterruptedException e){}
	}

}