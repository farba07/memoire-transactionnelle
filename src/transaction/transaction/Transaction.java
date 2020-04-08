package transaction.transaction;


public class Transaction<T> implements ITransaction<T>{

	/**
	 * Date de debut de la transaction.
	 * se synchronise avec l'horloge global a chaque debut de transaction
	 */
	private int birthDate; 

	/**
	 * Ensemble contenant les registres lus au cours de la transaction transaction
	 */
	Localset<T> lrst;

	/**
	 * Ensemble contenant les registres modifies au cours de la transaction 
	 */
	Localset<T> lwst;

	/**
	 * Copie locale du registre en cours de lecture ou d'ecriture
	 */
	Registre<T> lcx;

	/**
	 * True si une transaction est acceptee et s'est deroulee correctement
	 */
	private boolean isCommitted;


	public Transaction(){
		lrst = new Localset<T>();
		lwst = new Localset<T>();
	}

	/**
	 * Debute une transaction : 
	 *   --> synchronise le birthdate avec l'horloge globale
	 *   --> reinitialise les ensembles lrst et lwst
	 */
	@Override
	public void begin() {

		birthDate = Clock.globalClock.get();

		lwst = new Localset<T>();
		lrst = new Localset<T>();

		isCommitted = false;
	}

	/**
	 * Essaye de committer les modifications effectuees au cours de la transaction :
	 * 	 --> Verrouille les registres de la memoire partagee
	 *   --> Verifie la coherence des valeurs ecrites
	 *   --> Valide les modifications
	 */
	@Override
	public void try_to_commit() throws AbortException {

		// 1 ---> on place un verrou sur chaque registre
		
		for(Registre<T> registre : lrst.get()) {
			if (!registre.getLock().tryLock()) {
				for(Registre<T> registre_ : lrst.get()) {
					try {
						registre_.getLock().unlock();
					}catch(IllegalMonitorStateException e) {
						//locke par un autre
					}
				}
				lrst.clearSet();
				lwst.clearSet();
				throw new AbortException();
			}
		}
		
		// 2 ---> on verifie la coherence des valeurs ecrites en comparant les dates

		for(Registre<T> registre : lrst.get()) {
			if(registre.getDate() > birthDate) {
				for(Registre<T> registre_ : lwst.get()) {
					try {
						registre_.getLock().unlock();
					}catch(IllegalMonitorStateException e) {
						//locke par un autre
					}
				}
				lrst.clearSet();
				lwst.clearSet();
				throw new AbortException();
			}
		}
		
		// 3 ---> on committe les modifications
		
		int commitDate = Clock.globalClock.getAndIncrement();
		
		for(int i=0 ; i<lwst.get().size() ; i++) {
			lwst.get().get(i).setValue(lcx.getValue());
			lwst.get().get(i).setDate(commitDate);
		}

		for(Registre<T> registre : lrst.get()) {
			try {
				registre.getLock().unlock();
			}catch(IllegalMonitorStateException e) {
				//locke par un autre
			}
		}
		lrst.clearSet();
		lwst.clearSet();
		isCommitted = true;
	}

	@Override
	public boolean isCommitted() {
		return isCommitted;
	}


	//getters setters
	public Localset<T> getWriteSet() {return lwst;}
	public Localset<T> getReadSet() {return lrst;}
	public Registre<T> getLcx(){return lcx;}
	public void setLcx(Registre<T> copy) {this.lcx = copy;}
	public int getBirthDate() {return this.birthDate;}
}
