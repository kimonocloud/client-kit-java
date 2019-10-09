package kimono.client.impl.tasks;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang3.ObjectUtils;

import kimono.client.KCTenant;
import kimono.client.KCTenantSupplier;
import kimono.client.tasks.KCTask;
import kimono.client.tasks.KCTaskAck;
import kimono.client.tasks.KCTaskApi;
import kimono.client.tasks.KCTaskHandler;
import kimono.client.tasks.KCTaskPoller;
import kimono.client.tasks.KCTaskType;

/**
 * Sample implementation of an Event poller.
 * 
 * Call {@link #setTaskHandler(String, KCTaskHandler)} to register
 * {@link KCTaskHandler} implementations for each topic, or
 * {@link #setDefaultTaskHandler(KCTaskHandler)} to register a default handler
 * to be called when no topic-specific handler is registered. The handler is
 * responsible for applying client-specific logic and returning the results in a
 * {@link KCTaskHandlerResponse}.
 * 
 * To start the polling loop call {@link #start()}.
 */
public class TaskPoller implements KCTaskPoller {

	private static final Logger LOGGER = Logger.getLogger(TaskPoller.class.getName());

	/**
	 * Flag to stop the loop
	 */
	private boolean stop;

	/**
	 * Use the managed Tasks API? When true Kimono will manage the delivery of tasks
	 * to clients. This is best for production but is difficult to use during
	 * development. When false, the Tasks Admin API is used. This is an unmanaged
	 * API that does not inherently support multiple clients but is very easy to use
	 * during development and can also be used in production as long as a single
	 * client app is run. If you need to scale up multiple clients, use the managed
	 * Tasks API or provide a more sophisticated TaskPoller implementation that can
	 * coordinate work among your clients.
	 */
	private boolean useManagedTasksApi = false;

	/**
	 * Default Task Handler
	 */
	private KCTaskHandler defaultHandler;

	/**
	 * Optional Predicate to be called for each {@link KCTenant} prior to requesting
	 * tasks
	 */
	private Predicate<KCTenant> predicate;

	/**
	 * Task Handlers by type
	 */
	private Map<KCTaskType, KCTaskHandler> handlers = new HashMap<>();

	/**
	 * Identifies one or more tenants to poll for events
	 */
	private KCTenantSupplier tenantSupplier;

	public TaskPoller() {
		super();
	}

	public TaskPoller(KCTenantSupplier supplier) {
		setTenantInfoSupplier(supplier);
	}

	/**
	 * Register an event handler
	 * 
	 * @param topic the topic
	 */
	@Override
	public void setTaskHandler(KCTaskType type, KCTaskHandler handler) {
		handlers.put(type, handler);
	}

	@Override
	public void setDefaultTaskHandler(KCTaskHandler handler) {
		defaultHandler = handler;
	}

	@Override
	public void setTenantInfoSupplier(KCTenantSupplier supplier) {
		tenantSupplier = supplier;
	}

	@Override
	public void setUseManagedTasksApi(boolean flag) {
		useManagedTasksApi = flag;
	}

	/**
	 * Start the polling loop. Each iteration of the loop requests the next batch of
	 * Events from Kimono for each Integration tenant. Each Event is delegated to
	 * the {@link KCTaskHandler#handle(Event)} method of the registered event
	 * handler. The Event is acknowledged with the {@link KCTaskHandlerResponse}
	 * returned by that method.
	 * <p>
	 * 
	 * @param interval The number of seconds to wait between polling intervals
	 */
	@Override
	public synchronized void poll(int interval, TimeUnit unit) throws Exception {
		if (tenantSupplier == null) {
			throw new IllegalStateException("No supplier of TenantInfo");
		}
		do {
			// Iterator all tenants...
			long ms = System.currentTimeMillis();
			pollTenants();

			// Sleep if there is any time remaining in this polling interval
			long delay = unit.toMillis(interval) - (System.currentTimeMillis() - ms);
			if (delay > 0) {
				LOGGER.log(Level.INFO, "Next request in {0}ms", delay);
				TimeUnit.MILLISECONDS.sleep(delay);
			}
		} while (!stop);
	}

	/**
	 * Stop the polling loop
	 */
	@Override
	public synchronized void stop() {
		stop = true;
	}

	/**
	 * Poll the next page of Tasks for each tenant
	 */
	protected void pollTenants() {
		tenantSupplier.reset();
		while (tenantSupplier.hasNext()) {
			KCTenant tenant = tenantSupplier.next();

			// If a predicate is specified it must approve the tenant
			if (predicate == null || predicate.test(tenant)) {
				KCTaskApi tasks = newTaskApi(tenant);
				while (tasks.hasNext()) {
					KCTask task = tasks.next();
					KCTaskAck ack = delegateTask(tenant, task);
					tasks.ackTask(task, ack);
				}
			}
		}
	}

	protected KCTaskApi newTaskApi(KCTenant tenant) {
		if (useManagedTasksApi) {
			return new AdminTasksApiNonOAS(tenant);
		} else {
			return new AdminTasksApiNonOAS(tenant);
		}
	}

	protected KCTaskAck delegateTask(KCTenant tenant, KCTask tsk) {
		KCTaskHandler handler = ObjectUtils.firstNonNull(handlers.get(tsk.getType()), defaultHandler);
		if (handler != null) {
			try {
				return handler.handle(tenant, tsk);
			} catch( Exception ex ) {
				// Uncaught exception must ack the task (as an error) to avoid deadlock
				return TaskAck.error("Unexpected error",ex);
			}
		}
		
		return TaskAck.success();
	}

	@Override
	public void setPredicate(Predicate<KCTenant> predicate) {
		this.predicate = predicate;
	}
}
