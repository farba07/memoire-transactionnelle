package transaction.transaction;

public class AbortException extends Exception{
	public AbortException(String message) {
		super(message);
	}
	public AbortException() {
		super();
	}
}
