package kimono.client.impl;

import java.util.Properties;
import java.util.concurrent.TimeUnit;

import kimono.client.KCDriverProperties;

public class DriverProperties implements KCDriverProperties {

	private static final String PROP_POLLING_INTERVAL = "polling_interval";
	
	private static final String PROP_POLLING_INTERVAL_UNITS = "polling_interval_units";

	private static final String PROP_OUTPUT_FOLDER = "output_folder";
	
	Properties props = new Properties();
	
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
}
