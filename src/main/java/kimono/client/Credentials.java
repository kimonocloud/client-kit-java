package kimono.client;

import java.util.Base64;

import org.apache.commons.codec.Charsets;

import kimono.api.v2.interop.model.TenantInfo;

/**
 * Encapsulates actor or account ("api key") credentials to connect to a Kimono API.
 */
public class Credentials {

	public enum Type { ACCOUNT, ACTOR }
	
	private String fUsername;
	private String fPassword;
	private Type fType;
	
	public Credentials( Type type, String username, String password ) {
		fType = type;
		fUsername = username;
		fPassword = password;
	}

	/**
	 * Construct Actor Authentication credentials for a tenant
	 * @param tenant The tenant
	 * @return Credentials to connect to APIs that use Actor Authentication
	 */
	public static Credentials forTenant( TenantInfo tenant ) {
		return new Credentials(Type.ACTOR,tenant.getAuth().getClientId(),tenant.getAuth().getClientSecret());
	}
	
	/**
	 * Construct Account Authentication credentials for an API Key
	 * @param apiKey The API Key
	 * @return Credentials to connect to APIs that use Account Authentication
	 */
	public static Credentials forAccount( String apiKey ) {
		return new Credentials(Type.ACCOUNT,apiKey,apiKey);
	}
	
	/**
	 * Construct Account Authentication credentials for the API Key defined by 
	 * the {@code KIMONO_API_KEY} environment variable
	 * @return Credentials to connect to APIs that use Account Authentication
	 */
	public static Credentials forApiKey() {
		String apiKey = System.getenv("KIMONO_API_KEY");
		if( apiKey == null ) {
			throw new IllegalStateException("KIMONO_API_KEY not set");
		}
		return new Credentials(Type.ACCOUNT,apiKey,apiKey);
	}

	/**
	 * Encode credentials for HTTP Basic Authentication
	 * @return The string "Basic {username:password}", where the content in braces is Base64 encoded 
	 */
	public String encode() {
		String basic = getUsername()+":"+getPassword();
		return "Basic "+Base64.getEncoder().encodeToString(basic.getBytes( Charsets.ISO_8859_1 ) );
	}
	
	public String getUsername() {
		return fUsername;
	}
	
	public String getPassword() {
		return fPassword;
	}
	
	public Type getType() {
		return fType;
	}
}
