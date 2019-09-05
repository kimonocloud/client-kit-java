package kimono.client.impl.tasks;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;

import kimono.client.KCTenant;
import kimono.client.KimonoApis;
import kimono.client.impl.Credentials;

public class ManagedTasksApiNonOAS extends AdminTasksApiNonOAS {

	public ManagedTasksApiNonOAS(KCTenant tenant) {
		super(tenant);
	}
	
	@Override
	protected URL getTasksURL( Credentials cred ) throws MalformedURLException {
		return new URL(KimonoApis.getInteropDataClient(cred).getBasePath()+"/tasks");
	}
	
	@Override
	protected URL getTaskAckURL( Credentials cred, UUID id ) throws MalformedURLException {
		return new URL(KimonoApis.getInteropDataClient(cred).getBasePath()+"/tasks/"+id+"/ack");
	}
}
