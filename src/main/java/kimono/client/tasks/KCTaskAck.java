package kimono.client.tasks;

public interface KCTaskAck {

	public enum Status { 
		
		/**
		 * Acknowledge the task as successfully delivered and processed without errors
		 */
		SUCCESS, 
		
		/**
		 * Acknowledge the task as successfully delivered but processed with errors
		 */
		ERROR, 
		
		/**
		 * Do not acknowledge the task as successfully delivered; it will be retried
		 */
		RETRY 
	}

	Status getStatus();
	
	String getMessage();
	
	String getAppId();
}
