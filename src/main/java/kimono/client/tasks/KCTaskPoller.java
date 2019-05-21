package kimono.client.tasks;

import java.util.concurrent.TimeUnit;

import kimono.api.v2.interop.model.TenantInfo;
import kimono.client.KCInteropDataClientFactory;
import kimono.client.KCTenantInfoSupplier;

/**
 * Interface of an Task poller.
 */
public interface KCTaskPoller {

	/**
	 * Initialize the poller
	 * @param source The supplier of {@link TenantInfo} to poll
	 * @param authenticator An authenticator to to provide an authenticated ApiClient given a TenantInfo  
	 */
	void initialize( KCTenantInfoSupplier source, KCInteropDataClientFactory authenticator );
	
	/**
	 * Register an task handler
	 * @param topic the topic
	 */
	void setTaskHandler( String topic, KCTaskHandler handler );

	/**
	 * Register the default event handler to call when no topic-specific handler is registered
	 * @param topic the topic
	 */
	void setDefaultTaskHandler( KCTaskHandler handler );

	/**
	 * Start the polling loop. Each iteration of the loop requests the next batch
	 * of Events from Kimono, the delegates to the {@link KCTaskHandler#handle(Event)}
	 * method of the registered event handler. The Event is acknowledged with the
	 * {@link KCTaskHandlerResponse} returned by that method.<p>
	 * 
	 * @param interval The number of seconds to wait between polling intervals
	 */
	void poll(int interval, TimeUnit unit) throws Exception;
	
	/**
	 * Stop the polling loop
	 */
	void stop();
}
