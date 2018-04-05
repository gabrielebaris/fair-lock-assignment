package assignment2.managers;

/**
 * This interface is used to give to the managers a common behavior
 */
public interface SingleManager {

	/**
	 * This metod must be called to acquire the resource
	 */
	public void request();

	/**
	 * This method must be called to release the resource
	 */
	public void release();
}



