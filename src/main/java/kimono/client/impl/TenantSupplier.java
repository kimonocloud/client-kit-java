package kimono.client.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import kimono.api.v2.interop.ApiException;
import kimono.api.v2.interop.model.TenantInfo;
import kimono.api.v2.interop.model.TenantInfosResponse;
import kimono.client.KCTenant;
import kimono.client.KCTenantSupplier;
import kimono.client.KimonoApiException;
import kimono.client.KimonoApis;

/**
 * Default {@link KCTenantSupplier} implementation supplies
 * {@link TenantInfo} instances by calling the Kimono Tenants API, optionally
 * limited to specific Accounts, Integrations, or Tenants.
 */
public class TenantSupplier extends AbstractSupplier<KCTenant> implements KCTenantSupplier {

	/**
	 * The UUIDs of Accounts to limit results to.
	 */
	private Set<UUID> forAccounts = new HashSet<>();

	/**
	 * The names of the Integrations to limit results to.
	 */
	private Set<String> forIntegrations = new HashSet<>();
	
	/**
	 * Specific tenants to return
	 */
	private Set<UUID> forTenantIds = new HashSet<>();

	/**
	 * Are more pages available after this page?
	 */
	private boolean morePages;
	
	/**
	 * Return only tenants of specific Accounts
	 */
	public KCTenantSupplier forAccounts(UUID... accountIds) {
		forAccounts.addAll(Arrays.asList(accountIds));
		return this;
	}

	/**
	 * Return only tenants of specific Integrations
	 */
	public KCTenantSupplier forIntegrations(String... names) {
		forIntegrations.addAll(Arrays.asList(names));
		return this;
	}

	/**
	 * Return only specified tenants
	 */
	public KCTenantSupplier forTenants(UUID... ids) {
		forTenantIds.addAll(Arrays.asList(ids));
		return this;
	}

	@Override
	protected List<KCTenant> fetch( int p ) {
		try {
			// Convert Set to List
			List<String> integrations = forIntegrations.stream().collect(Collectors.toList());
			
			// Ask for tenants matching account ID and Integration filters
			TenantInfosResponse rsp = KimonoApis.getTenantsApi(Credentials.forApiKey()).
					listInteropTenants(null,integrations,p,null);
			morePages = rsp.getPaging().getNext() != null;
			
			// Convert to a list of KCTenant
			final List<KCTenant> tenants = new ArrayList<>();
			rsp.getData().forEach(ti->tenants.add(wrap(ti)));
			
			// TODO: The integrations filter is not being respected, so 
			// this is a temporary bandaid to perform the filtering on the 
			// client.
			if( !integrations.isEmpty() ) {
				return tenants.stream().
						filter(t->integrations.contains(t.getTenantInfo().getIntegration().getName())).
						collect(Collectors.toList());
			}
			
			return tenants;
		} catch (ApiException ex) {
			throw new KimonoApiException(ex);
		}
	}
	
	/**
	 * Create a {@link KCTenant} implementation to wrap a {@link TenantInfo}
	 */
	protected KCTenant wrap( TenantInfo tenantInfo ) {
		return new Tenant(tenantInfo);
	}

	@Override
	protected boolean hasMorePages() {
		return morePages;
	}
}
