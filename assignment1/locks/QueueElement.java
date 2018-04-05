package assignment1.locks;

/**
 * This class is used to manage the entry, urgent and condition queues of the FairLock.
 * Each object consists of a pair: a thread object and the event semaphore for the
 * event it wants to wait for.
 */
public class QueueElement {
	private Thread t;
	private Event e;

	/**
	 * This constructor creates a new queue element starting from a given thread object
	 * @param  t thread object
	 */
	public QueueElement(Thread t) {
		this.t = t;
		e = new Event();
	}

	/**
	 * This method returns the name of the thread associated to this queue element
	 * @return name of the thread
	 */
	public String getName() {
		return t.getName();
	}

	/**
	 * This method returns the thread object associated to this queue element
	 * @return thread object
	 */
	public Thread getThread() {
		return t;
	}

	/**
	 * This method lets the calling thread wait untile the event associated to this queue
	 * element has occurred. If the event has not occurred yet, this is a blocking operation,
	 * otherwise it's not
	 */
	public void await() {
		e.await();
	}

	/**
	 * This method lets the calling thread notify to the waiting thread that the event has
	 * occurred
	 */
	public void signal() {
		e.signal();
	}
}