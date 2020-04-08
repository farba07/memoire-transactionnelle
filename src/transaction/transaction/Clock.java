package transaction.transaction;

import java.util.concurrent.atomic.AtomicInteger;

public class Clock {
	
	static AtomicInteger globalClock;
	
	public Clock() {
		globalClock = new AtomicInteger(0) ;
	}
	
	public Integer getClock() {
		return globalClock.get();
	}
}
