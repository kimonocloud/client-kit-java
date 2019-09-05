package kimono.client.tasks;

public interface KCTaskAck {

	public enum Status { SUCCESS, ERROR };

	Status getStatus();
	
	String getMessage();
	
	String getAppId();
}
