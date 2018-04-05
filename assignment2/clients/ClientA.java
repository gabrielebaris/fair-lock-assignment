package assignment2.clients;

import assignment2.managers.SingleManager;

/**
 * This class implements a client of type A, extending the base Client class.
 * It's purpose is to acquire the resource from the manager, using it for
 * a certaing amount of time and then releasing it. The manager must implement the
 * SingleManager interface
 */
public class ClientA extends Client{

	private static final String NAME = "ClientA";
	private static int counter = 1;

	/**
	 * This constructor creates a client of type A passing data to its superclass constructor
	 * @param  m manager instance
	 */
	public ClientA(SingleManager m) {
		super(NAME + counter, m);
		counter++;
	}

	/**
	 * This method is the actual body of the thread, called via the start() method
	 */
	public void run() {
		m.request();
		
		busy();
		
		m.release();
	}

}