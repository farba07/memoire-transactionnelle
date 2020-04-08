package transaction.transaction;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * Test memoire transactionnelle tl2
 * @author farba
 *
 */
public class Test{

	static List<Registre<Integer>> memoire = new LinkedList<Registre<Integer>>();

	public static void main(String[] args) throws InterruptedException {

		//---> Initialision de l'horloge global
		@SuppressWarnings("unused")
		Clock clock = new Clock();

		//---> Initialisation memoire partagee
		System.out.println("Etat initial de la memoire partagee : ");
		for(int i=0 ; i<15 ; i++) {
			memoire.add(new Registre<Integer>(i));
			System.out.print(memoire.get(i).getValue()+"\t");
		}
		System.out.println("");

		//---> Execution thread en //
		Thread thread[] = new Thread[10];
		for(Thread t : thread) {
			t = new Thread(new Runnable() {
				public void run() {
					Random r = new Random();
					int random = r.nextInt(15);
					Transaction<Integer> transaction = new Transaction<Integer>();
					while( ! transaction.isCommitted()) {
						try {

							//---> transaction....
							transaction.begin();
							System.out.println(Thread.currentThread().getName()+" a commence sa transaction..");							
							Thread.sleep(100);
							int randomValue = memoire.get(random).read(transaction);
							memoire.get(random).write(transaction, random+randomValue);
							transaction.try_to_commit();
							System.out.println(Thread.currentThread().getName()+" a bien fini sa transaction!");
							//---> fin transaction

						}catch(AbortException | InterruptedException e) {
							System.out.println(Thread.currentThread().getName()+" a avorte sa transaction..");
						}
					}
				}
			});
			t.start();
		}

		//---> Etat finale de la memoire partagee
		Thread.sleep(1000);
		System.out.println("Etat final de la memoire partagee");
		for(Registre<Integer> registre : memoire) {
			System.out.print(registre.getValue()+"\t");
		}
	}
}