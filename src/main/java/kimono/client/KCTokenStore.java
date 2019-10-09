package kimono.client;

import java.util.UUID;

/**
 * Store tokens on behalf of a tenant 
 */
public interface KCTokenStore {

	/**
	 * Get a token for a tenant
	 */
	String getToken( UUID tenantId, KCTokenType type );
	
	/**
	 * Set a token for a tenant
	 */
	void setToken( UUID tenanId, KCTokenType type, String value );
	
	/**
	 * Clear a token for a tenant
	 */
	void clearToken( UUID tenantId, KCTokenType type );
}
