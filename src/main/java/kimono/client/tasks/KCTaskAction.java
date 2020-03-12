package kimono.client.tasks;

public enum KCTaskAction {

	NOT_APPLICABLE,
	
	// Lifecycle Event actions
	
	TENANT_INSTALLED,
	
	TENANT_ACTIVATED,
	
	TENANT_UNINSTALLED,
	
	// Sync Event actions
	
	SYNC_START,
	
	SYNC_END,
	
	// Data Event actions
	
	ADD,
	CHANGE,
	DELETE,
	SET
}
