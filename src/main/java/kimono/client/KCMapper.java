package kimono.client;

import org.json.JSONObject;

import kimono.client.tasks.KCTask;

/**
 * Transform source attributes to destination attributes 
 */
@FunctionalInterface
public interface KCMapper {

	JSONObject map( KCTask task, JSONObject attrs );
	
}
