package kimono.client;

import kimono.api.v2.broker.MessagesApi;
import kimono.api.v2.interop.ActorsApi;
import kimono.api.v2.interop.CloudsApi;
import kimono.api.v2.interop.IntegrationsApi;
import kimono.api.v2.interop.TenantsApi;
import kimono.api.v2.interopdata.GradesApi;
import kimono.api.v2.interopdata.IngestionsApi;
import kimono.api.v2.interopdata.RosteringApi;
import kimono.api.v2.interopdata.TasksApi;
import kimono.api.v2.platform.AccountsApi;
import kimono.api.v2.sifcloud.ClientsApi;
import kimono.api.v2.sifcloud.ZonesApi;

/**
 * Provides static methods to easily access all Kimono APIs using the correct
 * ApiClient and type of authentication credentials.
 */
public class KimonoApis {

	private KimonoApis() {
	}

	/**
	 * Get an ApiClient to use for Interop APIs
	 * 
	 * @param cred The Account Authentication credentials to use to
	 */
	public static kimono.api.v2.interop.ApiClient getInteropClient(Credentials cred) {
		requireType(cred, Credentials.Type.ACCOUNT);
		kimono.api.v2.interop.ApiClient client = kimono.api.v2.interop.Configuration.getDefaultApiClient();
		client.setUsername(cred.getUsername());
		client.setPassword(cred.getPassword());
		return client;
	}

	/**
	 * Get an ApiClient to use for Interop Data APIs
	 * 
	 * @param cred The Actor Authentication credentials to use to
	 */
	public static kimono.api.v2.interopdata.ApiClient getInteropDataClient(Credentials cred) {
		requireType(cred, Credentials.Type.ACTOR);
		kimono.api.v2.interopdata.ApiClient client = kimono.api.v2.interopdata.Configuration.getDefaultApiClient();
		client.setUsername(cred.getUsername());
		client.setPassword(cred.getPassword());
		return client;
	}

	/**
	 * Get an ApiClient to use for with Platform APIs
	 */
	public static kimono.api.v2.platform.ApiClient getPlatformClient(Credentials cred) {
		requireType(cred, Credentials.Type.ACCOUNT);
		kimono.api.v2.platform.ApiClient client = kimono.api.v2.platform.Configuration.getDefaultApiClient();
		client.setUsername(cred.getUsername());
		client.setPassword(cred.getPassword());
		return client;
	}

	/**
	 * Get an ApiClient to use for with Broker APIs
	 */
	public static kimono.api.v2.broker.ApiClient getBrokerClient(Credentials cred) {
		requireType(cred, Credentials.Type.ACCOUNT);
		kimono.api.v2.broker.ApiClient client = kimono.api.v2.broker.Configuration.getDefaultApiClient();
		client.setUsername(cred.getUsername());
		client.setPassword(cred.getPassword());
		return client;
	}

	/**
	 * Get an ApiClient to use for with SIF Cloud APIs
	 */
	public static kimono.api.v2.sifcloud.ApiClient getSifCloudClient(Credentials cred) {
		requireType(cred, Credentials.Type.ACCOUNT);
		kimono.api.v2.sifcloud.ApiClient client = kimono.api.v2.sifcloud.Configuration.getDefaultApiClient();
		client.setUsername(cred.getUsername());
		client.setPassword(cred.getPassword());
		return client;
	}

	//
	// Platform
	//

	/**
	 * Get an AccounsApi instance
	 * 
	 * @param cred The API Key credentials to use
	 * @return
	 */
	public static AccountsApi getAccountsApi(Credentials cred) {
		return new AccountsApi(getPlatformClient(cred));
	}

	public static AccountsApi getAccountsApi() {
		return getAccountsApi(Credentials.forApiKey());
	}

	//
	// Interop
	//

