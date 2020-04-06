package kimono.client.csv;

import java.io.IOException;

import org.json.JSONObject;

import kimono.client.KCTopic;
import kimono.client.tasks.KCTask;

public interface KCFile {

	String getGroupId();
	
	KCTopic getTopic();
	
	void open() throws IOException;
	
	void close();

	void write(KCTask task, JSONObject attrs) throws IOException;
	
	void commit() throws IOException;
}
