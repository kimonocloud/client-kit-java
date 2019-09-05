package kimono.client.impl;

import java.util.List;

import kimono.client.KCSupplier;

public abstract class AbstractSupplier<T> implements KCSupplier<T> {

	/**
	 * The current page number
	 */
	private int page = 0;
	
	/**
	 * The cursor into the current page
	 */
	private int cursor = -1;
	
	/**
	 * The current page
	 * 
	 */
	private List<T> data;
	
	@Override
	public synchronized void reset() {
		page = 0;
		data = fetch(page);
		cursor = data.isEmpty() ? -1 : 0;
	}
	
	@Override
	public synchronized boolean hasNext() {
		if( data == null ) {
			reset();
		}
		return( data != null && cursor >= 0 && cursor < data.size() ) || hasMorePages();
	}
	
	@Override
	public synchronized T next() {
		if( data != null && cursor < data.size() ) {
			return data.get(cursor++);
		}
		if( hasMorePages() ) {
			cursor = 0;
			fetch(page++);
			return next();
		} 
		return null;
	}
	
	protected abstract boolean hasMorePages();
	
	protected abstract List<T> fetch( int page );
}
