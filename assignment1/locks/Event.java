package assignment1.locks;

/**
 * This class is a simple implementation of an event semaphore
 */
public class Event {
	private boolean occurred;

	/**
	 * Default constructor, to initialize global variables
	 */
	public Event() {
		occurred = false;
	}	

	/**
	 * This method is used by the calling thread to block, waiting for the event to happen.
	 * If the event has not occurred yet, the calling thread is blocked.
	 * Spurious wake-ups are managed inside, so there is no need to put this call inside a while
	 */
	public synchronized void await() {
		while (!occurred) {	// to avoid spourious wake-ups
			try {
				wait();
			} catch(InterruptedException e) {}
		}
		occurred = false;	// this way it could also be used in a loop
	}

	/**
	 * This method is used to notify to the waiting thread that the event has occurred
	 */
	public synchronized void signal() {
		occurred = true;
		notify();
	}
}
