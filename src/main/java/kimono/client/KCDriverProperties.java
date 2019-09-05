package kimono.client;

import java.util.concurrent.TimeUnit;

public interface KCDriverProperties {
	
	int getPollingInterval();
	
	TimeUnit getPollingIntervalTimeUnit();
	
	String getOutputFolder();
}
