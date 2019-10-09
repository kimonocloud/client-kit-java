package kimono.client.tasks;

import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

import kimono.client.KCTenant;
import kimono.client.KCTenantSupplier;

/**
 * Interface of an Task poller.
 */
public interface KCTaskPoller {

	/**
	 * Set the supplier of TenantInfo
	 * @param topic the topic
	 */
	void setTenantInfoSupplier( KCTenantSupplier source );
	
	/**
	 * Register an task handler
	 * @param topic the topic
	 */
	void setTaskHandler( KCTaskType type, KCTaskHandler handler );

	/**
	 * Register the default event handler to call when no task-specific handler is registered
	 * @param topic the topic
	 */
	void setDefaultTaskHandler( KCTaskHandler handler );
	
	/**
	 * Set an optional Predicate to be called for each {@link KCTenant} prior to 
	 * requesting tasks.<p>
	 * 
	 * Use a Predicate to optionally filter tenants based on application or driver
	 * state that is not known to Kimono. For example, your application may have 
	 * designated a tenant as being off-line.
	 */
	void setPredicate( Predicate<KCTenant> predicate );
	
	/**
	 * Use the managed Tasks API
	 * @param flag true to use the managed Tasks API, false to use the Tasks Admin API
	 */
	void setUseManagedTasksApi( boolean flag );

	/**
	 * Start the polling loop. Each iteration of the loop requests the next batch
	 * of Events from Kimono, the delegates to the {@link KCTaskHandler#handle(Event)}
	 * method of the registered event handler. The Event is acknowledged with the
	 * {@link KCTaskAck} returned by that method.<p>
	 * 
	 * @param interval The number of seconds to wait between polling intervals
	 */
	void poll(int interval, TimeUnit unit) throws Exception;
	
	/**
	 * Stop the polling loop
	 */
	void stop();
}
