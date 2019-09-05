package kimono.client.util;

import kimono.api.v2.interop.model.TenantInfo;
import kimono.client.KCTenant;

/**
 * TenantInfo utilities
 */
public class TenantUtils {

	private static boolean sIncludeCloudName = true;
	
	private TenantUtils() { }

	public static void setIncludeCloudName(boolean opt) {
		sIncludeCloudName = opt;
	}

	/**
	 * Describe a tenant, including the name of the parent Cloud and Account
	 * 
	 * @return A string describing a tenant
	 */
	public static String describe(TenantInfo tenant) {
		if (tenant == null)
			return "<null>";
		StringBuilder str = new StringBuilder();
		str.append(tenant.getName()).append("/");
		if (sIncludeCloudName) {
			str.append(tenant.getCloud().getName()).append("/");
		}
		str.append(tenant.getAccount().getName());

		return str.toString();
	}
	
	public static String describe(KCTenant tenant) {
		return describe(tenant.getTenantInfo());
	}
}
