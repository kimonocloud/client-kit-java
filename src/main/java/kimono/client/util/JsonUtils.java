package kimono.client.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;

public class JsonUtils {

	private JsonUtils() {
	}

	/**
	 * Helper to get an attribute of {@code $sys}
	 * @param json The JSON object
	 * @param key The attribute
	 * @return {@code $sys}.{@code key}
	 */
	public static String sys(JSONObject json, String key) {
		return sys(json, key, null);
	}

	public static String sys(JSONObject json, String key, String defaultValue) {
		return sub(json, "$sys", key, defaultValue);
	}

	/**
	 * Helper to get the {@code $sys.id} value of a RefType
	 * @param json The JSON object
	 * @param key The attribute
	 * @return {@code attr.$sys.id}
	 */
	public static String refId(JSONObject json, String attr) {
		if (json.has(attr)) {
			JSONObject sub = json.getJSONObject(attr);
			return sys(sub, "id");
		}
		return null;
	}

	public static String ext(JSONObject json, String key) {
		return ext(json, key, null);
	}

	public static String ext(JSONObject json, String key, String defaultValue) {
		return sub(json, "$ext", key, defaultValue);
	}

	public static String string(JSONObject json, String key) {
		return json.optString(key,null);
	}

	public static String sub(JSONObject json, String objectKey, String attrKey) {
		return sub(json, objectKey, attrKey, null);
	}

	public static String sub(JSONObject json, String objectKey, String attrKey, String defaultValue) {
		if (json.has(objectKey)) {
			JSONObject sub = json.getJSONObject(objectKey);
			return sub.optString(attrKey,defaultValue);
		}
		return defaultValue;
	}

	public static String collectRefIds(JSONObject json, String key) {
		if (!json.has(key))
			return null;

		List<String> ids = new ArrayList<>();
		JSONArray orgs = json.getJSONArray(key);
		for (int i = 0; i < orgs.length(); i++) {
			String id = JsonUtils.sys(orgs.getJSONObject(i), "id");
			ids.add(id);
		}
		return StringUtils.join(ids,",");
	}

	public static Boolean bool(JSONObject json, String key, Boolean defaultValue) {
		if( !json.isNull(key) ) {
			return Boolean.parseBoolean(json.getString(key));
		}
		return defaultValue;		
	}
}
