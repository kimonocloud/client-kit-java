package kimono.client.impl.tasks;

import kimono.client.tasks.KCTaskAck;

public class TaskAck implements KCTaskAck {

	private KCTaskAck.Status status;
	private String message;
	private String appId;
	
	private TaskAck( KCTaskAck.Status status ) {
		this.status = status;
	}
	
	public static KCTaskAck success() {
		return new TaskAck(KCTaskAck.Status.SUCCESS);
	}
	
	public static KCTaskAck error( String msg ) {
		return new TaskAck(KCTaskAck.Status.ERROR);
	}
	
	public static KCTaskAck error( String msg, Exception cause ) {
		return new TaskAck(KCTaskAck.Status.ERROR);
	}
	
	public TaskAck withMessage( String msg ) {
		message = msg;
		return this;
	}
	
	public TaskAck withAppId( String appId ) {
		this.appId = appId;
		return this;
	}

	@Override
	public Status getStatus() {
		return status;
	}

	@Override
	public String getMessage() {
		return message;
	}

	@Override
	public String getAppId() {
		return appId;
	}
}
