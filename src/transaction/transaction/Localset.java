package transaction.transaction;

import java.util.LinkedList;

/**
 * 
 * @author farba
 */
public class Localset<T> implements ILocalset<T>{

	private LinkedList<Registre<T>> set;
	
	public Localset() {
		set = new LinkedList<Registre<T>>();
	}
	
	@Override
	public void clearSet() {
		set.clear();		
	}
	
	@Override
	public void addRegistre(Registre<T> registre) {
		set.add(registre);
	}
	
	public boolean contains(Registre<T> registre) {
		if(this.set.contains(registre)) return true;
		return false;
	}
	
	//getters
	public Registre<T> get(Registre<T> registre) {return this.set.get(this.set.indexOf(registre));}
	public LinkedList<Registre<T>> get(){return set;}
}
