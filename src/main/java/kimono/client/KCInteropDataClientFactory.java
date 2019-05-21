package kimono.client;

import kimono.api.v2.interop.model.TenantInfo;

/**
 * Create an {@link kimono.api.v2.interopdata.ApiClient} for a {@link TenantInfo}
 */
@FunctionalInterface
public interface KCInteropDataClientFactory {

	/**
	 * Return an authenticated ApiClient for a given tenant
	 * @param tenant The tenant
	 * @return The kimono.api.v2.interopdata.ApiClient
	 */
	kimono.api.v2.interopdata.ApiClient authenticate( TenantInfo tenant );
}
