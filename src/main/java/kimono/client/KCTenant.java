package kimono.client;

import kimono.api.v2.interop.model.TenantInfo;

/**
 * Encapsulates a Kimono Integration tenant.
 */
public interface KCTenant {

	TenantInfo getTenantInfo();
	
}
