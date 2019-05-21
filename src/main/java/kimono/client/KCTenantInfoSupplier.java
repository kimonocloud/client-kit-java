package kimono.client;

import java.util.List;

import kimono.api.v2.interop.model.TenantInfo;

/**
 * Produce a list of {@link TenantInfo} for the client to iterate.
 */
@FunctionalInterface
public interface KCTenantInfoSupplier {

	/**
	 * Produce a list of {@link TenantInfo} for the client to iterate.
	 * @return A list of tenant info
	 * @throws KimonoApiException
	 */
	List<TenantInfo> get() throws KimonoApiException;
}
