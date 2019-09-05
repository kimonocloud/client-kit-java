package kimono.client;

public interface KCSupplier<T> {

	/**
	 * Reset the supplier
	 */
	void reset();
	
	/**
	 * Determine if there are additional results
	 */
	boolean hasNext();
	
	/**
	 * Get the next result
	 */
	T next();
}
