package kimono.client.impl.tasks;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.http.HttpStatus;
import org.json.JSONArray;
import org.json.JSONObject;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import kimono.client.KCTenant;
import kimono.client.KimonoApiException;
import kimono.client.KimonoApis;
import kimono.client.impl.AbstractSupplier;
import kimono.client.impl.Credentials;
import kimono.client.tasks.KCTask;
import kimono.client.tasks.KCTaskAck;
import kimono.client.tasks.KCTaskApi;
import kimono.client.util.AuthenticationUtils;
import kong.unirest.GetRequest;
import kong.unirest.HttpRequestWithBody;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;

public class AdminTasksApiNonOAS extends AbstractSupplier<KCTask> implements KCTaskApi {

	/**
	 * When using Actor Authentication: the tenant to supply tasks for
	 */
	private KCTenant tenant;
	
	/**
	 * Are more pages available to fetch?
	 */
	private boolean morePages = true;
	
	/**
	 * Construct a {@link KCTaskApi} implementation to retrieve tasks for a specific tenant
	 * @param tenant The tenant
	 */
	public AdminTasksApiNonOAS( KCTenant tenant ) {
		this.tenant = tenant;
	}
	
	protected URL getTasksURL( Credentials cred ) throws MalformedURLException {
		return new URL(KimonoApis.getInteropDataClient(cred).getBasePath()+"/tasks/admin");
	}
	
	protected URL getTaskAckURL( Credentials cred, UUID id ) throws MalformedURLException {
		return new URL(KimonoApis.getInteropDataClient(cred).getBasePath()+"/tasks/admin/"+id+"/ack");
	}
	
	@Override
	protected List<KCTask> fetch( int page ) {

		List<KCTask> tasks = new ArrayList<>();
		
		// 100 is the maximum number of tasks we should expect from the managed
		// Tasks API, which does not let the client determine the page size. Use that 
		// here with the Admin Tasks API as well. However, keep in mind it is possible 
		// to specify this value and increase it to 2000 with the Admin Tasks API.
		int pageSize = 100;
		morePages = false;
		int status = 0;
		int retries = 0;

		do {
			try {
				// List all Tasks for the authenticated tenant via the Tasks Admin API
				Credentials cred = Credentials.forTenant(tenant);
				URL url = getTasksURL(cred);
	
				// Authenticate
				GetRequest getReq = Unirest.get(url.toString());
				if( cred.getProto() == Credentials.Proto.BASIC ) {
					getReq.basicAuth(cred.getUsername(), cred.getPassword());
				} else {
					if( cred.getAccessToken() == null ) {
						// TODO: Persist token
						JSONObject tokenInfo = AuthenticationUtils.authorize(cred);
						cred.setAccessToken(tokenInfo.getString("access_token"));
					}
					getReq.header("Authorization", "Bearer "+cred.getAccessToken());
				}
	
				// Get this page
				
				HttpResponse<JsonNode> jsonResponse = getReq.header("accept", "application/json").
					queryString("page", page).
					queryString("page_size", pageSize).
					asJson();
	
				// Convert the response to an array of KCTasks
				status = jsonResponse.getStatus();
				if( status == HttpStatus.SC_OK ) {
					JSONObject body = jsonResponse.getBody().getObject();
					if( body.has("data") ) {
						JSONArray arr = body.getJSONArray("data");
						for( int i = 0; i < arr.length(); i++ ) {
							tasks.add(new Task(arr.getJSONObject(i)));
						}
					}
					if( body.has("paging") ) {
						morePages = !body.getJSONObject("paging").isNull("next"); 
					}
				} else
				if( status != HttpStatus.SC_UNAUTHORIZED ) {
					throw new KimonoApiException(jsonResponse.getStatus()+" "+jsonResponse.getStatusText());
				}
			} catch( KimonoApiException apiEx ) {
				throw apiEx;
			} catch( Exception ex ) {
				throw new KimonoApiException(ex);
			}
		} while( status == HttpStatus.SC_UNAUTHORIZED && retries++ < 3 );

		return tasks;
	}

	@Override
	protected boolean hasMorePages() {
		return morePages;
	}

	@Override
	public void ackTask(KCTask task, KCTaskAck ack) {
		try {
			Credentials cred = Credentials.forTenant(tenant);
			URL url = getTaskAckURL(cred,task.getId());
			
			// Authenticate
			HttpRequestWithBody putReq = Unirest.put(url.toString());
			if( cred.getProto() == Credentials.Proto.BASIC ) {
				putReq.basicAuth(cred.getUsername(), cred.getPassword());
			} else {
				// TODO: OAuth2
			}
	
			Object r = putReq.header("Content-Type", "application/json").
				body(toJson(ack)).asJson();
		} catch( Exception ex ) {
			throw new KimonoApiException(ex);
		}
	}
	
	protected ObjectNode toJson( KCTaskAck ack ) {
		ObjectNode node = new ObjectMapper().createObjectNode();
		node.put("status", ( ack == null ? KCTaskAck.Status.SUCCESS : ack.getStatus() ).name().toLowerCase());
		if( ack != null ) {
			if( ack.getMessage() != null ) {
				node.put("message", ack.getMessage());
			}
			if( ack.getAppId() != null ) {
				node.put("app_id", ack.getAppId());
			}
		}
		return node;
	}
}
