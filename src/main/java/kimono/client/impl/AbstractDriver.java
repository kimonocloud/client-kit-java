package kimono.client.impl;

import org.apache.commons.lang3.StringUtils;

import kimono.client.KCDriverInfo;
import kimono.client.KCDriverProperties;
import kimono.client.impl.tasks.TaskPoller;
import kimono.client.tasks.KCTaskPoller;
import kong.unirest.JacksonObjectMapper;
import kong.unirest.Unirest;

/**
 * A Kimono Integration driver
 */
public abstract class AbstractDriver {

	private KCDriverProperties props;
	private KCDriverInfo driverInfo;
	
	public AbstractDriver() {
		super();
		props = newProperties();
		driverInfo = newDriverInfo();
		
		// Unirest is temporarily used for some API calls, configure it to use Jackson
		Unirest.config().setObjectMapper(new JacksonObjectMapper());
	}

	/**
	 * Create a new {@link KCDriverProperties} instance. Override this method
	 * to customize how properties are obtained from your environment.
	 * @return
	 */
	protected KCDriverProperties newProperties() {
		return new DriverProperties();
	}
	
	/**
	 * Get driver properties
	 * @return
	 */
	public KCDriverProperties getProperties() {
		return props;
	}
	
	/**
	 * Run the driver task loop indefinitely
	 * @throws Exception
	 */
	public void run() throws Exception {
		
		
		KCTaskPoller poller = new TaskPoller(new TenantSupplier().
				forTenants(props.getTenantIds()).
				forAccounts(props.getAccountIds()).
				forIntegrations(driverInfo.getName()));
		
		// Let the concrete class set up task handlers
		configureTaskHandlers(poller);

		// Start the task loop
		poller.poll(props.getPollingInterval(),props.getPollingIntervalTimeUnit());
	}
	
	/**
	 * Called to configure task handlers
	 * @param poller The task poller
	 */
	protected abstract void configureTaskHandlers( KCTaskPoller poller );

	/**
	 * Called to create a {@link KCDriverInfo} describing this driver
	 */
	protected abstract KCDriverInfo newDriverInfo();
	
	/**
	 * Apply any options specified on the command-line or environment
	 */
	public AbstractDriver parseCommandLine( String[] args ) {
		for( int i = 0; i < args.length; i++ ) {
			if( args[i].charAt(0) == '-' ) {
				String[] parts = StringUtils.split(args[i].substring(1),':');
				applyCommandLineOption(parts[0], parts.length == 2 ? parts[1] : null);
			} else {
				applyCommandLineOption(args[i], null);
			}
		}
		return this;
	}
	
	/**
	 * Apply a command-line option in the form {@code -name:value}
	 * @param name The option name
	 * @param value The option value
	 */
	protected void applyCommandLineOption( String name, String value ) {
		props.setOption(name, value);
	}
}


