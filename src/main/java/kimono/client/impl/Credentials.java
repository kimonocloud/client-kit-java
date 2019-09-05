package kimono.client.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.codec.Charsets;

import kimono.api.v2.interop.model.TenantInfo;
import kimono.client.KCTenant;

/**
 * Encapsulates actor or account ("api key") credentials to connect to a Kimono API.
 */
public class Credentials {

	public enum Type { ACCOUNT, ACTOR }
	public enum Proto { OAUTH2, BASIC }
	
	private String fUsername;
	private String fPassword;
	private String fToken;
	private Type fType;
	private Proto fProto;
	
	private static Map<Type,Proto> sDefaultProto = new HashMap<>();
	static {
		sDefaultProto.put(Type.ACCOUNT,getGlobalProtocolForType(Type.ACCOUNT,Proto.BASIC));
		sDefaultProto.put(Type.ACTOR,getGlobalProtocolForType(Type.ACTOR,Proto.OAUTH2));
	}
	
	public Credentials( Type type, Proto proto, String username, String password ) {
		fType = type;
		fProto = proto;
		fUsername = username;
		fPassword = password;
	}

	public Credentials( Type type, String username, String password ) {
		this(type,sDefaultProto.get(type),username,password);
	}
	
	/**
	 * Determine whether to use HTTP Basic Auth or OAuth2 for Actor Authentication.
	 * Some Kimono environments require HTTP Basic Auth. If the {@code KIMONO_ACTOR_AUTH}
	 * property is set, use that value (can be set to "basic" or "oauth2"). Otherwise,
	 * read the value from the ~/.kimono/api.properties file if it exists or
	 * default to OAuth2 if not.
	 */
	public static Proto getGlobalProtocolForType( Type type, Proto defaultProto ) {
		String proto = System.getenv("KIMONO_API_AUTH_"+type.name());
		if( proto == null ) {
			File f = new File("~/.kimono/api.properties");
			if( f.exists() ) {
				Properties p = new Properties();
				try(InputStream in = new FileInputStream(f) ) {
					p.load(in);
				} catch( Exception ex ) {
					System.err.println("Error reading "+f.getAbsolutePath());
					ex.printStackTrace();
				}
				proto = p.getProperty("auth_"+type.name().toLowerCase(), defaultProto.name());
			}
		}
		
		if( proto != null ) {
			try {
				return Proto.valueOf(proto);
			} catch( Exception ex ) {
				System.err.println("Invalid global actor authentication type specified in KIMONO_ACTOR_AUTH or in ~/.kimono/api.properties: "+proto);
			}
		}
		
		return defaultProto;
	}

	/**
	 * Construct Actor Authentication credentials for a tenant
	 * @param tenant The tenant
	 * @return Credentials to connect to APIs that use Actor Authentication
	 */
	public static Credentials forTenant( TenantInfo tenant ) {
		return new Credentials(Type.ACTOR,
				Proto.valueOf(tenant.getAuth().getType().toUpperCase()),
				tenant.getAuth().getClientId(),
				tenant.getAuth().getClientSecret());
	}

	/**
	 * Construct Actor Authentication credentials for a tenant
	 * @param tenant The tenant
	 * @return Credentials to connect to APIs that use Actor Authentication
	 */
	public static Credentials forTenant( KCTenant tenant ) {
		return forTenant(tenant.getTenantInfo());
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
	
	public Proto getProto() {
		return fProto;
	}
	
	public String getAccessToken() {
		return fToken;
	}
	
	public void setAccessToken( String token ) {
		fToken = token;
	}
}
