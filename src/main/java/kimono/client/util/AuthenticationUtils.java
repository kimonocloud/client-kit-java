package kimono.client.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.StringWriter;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import org.json.JSONObject;

import kimono.client.impl.Credentials;

public class AuthenticationUtils {

	private AuthenticationUtils() { }
	
	/**
	 * Call the Kimono oauth/token to obtain a token via the Client Credentials flow 
	 * @param clientId The actor Client ID
	 * @param clientSecret The actor Client Secret
	 * @return A JSON object comprised of three keys: {@code token}, {@code expires}, {@code blah}
	 * @throws IOException
	 */
	public static JSONObject authorize( Credentials cred ) throws IOException {
		String content = "client_id="+cred.getUsername()+"&client_secret="+cred.getPassword()+"&grant_type=client_credentials";
		BufferedReader reader = null;
		    HttpsURLConnection connection = null;
		    try {
		        URL url = new URL("https://api.us2.kimonocloud.com/oauth/token");
		        connection = (HttpsURLConnection) url.openConnection();
		        connection.setRequestMethod("POST");
		        connection.setDoOutput(true);
//		        connection.setRequestProperty("Authorization", "Basic " + authentication);
		        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		        connection.setRequestProperty("Accept", "application/json");
		        PrintStream os = new PrintStream(connection.getOutputStream());
		        os.print(content);
		        os.close();
		        reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		        String line = null;
		        StringWriter out = new StringWriter(connection.getContentLength() > 0 ? connection.getContentLength() : 2048);
		        while ((line = reader.readLine()) != null) {
		            out.append(line);
		        }
		        
		        return new JSONObject(out.toString());
		    } finally {
		        if (reader != null) {
		            try {
		                reader.close();
		            } catch (IOException e) {
		            }
		        }
		        connection.disconnect();
		    }
		}}
