package transaction.transaction;


public interface IRegistre<T> {
	/**
	 * Permet d'ecrire une nouvelle valeur dans le registre
	 * @param transaction Transaction qui essaye d'ecrire dans le registre
	 * @param value Valeur dans le registre
	 * @throws AbortException 
	 */
	public void write(Transaction<T> transaction, T value) throws AbortException;
	
	/**
	 * Permet de lire la valeur dans le registre
	 * @param transaction Transaction qui essaye de lire la valeur du registre
	 * @return La derniere valeur ecrite dans le registre
	 * @throws AbortException
	 */
	public T read(Transaction<T> transaction) throws AbortException;
}
