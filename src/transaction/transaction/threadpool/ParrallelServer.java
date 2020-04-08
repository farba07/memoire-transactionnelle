package transaction.transaction.threadpool;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


/**
 * Server using a fixed thread pool to compute blocks
 * @author farba
 *
 */
public class ParrallelServer implements Server{

	private final int nthreads = 96;
	
	ExecutorService executorService = Executors.newFixedThreadPool(nthreads);
	
	
	@Override
	public Future<Block> getBlock(Task task) {
		
		Future<Block> future = executorService.submit(task);
		
		return future;
	}

}
