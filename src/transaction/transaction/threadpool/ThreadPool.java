package transaction.transaction.threadpool;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

import transaction.transaction.AbortException;
import transaction.transaction.Clock;
import transaction.transaction.Registre;
import transaction.transaction.Transaction;

/**
 * Server using my own implementation of thread pool to compute block with priority
 * @author farba
 */

public class ThreadPool implements Server{

	private static int nthreads = 10;	

	LinkedList<Registre<FutureTask<Block>>> memoire = new LinkedList<Registre<FutureTask<Block>>>();

	private List<PoolThread> threads = new ArrayList<PoolThread>();

	public ThreadPool() {

		//Horloge global
		@SuppressWarnings("unused")
		Clock clock = new Clock();

		for(int i =0 ; i<nthreads ; i++) {
			threads.add(new PoolThread());
		}

		for(PoolThread thread : threads) {
			thread.start();
		}
	}

	@Override
	public Future<Block> getBlock(Task task) {

		// Create the future
		FutureTask<Block> future = new FutureTask<Block>(task);

		memoire.add(new Registre<FutureTask<Block>>(future));

		// Return the future
		return future;
	}

	/*
	 * Idle poolthread that dequeued the future and run it
	 */
	private class PoolThread extends Thread{

		private boolean isStopped = false;

		public PoolThread() {}

		//run
		public void run() {

			while(!isStopped) {
				Transaction<FutureTask<Block>> transaction = new Transaction<FutureTask<Block>>();
				while(!transaction.isCommitted()) {
					try {
						transaction.begin();
						if(memoire.size()==0) {continue;}
						Registre<FutureTask<Block>> registre = memoire.getFirst();
						FutureTask<Block> ft = registre.read(transaction);
						ft.run();
						transaction.try_to_commit();
						memoire.remove(registre);
					} catch (AbortException e) {
						// retry
					}
				}
			}
		}
	}
}