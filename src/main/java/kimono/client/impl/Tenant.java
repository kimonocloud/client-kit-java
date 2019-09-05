package kimono.client.impl;

import kimono.api.v2.interop.model.TenantInfo;
import kimono.client.KCTenant;
import kimono.client.util.TenantUtils;

public class Tenant implements KCTenant {

	private TenantInfo tenantInfo;
	
	public Tenant( TenantInfo ti ) {
		tenantInfo = ti;
	}

	@Override
	public TenantInfo getTenantInfo() {
		return tenantInfo;
	}
	
	@Override
	public String toString() {
		return TenantUtils.describe(getTenantInfo());
	}
}
