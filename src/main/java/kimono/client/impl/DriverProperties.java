package kimono.client.impl;

import java.util.Collections;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;

import kimono.client.KCDriverProperties;

public class DriverProperties implements KCDriverProperties {

	private static final String PROP_POLLING_INTERVAL = "polling_interval";
	
	private static final String PROP_POLLING_INTERVAL_UNITS = "polling_interval_units";

	private static final String PROP_OUTPUT_FOLDER = "output_folder";

	Properties props = new Properties();
	
	/**
	 * Set of account IDs to limit processing to
	 * @see #setAccountIds(String)
	 * @see #getAccountIds()
	 */
	Set<UUID> accountIds = new HashSet<>();
	
	/**
	 * Set of tenant IDs to limit processing to
	 * @see #setTenantIds(String)
	 * @see #getTenantIds()
	 */
	Set<UUID> tenantIds = new HashSet<>();
	
	@Override
	public int getPollingInterval() {
		return Integer.parseInt(props.getProperty(PROP_POLLING_INTERVAL, "30"));
	}
	
	@Override
	public TimeUnit getPollingIntervalTimeUnit() {
		return TimeUnit.valueOf(props.getProperty(PROP_POLLING_INTERVAL_UNITS, "SECONDS").toUpperCase());
	}
	
	@Override
	public String getOutputFolder() {
		return props.getProperty(PROP_OUTPUT_FOLDER, "output");
	}

	@Override
	public void setTenantIds(String ids) {
		for( String id : StringUtils.split(ids,",") ) {
			tenantIds.add(UUID.fromString(id));
		}
	}
	
	@Override
	public Set<UUID> getTenantIds() {
		return Collections.unmodifiableSet(tenantIds);
	}

	@Override
	public void setAccountIds(String ids) {
		for( String id : StringUtils.split(ids,",") ) {
			accountIds.add(UUID.fromString(id));
		}
	}
	
	@Override
	public Set<UUID> getAccountIds() {
		return Collections.unmodifiableSet(accountIds);
	}

	/**
	 * Apply a command-line option in the form {@code -name:value}. These options
	 * are recognized:
	 * 
	 * {@code -tenant:id1,id2...} Comma-delimited list of tenant IDs to limit 
	 * 	processing to. When specified the driver processes Tasks for only these
	 * 	tenants.
	 * 
	 * {@code -account:id1,id2,...} Comma-delimited list of account IDs to limit
	 * 	processing to. When specified the driver processes Tasks for only those
	 * 	tenants that belong to these accounts.
	 */
	@Override
	public void setOption(String name, String value) {
		if( name.equalsIgnoreCase("tenant") ) {
			setTenantIds(value);
		} else
		if( name.equalsIgnoreCase("account") ) {
			setAccountIds(value);
		} else {
			props.setProperty(name, null);
		}
	}

	@Override
	public boolean hasOption(String name) {
		return props.containsKey(name);
	}

	@Override
	public String getOption(String name) {
		return props.getProperty(name);
	}
}
