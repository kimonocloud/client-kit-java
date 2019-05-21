package kimono.client.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import kimono.api.v2.interop.ApiClient;
import kimono.api.v2.interop.ApiException;
import kimono.api.v2.interop.TenantsApi;
import kimono.api.v2.interop.model.TenantInfo;
import kimono.client.KCTenantInfoSupplier;
import kimono.client.KimonoApiException;

/**
 * Default {@link KCTenantInfoSupplier} implementation gets {@link TenantInfo}
 * instances from the Tenants API, optionally limited to specific Integrations. 
 * Alternatively, you can specify a specific actor ID to return 
 * {@link TenantInfo} for that actor only.
 */
public class DefaultTenantInfoSupplier implements KCTenantInfoSupplier {

	/**
	 * The Tenants API
	 */
	private TenantsApi api;
	
	/**
	 * The UUID of an actor when configured to return TenantInfo for a specific 
	 * actor. Otherwise, returns TenantInfo for all actors of all Integrations
	 * identified by {@link #integrations}
	 */
	private UUID actorId;
	
	/**
	 * A list of Integration names to limit tenants to.
	 * @see #setIntegrations(Collection)
	 */
	private List<String> integrations;
	
	/**
	 * Constructor
	 * @param client The ApiClient to use to call the Tenants API. The
	 * 	Tenants API uses API Key authentication rather than Actor Authentication,
	 * 	so you must supply an ApiClient that is configured with an API Key
	 * 	for Http Basic Authentication
	 */
	public DefaultTenantInfoSupplier( ApiClient client ) {
		this(client,Collections.emptySet(),null);
	}
	
	/**
	 * Constructor
	 * @param client The ApiClient to use to call the Tenants API. The
	 * 	Tenants API uses API Key authentication rather than Actor Authentication,
	 * 	so you must supply an ApiClient that is configured with an API Key
	 * 	for Http Basic Authentication
	 * @param integrations Optional set of Integration names to limit the response to.
	 * 	See {@link #setIntegrations(Collection)}
	 * @param actorId Optional actor ID to limit the response to
	 */
	public DefaultTenantInfoSupplier( ApiClient client, Set<String> integrations, UUID actorId ) {
		this.api = new TenantsApi(client);
		this.actorId = actorId;
		setIntegrations(integrations);
	}
	
	/**
	 * A list of Integration names to limit tenants to. If you have a single
	 * client that processes data on behalf of multiple Integrations, either
	 * representing different products or variants of the same Integration,
	 * use the form of constructor that accepts 
	 */
	public void setIntegrations( Collection<String> integrations ) {
		// Convert to a List as required by Swagger
		if( integrations != null ) {
			this.integrations = integrations.stream().collect(Collectors.toList());
		} else {
			this.integrations = Collections.emptyList();
		}
	}
	
	@Override
	public List<TenantInfo> get() throws KimonoApiException {
		try {
			// TODO: Support specifying a single actorId
			return api.listInteropTenants(integrations);
		} catch( ApiException ex ) {
			throw new KimonoApiException(ex);
		}
	}
}
