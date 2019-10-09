package kimono.client.impl.tasks;

import org.apache.commons.lang3.exception.ExceptionUtils;

import kimono.client.tasks.KCTaskAck;

public class TaskAck implements KCTaskAck {

	private KCTaskAck.Status status;
	private String message;
	private String appId;
	
	private TaskAck( KCTaskAck.Status status ) {
		this(status,null,null);
	}
	private TaskAck( KCTaskAck.Status status, String msg ) {
		this(status,msg,null);
	}
	private TaskAck( KCTaskAck.Status status, String msg, Throwable cause ) {
		this.status = status;
		if( cause != null ) {
			message = msg+". "+ExceptionUtils.getMessage(cause);
		} else {
			message = msg;
		}
	}
	
	public static KCTaskAck success() {
		return new TaskAck(KCTaskAck.Status.SUCCESS);
	}
	
	public static KCTaskAck error( String msg ) {
		return new TaskAck(KCTaskAck.Status.ERROR,msg);
	}
	
	public static KCTaskAck error( String msg, Exception cause ) {
		return new TaskAck(KCTaskAck.Status.ERROR,msg,cause);
	}
	
	public static KCTaskAck retry( String msg ) {
		return new TaskAck(KCTaskAck.Status.RETRY,msg);
	}
	
	public static KCTaskAck retry( String msg, Exception cause ) {
		return new TaskAck(KCTaskAck.Status.RETRY,msg,cause);
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
	
	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		b.append("[");
		b.append(status.name());
		b.append("]");
		if( message != null ) {
			b.append(" ").append(message);
		}
		if( appId != null ) {
			b.append(" (").append(appId).append(")");
		}
		return b.toString();
	}
}

