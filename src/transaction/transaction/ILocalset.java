package transaction.transaction;


public interface ILocalset<T> {
	
	public boolean contains(Registre<T> registre);
	public void addRegistre(Registre<T> registre);
	public void clearSet();

}
