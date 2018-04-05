package assignment1.test;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.Condition;
import assignment1.locks.FairLock;

/**
 * A simple Producer/Consumer application to test the behavior of the Condition implementation
 */
public class TestCondition {
	public static final int ITERATIONS = 10;
	public static void main(String args[]){
		Buffer b = new Buffer();
		new Consumer("Consumer", b).start();
		new Producer("Producer", b).start();
	}
	
}

/**
 * Custom Thread class used as produrcer
 */
class Producer extends Thread {
	private Buffer b;

	public Producer(String name, Buffer b) {
		super(name);
		this.b = b;
	}

	public void run(){
		try {
			for(int i = 0; i < TestCondition.ITERATIONS; i++) {
				sleep(500);
				System.out.println(" > " + getName() + " puts " + i + " inside the buffer");
				b.put(i);
			}
		} catch(InterruptedException e) {}
	}
}

/**
 * Custom Thread class used as consumer
 */
class Consumer extends Thread {
	private Buffer b;

	public Consumer(String name, Buffer b){
		super(name);
		this.b = b;
	}

	public void run() {
		try {
			for(int i = 0; i < TestCondition.ITERATIONS; i++) {
				sleep(200);
				int x = b.get();
				System.out.println(" < " + getName() + " gets " + x + " from the buffer");
			}
		} catch(InterruptedException e) {}
	}
}

/**
 * Simple implementation of a multi-slot buffer of integers
 */
class Buffer {
	private int[] t;
	private final int SIZE;
	private int front, rear, count;
	private static final int DEFAULT_SIZE = 1;

	private final Lock lock;
	private final Condition isEmpty;
	private final Condition isFull;

	public Buffer(int size) {
		SIZE = size;
		t = new int[SIZE];
		front = rear = count = 0;
		lock = new FairLock();
		isEmpty = lock.newCondition();
		isFull = lock.newCondition();
	}

	public Buffer() {
		this(DEFAULT_SIZE);
	}

	/**
	 * Puts a value inside the buffer
	 * @param  x                    value to be inserted
	 * @throws InterruptedException 
	 */
	public void put(int x) throws InterruptedException{
		try{
			lock.lock();
			while (count == SIZE)
				isFull.await();
			t[rear] = x;
			System.out.println(" - Added " + x + " in [" + rear + "]");
			rear = (rear + 1) % SIZE;
			count++;
			isEmpty.signal();
		} finally {
			lock.unlock();
		}
	}

	/**
	 * Gets a value from the buffer
	 * @return value to be returned
	 * @throws InterruptedException
	 */
	public int get() throws InterruptedException{
		try{
			lock.lock();
			while (count == 0)
				isEmpty.await();
			int x = t[front];
			System.out.println(" - Got " + x + " from [" + front + "]");
			front = (front + 1) % SIZE;
			count--;
			isFull.signal();
			return x;
		} finally {
			lock.unlock();
		}
	}


}