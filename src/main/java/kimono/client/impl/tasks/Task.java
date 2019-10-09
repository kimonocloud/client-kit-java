package kimono.client.impl.tasks;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.text.CaseUtils;
import org.json.JSONObject;

import com.github.zafarkhaja.semver.Version;

import kimono.client.KCTopic;
import kimono.client.KimonoApiException;
import kimono.client.tasks.KCTask;
import kimono.client.tasks.KCTaskAction;
import kimono.client.tasks.KCTaskOrigin;
import kimono.client.tasks.KCTaskType;

public class Task implements KCTask {

	/**
	 * The JSON object representing the task payload. For 2.x and earlier schema
	 * versions this object is a child of the task JSON, for 3.x and later it is the
	 * same as the task JSON.
	 */
	private JSONObject payload;

	/**
	 * The task schema
	 */
	private Version schema;

	/**
	 * The task type
	 */
	private KCTaskType type;

	private static final String OBJECT_TYPE = "object_type";
	private static final String ACTION = "action";
	private static final String TYPE = "type";
	private static final String DATA = "data";
	private static final String CHANGES = "changes";
	private static final String GROUP_ID = "group_id";
	private static final String SEQUENCE = "sequence";
	private static final String SCHEMA = "schema";
	private static final String ID = "id";

	private static Map<String, String> ATTRS1 = new HashMap<>();
	static {
		ATTRS1.put(OBJECT_TYPE, "objectType");
		ATTRS1.put(ACTION, "action");
		ATTRS1.put(TYPE, "type");
		ATTRS1.put(DATA, "data");
		ATTRS1.put(CHANGES, "changes");
		ATTRS1.put(GROUP_ID, "groupId");
		ATTRS1.put(SEQUENCE, "sequence");
		ATTRS1.put(SCHEMA, "schema");
		ATTRS1.put(ID, "id");
	};

	private static Map<String, String> ATTRS2 = new HashMap<>();
	static {
		ATTRS2.put(OBJECT_TYPE, OBJECT_TYPE);
		ATTRS2.put(ACTION, ACTION);
		ATTRS2.put(TYPE, TYPE);
		ATTRS2.put(DATA, DATA);
		ATTRS2.put(CHANGES, CHANGES);
		ATTRS2.put(GROUP_ID, GROUP_ID);
		ATTRS2.put(SEQUENCE, SEQUENCE);
		ATTRS2.put(SCHEMA, SCHEMA);
		ATTRS2.put(ID, ID);
	};

	private String key(String s) {
		return schema.getMajorVersion() == 1 ? CaseUtils.toCamelCase(s, false, '_') : s;
	}

	/**
	 * Construct a Task to wrap a task represented as JSON
	 * 
	 * @param task The JSON object
	 */
	public Task(JSONObject task) {
		if (task.has("schema")) {
			schema = Version.valueOf(task.getString(SCHEMA));
		} else {
			schema = Version.forIntegers(1);
			
			// TODO: REMOVE: There are some malformed Tasks that were created in 
			// production for a very short period of time, in which the schema was
			// found not at the root but within the first element (e.g. "lifecycle_event.schema")
			// Check for that condition but this code can go away soon.
			if( task.length() == 1 ) {
				String key = task.keys().next();
				JSONObject inner = task.getJSONObject(key);
				if( inner != null && inner.has(SCHEMA) ) {
					schema = Version.valueOf(inner.getString(SCHEMA));
				}
			}
		}

		if (schema.getMajorVersion() == 1 || schema.getMajorVersion() == 2) {
			decodePre3x(task);
		} else if (schema.getMajorVersion() == 3) {
			decode3x(task);
		} else {
			throw new KimonoApiException("Task schema not supported: " + schema);
		}
	}

	protected void decodePre3x(JSONObject task) {
		try {
			// Prior to Schema 3.x is like this: { "schema": version, "type": { ... } }
			for (KCTaskType typ : KCTaskType.values()) {
				String str = schema.getMajorVersion() == 1 ? CaseUtils.toCamelCase(typ.name(), false, '_')
						: typ.name().toLowerCase();
				if (task.has(str)) {
					type = typ;
					payload = task.getJSONObject(str);
					break;
				}
			}
		} catch (Exception ex) {
			throw new KimonoApiException("Error decoding 2.x task", ex);
		}
	}

	protected void decode3x(JSONObject task) {
		try {
			// Schema 3.x is flat: { "schema": version, "type": type, ... }
			payload = task;
			type = KCTaskType.valueOf(payload.getString(key(TYPE)).toUpperCase());
		} catch (Exception ex) {
			throw new KimonoApiException("Error decoding 3.x task", ex);
		}
	}

	@Override
	public JSONObject getAttributes() {
		return payload.getJSONObject(key(DATA));
	}

	@Override
	public JSONObject getChanges() {
		return payload.getJSONObject(key(CHANGES));
	}

	@Override
	public KCTopic getTopic() {
		if (payload.has(key(OBJECT_TYPE))) {
			return KCTopic.parse(payload.getString(key(OBJECT_TYPE)));
		} else {
			return null;
		}
	}

	@Override
	public KCTaskType getType() {
		return type;
	}

	@Override
	public KCTaskAction getAction() {
		return KCTaskAction.valueOf(payload.getString(key(ACTION)).toUpperCase());
	}

	@Override
	public String getGroupId() {
		return payload.getString(key(GROUP_ID));
	}

	@Override
	public long getSequence() {
		return payload.getLong(key(SEQUENCE));
	}

	@Override
	public UUID getId() {
		return UUID.fromString(payload.getString(key(ID)));
	}

	@Override
	public String getSchemaVersion() {
		return payload.getString(key(SCHEMA));
	}

	@Override
	public KCTaskOrigin getOrigin() {
		return payload.has("origin") ? KCTaskOrigin.valueOf(payload.getString("origin").toUpperCase()) : null;
	}
}
