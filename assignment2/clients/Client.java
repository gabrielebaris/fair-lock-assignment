package assignment2.clients;

import assignment2.managers.SingleManager;

/**
 * This is the base class provided for both clients of type A and B. All of them
 * will work on an implementation of the interface SingleManager. Since it's a
 * subclass of Thread, this can be used as any other thread object
 */
public class Client extends Thread {
	protected SingleManager m;

	/**
	 * This constructor creates a new Client starting from its name and an implementation of SingleManager
	 * @param  name name for the thread object
	 * @param  m    manager object
	 */
	public Client(String name, SingleManager m) {
		super(name);
		this.m = m;
	}

	/**
	 * This method simulates the client busy for a certain amount of time
	 */
	protected void busy(){
		int delay = (int)(Math.random() * 4000) + 1000;
		
		try {
			System.out.println(" - " + getName() + " uses the resource for " + delay + " milliseconds");
			sleep(delay);
		}catch(InterruptedException e){}
	}
}