package transaction.transaction;

/**
 * 
 * @author farba
 * 
 */
public interface ITransaction<T> {
	
	public void begin();
	public void try_to_commit() throws AbortException;
	public boolean isCommitted();
}
