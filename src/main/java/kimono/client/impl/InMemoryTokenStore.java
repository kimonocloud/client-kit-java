package kimono.client.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import kimono.client.KCTokenStore;
import kimono.client.KCTokenType;

/**
 * A {@link KCTokenStore} implementation that records tokens in memory
 */
public class InMemoryTokenStore implements KCTokenStore {

	private Map<UUID,String> tokens = new HashMap<>();
	
	@Override
	public String getToken(UUID tenantId, KCTokenType type) {
		return tokens.get(tenantId);
	}

	@Override
	public void setToken(UUID tenantId, KCTokenType type, String value) {
		tokens.put(tenantId, value);
	}

	@Override
	public void clearToken(UUID tenantId, KCTokenType type) {
		tokens.remove(tenantId);
	}
}