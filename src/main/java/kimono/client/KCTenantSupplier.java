package kimono.client;

import kimono.api.v2.interop.model.TenantInfo;

/**
 * Produce a list of {@link TenantInfo} for a client to iterate.
 */
public interface KCTenantSupplier extends KCSupplier<KCTenant> {

}
