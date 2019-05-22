package kimono.client.impl;

import java.util.Collections;
import java.util.HashSet;
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
 * Default {@link KCTenantInfoSupplier} implementation supplies
 * {@link TenantInfo} instances by calling the Kimono Tenants API, optionally
 * limited to specific Integrations by name or specific actors by Actor ID.
 */
public class DefaultTenantInfoSupplier implements KCTenantInfoSupplier {

	/**
	 * The Tenants API
	 */
	private TenantsApi api;

	/**
	 * The UUIDs of the actors to limit results to.
	 * 
	 * @see #withName(String...)
	 */
	private Set<UUID> filterByActorId = new HashSet<>();

	/**
	 * The names of the Integrations to limit results to.
	 * 
	 * @see #withActorId(UUID...)
	 */
	private Set<String> filterByName = new HashSet<>();

	/**
	 * Constructor
	 * 
	 * @param client The ApiClient to use to call the Tenants API. The Tenants API
	 *               uses API Key authentication rather than Actor Authentication,
	 *               so you must supply an ApiClient that is configured with an API
	 *               Key for Http Basic Authentication
	 */
	public DefaultTenantInfoSupplier(ApiClient client) {
		api = new TenantsApi(client);
	}

	/**
	 * Return only tenants of a specific Integration or Integrations.
	 * 
	 * @param name The name of the Integration as specified in the Integration
	 *             Blueprint
	 */
	public KCTenantInfoSupplier withName(String... names) {
		for (String name : names) {
			filterByName.add(name);
		}
		return this;
	}

	/**
	 * Return only tenants having specific Actor Ids
	 * 
	 * @param name One or more Actor Ids
	 */
	public KCTenantInfoSupplier withActorId(UUID... ids) {
		for (UUID id : ids) {
			filterByActorId.add(id);
		}
		return this;
	}

	@Override
	public List<TenantInfo> get() throws KimonoApiException {
		try {
			if (!filterByActorId.isEmpty()) {
				return findByIds();
			} else {
				List<String> names = filterByName.stream().collect(Collectors.toList());
				return api.listInteropTenants(names);
			}
		} catch (ApiException ex) {
			throw new KimonoApiException(ex);
		}
	}

	protected List<TenantInfo> findByIds() throws KimonoApiException {
		// TODO: Not yet implemented
		return Collections.emptyList();
	}
}
