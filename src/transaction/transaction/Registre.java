package transaction.transaction;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Registre<T> implements IRegistre<T>{

	/**
	 * Stocke la derniere valeur ecrite dans le registre
	 */
	private T value;
	
	/**
	 * Stocke la date de la derniere transaction acceptee
	 */
	private int date ;
	
	/**
	 * Permet de verrouiller le registre
	 */
	private Lock verrou;

	public Registre(T value){
		this.value = value;
		 verrou = new ReentrantLock();
	}

	@Override
	public void write(Transaction<T> transaction, T value) throws AbortException{

		if(!transaction.lwst.contains(this)) {
			transaction.lcx = new Registre<T>(null);
		}
		transaction.lcx.value = value;
		transaction.lwst.addRegistre(this);
	}

	@Override
	public T read(Transaction<T> transaction) throws AbortException{
		if(transaction.lrst.contains(this)){
			return (T) transaction.lcx.value;
		}else {
			transaction.lcx = this;
			transaction.lrst.addRegistre(this);
			if(transaction.lcx.date > transaction.getBirthDate()) throw new AbortException();
			else return (T) transaction.lcx.value;
		}
	}

	//getters et setters
	public Lock getLock() {return verrou;}
	public T getValue(){return value;}
	public void setValue(T value) {this.value = value;}
	public int getDate() {return this.date;}
	public void setDate(int date) {this.date = date;}
}
