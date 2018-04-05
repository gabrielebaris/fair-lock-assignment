package assignment1.locks;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.TimeUnit;
import java.util.LinkedList;
import java.util.Date;

/**
 * This class is the implementation of the FairLock according to the specification of Assignment 1
 */
public class FairLock implements Lock {
	
	private static final boolean D = false;			// if true, prints debug info on the console
	private LinkedList<QueueElement> entryQueue;	// entry queue
	private LinkedList<QueueElement> urgentQueue;	// urgent queue
	private Thread owner;							// if null, the lock is free; otherwise, it
													// represents the current owner of the lock

	/**
	 * Default constructor, used to initialize class variables
	 */
	public FairLock() {
		entryQueue = new LinkedList<>();
		urgentQueue = new LinkedList<>();
		owner = null;
	}

	/**
	 * Prints on the console the String passed as argument if the D flag is true
	 * @param s string to be printed
	 */
	private void debug(String s) {
		if(D) System.out.println(" # " + s);
	}

	/**
	 * This method is used to acquire the lock if it's free, otherwise
	 * the calling thread is added to the entry queue
	 */
	public void lock() {
		QueueElement block = null;	// Pointer to the thread that must block

		synchronized(this){
			if(owner == Thread.currentThread()) {
				throw new IllegalMonitorStateException("This type of lock is not reentrant");
			} else if(owner == null) {
				owner = Thread.currentThread();
				debug(owner.getName() + " acquires the lock");
			} else {
				block = new QueueElement(Thread.currentThread());
				entryQueue.add(block);
				debug(block.getName() + " is added to the entry queue");
			}
		}

		if(block != null) {
			debug(block.getName() + " waits in the entry queue");
			block.await();		//spourious wake-ups are prevented inside 
		}
		
	}

	/**
	 * This method is used to release the lock. According to the semantics, you first need to check if there is at lease
	 * one thread in the urgent queue to pass the baton to. If not, check the entry queue. If also this queue is empty, then
	 * just releases the lock.
	 */
	public void unlock() {
		QueueElement awake = null;	// pointer to the thread that must awake

		synchronized(this) {
			if(Thread.currentThread() != owner) {
				throw new IllegalMonitorStateException("The unlock must be called by the owner of the lock");
			}
			if(urgentQueue.size() > 0) {
				awake = urgentQueue.poll();
				debug(Thread.currentThread().getName() + " passes the baton to " + awake.getName() + " from the urgent queue");
			} else if(entryQueue.size() > 0) {
				awake = entryQueue.poll();
				debug(Thread.currentThread().getName() + " passes the baton to " + awake.getName() + " from the entry queue");
			} else {
				debug(Thread.currentThread().getName() + " releases the lock");
				owner = null;
			}
		}
		
		if(awake != null) {
			synchronized(awake) {
				debug(awake.getName() + " is awakened");
				owner = awake.getThread();
			}
			awake.signal();
		}
	
	}

	/**
	 * This method returns a new Condition instance that is bound to this Lock instance.
	 */
	public Condition newCondition() {
		return new FairCondition();
	}

	/**
	 * Acquires the lock unless the current thread is interrupted.
	 */
	public void lockInterruptibly() {
		throw new UnsupportedOperationException();
	}

	/**
	 * Acquires the lock only if it is free at the time of invocation.
	 */
	public boolean tryLock() {
		throw new UnsupportedOperationException();
	}

	/**
	 * Acquires the lock if it is free within the given waiting time and the current thread has not been interrupted.
	 * @param	time
	 * @param	unit
	 */
	public boolean tryLock(long time, TimeUnit unit) {
		throw new UnsupportedOperationException();
	}

	/**
	 * This class represents the implementation of the FIFO condition according to the specification of Assignment 1
	 */
	private class FairCondition implements Condition {

		private LinkedList<QueueElement> conditionQueue;	// FIFO queue for the condition

		/**
		 * Default constructor, used to initalize class variables
		 */
		public FairCondition() {
			conditionQueue = new LinkedList<>();
		}


		/**
		 * This method causes the current thread to wait until it is signalled or interrupted.
		 * The calling thread is enqueued in the condition queue
		 */
		public void	await() {
			QueueElement block = new QueueElement(Thread.currentThread());

			synchronized(FairLock.this) {
				if(Thread.currentThread() != owner) {
					throw new IllegalMonitorStateException("The await must be done by the owner of the lock");
				}

				debug(block.getName() + " is added to condition queue");
				conditionQueue.add(block);
			}

			FairLock.this.unlock();	// release mutual exclusion before suspension

			block.await();		// spurious wake-ups are prevented inside
			
		}

		/**
		 * This method is used to wake up the next (in FIFO order) waiting thread on this condition.
		 * If the condition queue is empty, this is no operation
		 */
		public void	signal() {
			QueueElement awake = null;
			QueueElement block = new QueueElement(Thread.currentThread());

			synchronized(FairLock.this) {
				if(Thread.currentThread() != owner) {
					throw new IllegalMonitorStateException("The signal must be done by the owner of the lock");
				}

				if(conditionQueue.size() > 0) {
					awake = conditionQueue.poll();
					debug(awake.getName() + " extracted from condition queue");
					urgentQueue.add(block);
					debug(block.getName() + " is added to the urget queue");
					owner = awake.getThread();
				}
			}
			
			if(awake != null) {
				debug(awake.getName() + " is awakened from condition");
				awake.signal();

				debug(block.getName() + " is suspended in signal");
				block.await();
			}
		}

		/**
		 * Causes the current thread to wait until it is signalled or interrupted, or the specified waiting time elapses.
		 */
		public boolean	await(long time, TimeUnit unit) {
			throw new UnsupportedOperationException();
		}

		/**
		 * Causes the current thread to wait until it is signalled or interrupted, or the specified waiting time elapses.
		 * @param  nanosTimeout 
		 * @return 
		 */
		public long	awaitNanos(long nanosTimeout) {
			throw new UnsupportedOperationException();
		}
		
		/**
		 * Causes the current thread to wait until it is signalled.
		 */
		public void	awaitUninterruptibly() {
			throw new UnsupportedOperationException();
		}
		
		/**
		 * Causes the current thread to wait until it is signalled or interrupted, or the specified deadline elapses.
		 * @param  deadline 
		 * @return 
		 */
		public boolean	awaitUntil(Date deadline) {
			throw new UnsupportedOperationException();
		}
		
		/**
		 * Wakes up all waiting threads.
		 */
		public void	signalAll() {
			throw new UnsupportedOperationException();
		}
	}

}


























