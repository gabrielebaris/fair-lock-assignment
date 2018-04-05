package assignment3.implementation;

/**
 * This class represent the implementation of CLIENT_A designed in assignment3/models/design_model.ltsa
 */
public class ClientA extends Thread{
	private ModelManager m;

	/**
	 * Creates a new client of type A starting from its name and a manager
	 * @param  m manager
	 * @param  n name
	 */
	public ClientA(ModelManager m, String n){
		super(n);
		this.m = m;
	}

	/**
	 * This method simulates the thread using the resource for a certain amount of time
	 */
	private void use() {
		try {
			System.out.println(" - " + getName() + " gets the resource");
			int delay = (int)(Math.random()*4000) + 1000;
			System.out.println(" - " + getName() + " is busy for " + delay + " milliseconds");
			sleep(delay);
		} catch(InterruptedException e) {}
	}

	/**
	 * Body of the thread
	 */
	public void run(){
		System.out.println(" - " + getName() + " requests the resource");
		m.request_a();
		
		use();
		
		System.out.println(" - " + getName() + " releases the resource");
		m.release();
	}
}