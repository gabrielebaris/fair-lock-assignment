package assignment2.test;

import java.util.Scanner;

import assignment2.managers.SingleManager;
import assignment2.managers.ContinueManager;
import assignment2.managers.UrgentManager;

import assignment2.clients.Client;
import assignment2.clients.ClientA;
import assignment2.clients.ClientB;

/**
 * This class is a simple test case for the managers implemented for Assignment 2
 */
public class TestManagers {

	/**
	 * This method generates some random clients of type A and B to check if the behavior of the
	 * ContinueManager class satisfies the requirements of Assignment 2.0
	 */
	public void testContinue(){
		System.out.println(" === Testing ContinueManager === ");
		
		SingleManager m = new ContinueManager();
		int N = 7;
		Client[] clients = new Client[N];

		for(int i = 0; i < N; ++i) {
			clients[i] = ((int)(Math.random()*10) % 2 == 0) ? new ClientA(m) : new ClientB(m);
			clients[i].start();
			int delay = (int)(Math.random() * 100) + 500;
			try {
				Thread.currentThread().sleep(delay);
			} catch(InterruptedException e) {}
		}

		for(int i = 0; i < N; ++i) {
			try {
				clients[i].join();
			} catch(InterruptedException e) {}
		}

		System.out.println(" === End for Testing ContinueManager === \n");
	}

	/**
	 * This method generates some random clients of type A and B to check if the behavior of the
	 * UrgentManager class satisfies the requirements of Assignment 2.0
	 */
	public void testUrgent(){
		System.out.println(" === Testing UrgentManager === ");

		SingleManager m = new UrgentManager();
		int N = 7;
		Client[] clients = new Client[N];
		
		for(int i = 0; i < N; ++i) {
			clients[i] = ((int)(Math.random()*10) % 2 == 0) ? new ClientA(m) : new ClientB(m);
			clients[i].start();
			int delay = (int)(Math.random() * 100) + 500;
			try {
				Thread.currentThread().sleep(delay);
			}catch(InterruptedException e){}
		}

		for(int i = 0; i < N; ++i) {
			try {
				clients[i].join();
			} catch(InterruptedException e) {}
		}

		System.out.println(" === End for Testing UrgentManager === \n");
	}

	/**
	 * Interactive test
	 */
	public static void main(String args[]) {
		TestManagers test = new TestManagers();
		Scanner sc = new Scanner(System.in);
		System.out.println(" === Test program for Assignment 2 === \n");
		
		System.out.print("Type U for UrgentManager, C for ContinueManager or any other key to exit: ");
		char c = sc.next().charAt(0);
		
		while(c == 'C' || c == 'c' || c == 'U' || c == 'u') {
			if(c == 'C' || c == 'c')
				test.testContinue();
			else
				test.testUrgent();

			System.out.print("Type U for UrgentManager, C for ContinueManager or any other key to exit: ");
			c = sc.next().charAt(0);
		}
	}
}