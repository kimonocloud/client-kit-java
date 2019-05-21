package kimono.client.impl;

import kimono.api.v2.interop.model.TenantInfo;
import kimono.api.v2.interopdata.Configuration;
import kimono.client.KCInteropDataClientFactory;

public class DefaultInteropDataClientFactory implements KCInteropDataClientFactory {

	@Override
	public kimono.api.v2.interopdata.ApiClient authenticate(TenantInfo tenant) {
		// NOTE: Because of how we organize our APIs and how openapi-generator
		// generates code, it is important to use the correct generated classes
		// from the correct packages. This method returns an ApiClient from 
		// the kimono.api.v2.interopdata package because the caller will 
		// use this client to access the Interop Data API.
		kimono.api.v2.interopdata.ApiClient client = Configuration.getDefaultApiClient();
		
		// TODO: Actor Authentication uses OAuth
		client.setUsername(tenant.getAuth().getClientId());
		client.setPassword(tenant.getAuth().getClientSecret());
		return client;
	}
}
