package kimono.client.impl.tasks;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import kimono.api.v2.interop.model.TenantInfo;
import kimono.api.v2.interopdata.TasksApi;
import kimono.api.v2.interopdata.model.ErrorType;
import kimono.api.v2.interopdata.model.Task;
import kimono.api.v2.interopdata.model.Task.ActionEnum;
import kimono.api.v2.interopdata.model.TaskAck;
import kimono.api.v2.interopdata.model.TaskAck.StatusEnum;
import kimono.client.KCInteropDataClientFactory;
import kimono.client.KCTenantInfoSupplier;
import kimono.client.KimonoApiException;
import kimono.client.tasks.KCTaskHandler;
import kimono.client.tasks.KCTaskHandlerResponse;
import kimono.client.tasks.KCTaskPoller;
import kimono.client.util.TenantUtils;

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
	 * Default IEventHandler
	 */
	private KCTaskHandler defaultHandler;

	/**
	 * IEventHandler for each topic
	 */
	private Map<String, KCTaskHandler> handlers = new HashMap<>();

	/**
	 * Identifies one or more tenants to poll for events
	 */
	private KCTenantInfoSupplier tenantInfoSupplier;

	/**
	 * Returns an authenticated ApiClient given a tenant
	 */
	private KCInteropDataClientFactory authenticator;

	/**
	 * Register an event handler
	 * 
	 * @param topic
	 *            the topic
	 */
	@Override
	public void setTaskHandler(String topic, KCTaskHandler handler) {
		handlers.put(topic, handler);
	}

	@Override
	public void setDefaultTaskHandler(KCTaskHandler handler) {
		defaultHandler = handler;
	}

	@Override
	public void initialize(KCTenantInfoSupplier supplier, KCInteropDataClientFactory auth) {
		if (supplier == null) {
			throw new IllegalArgumentException("TenantInfoSupplier is required");
		}
		tenantInfoSupplier = supplier;

		if (auth == null) {
			throw new IllegalArgumentException("ActorAuthenticator is required");
		}
		authenticator = auth;
	}

	/**
	 * Start the polling loop. Each iteration of the loop requests the next
	 * batch of Events from Kimono for each Integration tenant. Each Event is
	 * delegated to the {@link KCTaskHandler#handle(Event)} method of the
	 * registered event handler. The Event is acknowledged with the
	 * {@link KCTaskHandlerResponse} returned by that method.
	 * <p>
	 * 
	 * @param interval
	 *            The number of seconds to wait between polling intervals
	 */
	@Override
	public synchronized void poll(int interval, TimeUnit unit) throws Exception {
		if (tenantInfoSupplier == null) {
			throw new IllegalStateException("Poller has not been initialized");
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
	 * Poll each Kimono Integration tenant supplied by the ITenantInfoSupplier
	 * for pending events.
	 * 
	 * @throws ApiException
	 *             if a non-retryable error is encountered connecting to Kimono
	 */
	protected void pollTenants() throws KimonoApiException {
		Collection<TenantInfo> tenants;
		try {
			tenants = tenantInfoSupplier.get();
		} catch (Exception ex) {
			throw new KimonoApiException("Error getting tenant info",ex); // TODO: Determine if retryable
		}

		if (tenants != null) {
			for (TenantInfo tenant : tenants) {
				TasksApi api = getTasksApi(tenant);
				if (api != null) {
					getTasks(api, tenant);
				}
			}
		}
	}

	protected TasksApi getTasksApi(TenantInfo tenant) {
		return new TasksApi(authenticator.authenticate(tenant));
	}

	protected void getTasks(TasksApi api, TenantInfo tenant) {
		try {
			LOGGER.log(Level.INFO, "Polling: {0}", TenantUtils.describe(tenant));
			List<Task> tasks = api.listTasks();
			LOGGER.log(Level.INFO, "Received {0} tasks", tasks.size());
			for (Task task : tasks ) {
				ActionEnum action = task.getAction();
				String batch = task.getBatchId();
				String topic = task.getTopic();
				UUID id = task.getId();
				Object data = task.getData();
				KCTaskHandler handler = handlers.get(topic);
				if (handler == null) {
					handler = defaultHandler;
				}

				KCTaskHandlerResponse rsp = handler.handle(task);

				TaskAck ack = new TaskAck();
				ack.setMessage("It did not worked");
				ack.setStatus(StatusEnum.ERROR);
				
				ErrorType error = new ErrorType();
				error.setCode("APP101");
				error.setMessage("Could not update record");
				error.setExtMessage("Student: Eric Petersen");
				error.setDetails("id=42");
				ack.setError(error);
				
				Map<String, String> params = new HashMap<>();
				params.put("$sys.app_id", "42");
				ack.setParams(params);

				api.acknowledgeTask(id, ack);
			}
		} catch (Exception ex) {
			LOGGER.log(Level.SEVERE, "Unexpected API error", ex);
		}
	}
}
