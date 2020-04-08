package transaction.transaction.tp3;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
//import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;


public class WebGrep {

	//private final static LinkedList<String> explored = new LinkedList<String>();
	static final ExecutorService service = Executors.newFixedThreadPool(10);

	//private final static ConcurrentSkipListSet<String> explored = new ConcurrentSkipListSet<String>();  

	private final static BlockingQueue<ParsedPage> matches = new LinkedBlockingQueue<ParsedPage>();
	
	//dictionnary
	static WfreeDictionary explored = new WfreeDictionary(); 
	/*
	 *  TODO : the search must be parallelized between the given number of threads
	 */
	private static void explore(String address) {
		service.execute(new Runnable() {
			@Override
			public void run() {
				try {
					/*
					 * Check that the page was not already explored and adds it
					 * TODO : the check and insertion must be atomic. Explain why. How to do it?
					 */
					//if(!explored.contains(address)) {
						explored.add(address);
					
						// Parse the page to find matches and hypertext links
						ParsedPage page = Tools.parsePage(address);
						if(!page.matches().isEmpty()) {
							/* 
							 * TODO: Tools.print(page) is not thread safe...
							 */
							//Tools.print(page);
							
							synchronized(matches) {
								matches.add(page);
							}
							// Recursively explore other pages
							for(String href : page.hrefs()) explore(href);
						}
					//}
				} catch (IOException e) {/*We could retry later...*/}
			}
		});
	}

	public static void main(String[] args) throws InterruptedException, IOException {

		// Initialize the program using the options given in argument
		if(args.length == 0) Tools.initialize("-celt --threads=1000 Nantes https://fr.wikipedia.org/wiki/Nantes");
		else Tools.initialize(args);

		// TODO Just do it!
		System.err.println("You must search parallelize the application between " + Tools.numberThreads() + " threads!\n");

		// Get the starting URL given in argument
		for(String address : Tools.startingURL()) 
			explore(address);

		//thread printer
		new Thread(() -> {
			while(true) {
				try {
					ParsedPage page = matches.take();
					Tools.print(page);
				} catch (InterruptedException e) { }
			}
		}).start();
	}
}

