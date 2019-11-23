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
	 */
	private List<T> data;
	
	@Override
	public synchronized void reset() {
		page = 0;
		data = fetch(page);
		cursor = initCursor(data);
	}
	
	@Override
	public synchronized boolean hasNext() {
		if( data == null ) {
			reset();
		}
		return hasCursor() || hasMorePages();
	}
	
	private boolean hasCursor() {
		return ( data != null && cursor >= 0 && cursor < data.size() );
	}

	private int initCursor( List<T> data ) {
		return data == null || data.isEmpty() ? -1 : 0;
	}

	@Override
	public synchronized T next() {
		if( hasCursor() ) {
			return data.get(cursor++);
		}
		if( hasMorePages() ) {
			data = fetch(++page);
			cursor = initCursor(data);
			return next();
		} 
		return null;
	}
	
	protected abstract boolean hasMorePages();
	
	protected abstract List<T> fetch( int page );
}
