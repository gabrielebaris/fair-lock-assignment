package assignment3.test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.Scanner;

import assignment3.implementation.ModelManager;
import assignment3.implementation.ClientA;
import assignment3.implementation.ClientB;

/**
 * This class is used to check the behavior of the manager implemented according to what is modelled
 * in assignment3/models/design_model.ltsa
 */
public class TestImplementation {

	/**
	 * This method is used to check if the manager works as specified in Assignment 2.0
	 * (with a given arrival order)
	 */
	public void testAssignment(){
		System.out.println(" === Test Assignment ===");
		ModelManager m = new ModelManager();
		ClientA a1 = new ClientA(m, "ClientA1");
		ClientA a2 = new ClientA(m, "ClientA2");
		ClientB b = new ClientB(m, "ClientB");

		try {
			a1.start();
			Thread.sleep(800);
			b.start();
			Thread.sleep(200);
			a2.start();

			a1.join();
			a2.join();
			b.join();
		} catch(InterruptedException e) {}

		System.out.println(" === End Test Assignment ===\n");
	}

	/**
	 * This method is used to check if the manager works as specified in Assignment 2.0
	 * (with a random arrival order)
	 */
	public void testRandom(){
		System.out.println(" === Test Random === ");
		
		ModelManager m = new ModelManager();
		ArrayList<Thread> clients = new ArrayList<>();
		clients.add(new ClientA(m, "ClientA1"));
		clients.add(new ClientA(m, "ClientA2"));
		clients.add(new ClientB(m, "ClientB"));
		Collections.shuffle(clients, new Random(System.nanoTime()));
		
		clients.forEach((t) -> {
			t.start();
			try{
				Thread.sleep((int)Math.random() * 200 + 300);
			}
			catch(InterruptedException e){}
		});
		
		clients.forEach((t) -> {
			try{
				t.join();
			}
			catch(InterruptedException e){}
		});
		System.out.println(" === End Test Random === \n");
	}

	/**
	 * Interactive test
	 */
	public static void main(String args[]){
		TestImplementation t = new TestImplementation();
		
		Scanner sc = new Scanner(System.in);
		System.out.println(" === Test program for Assignment 3.1 === \n");
		
		System.out.print("Type A for 'Test Assignment', R for 'Test Random' or any other key to exit: ");
		char c = sc.next().charAt(0);
		
		while(c == 'R' || c == 'r' || c == 'A' || c == 'a') {
			if(c == 'A' || c == 'a')
				t.testAssignment();
			else
				t.testRandom();

			System.out.print("Type A for 'Test Assignment', R for 'Test Random' or any other key to exit: ");
			c = sc.next().charAt(0);
		}
	}
}

