package kimono.client;

public interface KCSupplier<T> {

	/**
	 * Reset the supplier
	 */
	void reset();
	
	/**
	 * Determine if there are additional results
	 * @true if there are additional results, either in the local cache or 
	 * 	by querying the server
	 */
	boolean hasNext();
	
	/**
	 * Get the next result
	 * @return The next result or {@code null} if none
	 */
	T next();
}
