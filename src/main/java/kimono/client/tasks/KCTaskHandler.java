package kimono.client.tasks;

import kimono.api.v2.interopdata.model.Task;

/**
 * Interface of an Event handler.
 * <p>
 * 
 * The handler applies the event to the application and returns a success or
 * error response. It is imperative that event handlers perform their work in a
 * timely manner; Kimono will only wait for an acknowledgement for a limited
 * period of time before considering the client to have timed out. In this case
 * any events in the batch that have not been acknowledged will be redelivered.
 * Note that because timeouts are possible it is imperative the client process
 * events idempotently. Kimono guarantees it will deliver events and deliver
 * them in order, but may deliver the same event more than once.
 *
 */
@FunctionalInterface
public interface KCTaskHandler {

	/**
	 * Handle a Task
	 * 
	 * @param task The Task
	 * 
	 * @return The response, which indicates success or failure as well as an
	 *         optional message to return to the server. When Interactive Sync is
	 *         used, the response for an Add event may also include the identifier
	 *         to assign to the {@code $sys.app_id} of the object (if not provided
	 *         synchronously, the client must eventually inform Kimono of this
	 *         identifier by calling Interactive Sync APIs)
	 */
	KCTaskHandlerResponse handle(Task task);
}
