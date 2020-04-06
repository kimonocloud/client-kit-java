package kimono.client;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public interface KCDriverProperties {
	
	int getPollingInterval();
	
	TimeUnit getPollingIntervalTimeUnit();
	
	String getOutputFolder();
	
	/**
	 * Gets the set of account {@code id}s to process. When specified, only
	 * tenants belonging to this set of accounts are processed by the driver.
	 * @return A set of account {@code id}s or an empty set to process all available 
	 * 	tenants for the authenticated API Key
	 */
	Set<UUID> getAccountIds();
	
	/**
	 * Set a list of account {@code id}s to process. When specified, only
	 * tenants belonging to this set of accounts are processed by the driver.
	 * @param ids A comma-delimited list of {@code tenant_id}s
	 */
	void setAccountIds( String ids );
	
	/**
	 * Gets the set of {@code tenant_id}s to process. When specified, only these
	 * specific tenants are processed by the driver.
	 * @return A set of {@code tenant_id}s or an empty set to process all availble 
	 * 	tenants for the authenticated API Key
	 */
	Set<UUID> getTenantIds();
	
	/**
	 * Set a list of {@code tenant_id}s to process. When specified, only these
	 * specific tenants are processed by the driver.
	 * @param ids A comma-delimited list of {@code tenant_id}s
	 */
	void setTenantIds( String ids );
	
	/**
	 * Set a driver option
	 * @param name The option name
	 * @param value The value or null if no value
	 */
	void setOption( String name, String value );
	
	/**
	 * Determines if an option is defined
	 * @param name The option name
	 */
	boolean hasOption( String name );
	
	/**
	 * Determines if an option is defined
	 * @param name The option name
	 */
	boolean hasOption( String name, boolean defaultValue );
	
	/**
	 * Get a driver option
	 */
	String getOption( String name );
}