	/**
	 * Get an ActorsApi instance
	 * 
	 * @param cred The API Key credentials to use
	 * @return
	 */
	public static ActorsApi getActorsApi(Credentials cred) {
		return new ActorsApi(getInteropClient(cred));
	}

	public static ActorsApi getActorsApi() {
		return getActorsApi(Credentials.forApiKey());
	}

	/**
	 * Get a CloudsApi instance
	 * 
	 * @param cred The API Key credentials to use
	 * @return
	 */
	public static CloudsApi getCloudsApi(Credentials cred) {
		return new CloudsApi(getInteropClient(cred));
	}

	public static CloudsApi getCloudsApi() {
		return getCloudsApi(Credentials.forApiKey());
	}

	/**
	 * Get an IntegrationsApi instance
	 * 
	 * @param cred The API Key credentials to use
	 * @return
	 */
	public static IntegrationsApi getIntegrationsApi(Credentials cred) {
		return new IntegrationsApi(getInteropClient(cred));
	}

	public static IntegrationsApi getIntegrationsApi() {
		return getIntegrationsApi(Credentials.forApiKey());
	}

	/**
	 * Get an TenantInfoApi instance
	 * 
	 * @param cred The API Key credentials to use
	 * @return
	 */
	public static TenantsApi getTenantsApi(Credentials cred) {
		return new TenantsApi(getInteropClient(cred));
	}

	public static TenantsApi getTenantsApi() {
		return getTenantsApi(Credentials.forApiKey());
	}

	//
	// Interop Data
	//

	/**
	 * Get an IngestionsApi instance
	 * 
	 * @param cred The actor credentials to use
	 * @return
	 */
	public static IngestionsApi getIngestionsApi(Credentials cred) {
		return new IngestionsApi(getInteropDataClient(cred));
	}

	/**
	 * Get a RosteringApi instance
	 * 
	 * @param cred The actor credentials to use
	 * @return
	 */
	public static RosteringApi getRosteringApi(Credentials cred) {
		return new RosteringApi(getInteropDataClient(cred));
	}

	/**
	 * Get a RosteringApi instance
	 * 
	 * @param cred The actor credentials to use
	 * @return
	 */
	public static GradesApi getGradesApi(Credentials cred) {
		return new GradesApi(getInteropDataClient(cred));
	}

	/**
	 * Get a TasksApi instance
	 * 
	 * @param cred The actor credentials to use
	 * @return
	 */
	public static TasksApi getTasksApi(Credentials cred) {
		return new TasksApi(getInteropDataClient(cred));
	}

	//
	// Broker
	//

	/**
	 * Get an MessagesApi instance
	 * 
	 * @param cred The API Key credentials to use
	 * @return
	 */
	public static MessagesApi getMessagesApi(Credentials cred) {
		return new MessagesApi(getBrokerClient(cred));
	}

	public static MessagesApi getMessagesApi() {
		return getMessagesApi(Credentials.forApiKey());
	}

	//
	// Grades Exchange
	//

	//
	// SIF Clouds
	//

	/**
	 * Get a ClientsApi instance
	 * 
	 * @param cred The API Key credentials to use
	 * @return
	 */
	public static ClientsApi getClientsApi(Credentials cred) {
		return new ClientsApi(getSifCloudClient(cred));
	}

	public static ClientsApi getClientsApi() {
		return getClientsApi(Credentials.forApiKey());
	}

	/**
	 * Get a ZonesApi instance
	 * 
	 * @param cred The API Key credentials to use
	 * @return
	 */
	public static ZonesApi getZonesApi(Credentials cred) {
		return new ZonesApi(getSifCloudClient(cred));
	}

	public static ZonesApi getZonesApi() {
		return getZonesApi(Credentials.forApiKey());
	}

	private static void requireType(Credentials cred, Credentials.Type type) {
		if (cred.getType() != type) {
			throw new IllegalArgumentException(
					"Requires " + (type == Credentials.Type.ACTOR ? "actor" : "account (api key)") + " credentials");
		}
	}
}