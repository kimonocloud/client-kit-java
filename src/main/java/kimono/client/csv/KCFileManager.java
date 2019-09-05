package kimono.client.csv;

import java.io.File;
import java.io.IOException;

import kimono.client.KCDriverProperties;
import kimono.client.KCTenant;
import kimono.client.KCTopic;
import kimono.client.tasks.KCTask;

/**
 * Manages CSV files on behalf of a tenant. A File Manager instance manages
 * files for only one tenant and only one set of files at a time.
 * 
 * Kimono bookends groups of Data Event tasks with Sync Start and Sync End
 * markers. The Sync Start identifies a {@code group_id} in the form
 * "group:uuid", where "group" is the name of a dependency group. A dependency
 * group may contain multiple topics that are dependent on groups already
 * received. When the Sync End marker is received, it signals the end of data in
 * this dependency group.
 * <p>
 * 
 * At some point the File Manager must commit the files it has received, usually
 * by sending them to an API. The Sync End marker is used to determine when to
 * do this. Because all dependent data in a group has been received at the time
 * of the Sync End, clients may be able to commit the files at this time without
 * waiting for more data. Alternatively, a client can wait for the last
 * dependency group (e.g. Classes) to perform this step. In either case call
 * {@link #commitFiles()} to commit files received thus far.
 * <p>
 * 
 * {@link #commitFiles()} copies files to an archive folder. The archive folder 
 * is useful to see the last set of files that were committed.
 */
public interface KCFileManager {

	@FunctionalInterface
	public interface Supplier {
		KCFile get(String groupId, KCTopic topic);
	}

	/**
	 * Get the tenant
	 */
	KCTenant getTenant();

	/**
	 * Get the driver properties
	 */
	KCDriverProperties getProperties();

	/**
	 * Get the folder where files are located
	 */
	File getFolder();

	/**
	 * Close the File Manager
	 */
	void close() throws IOException;

	/**
	 * Start receiving data for a group of topics
	 */
	KCFile startGroup(KCTask task) throws IOException;

	/**
	 * Write a row of data to a file
	 */
	void write(KCTask task) throws IOException;

	/**
	 * Stop receiving data for a group of topics
	 */
	void endGroup(KCTask task);

	/**
	 * Commit all files and move them to an archive directory
	 */
	void commitFiles();

}
