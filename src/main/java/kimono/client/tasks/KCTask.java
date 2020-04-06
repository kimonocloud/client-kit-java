package kimono.client.tasks;

import java.util.UUID;

import org.json.JSONObject;

import kimono.client.KCTopic;

public interface KCTask {

	UUID getId();
	
	String getSchemaVersion();
	
	JSONObject getAttributes();
	
	JSONObject getChanges();

	KCTopic getTopic();
	
	KCTaskAction getAction();
	
	KCTaskType getType();
	
	KCTaskOrigin getOrigin();
	
	String getGroupId();
	
	long getSequence();
	
	JSONObject getPayload();
}
