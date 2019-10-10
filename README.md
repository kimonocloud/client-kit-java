# api-client-kit

The Client Kit for Java is a framework for implementing a Java-based driver for your Kimono 
Integration. It's not required to implement a Kimono Integration but is highly recommended 
for developers using Java. The Client Kit handles the low-level details of implementing a
scalable driver, including:

* API Authentication
* Tenant Discovery
* Calling the appropriate Tasks API
* Implementing a Task Loop

## Example

See the `api-simple-driver` project for an example of a Kimono Integration built on 
Client Kit for Java.

## Quick Start

Creating a simple Integration driver is easy:

1. Use the Integration Blueprint (`blueprint/blueprint.xml`) as a starting point and install it into Kimono

2. Derive a class from the `AbstractDriver` base class and implement its `main` method to parse the command-line.

3. Write one or more functions to process Tasks.

4. Set the `KIMONO_API_KEY` environment variable.

These steps are covered in greater detail below. Refer to the `simple-driver` project for a working example.

```
public void MyDriver extends AbstractDriver {

	@Override protected KCDriverInfo newDriverInfo() {
		return new KCDriverInfo("MyDriver");
	}

	@Override protected void configureTaskHandlers(KCTaskPoller poller) {

		// Handle Sync Start/Sync End events
		poller.setTaskHandler(KCTaskType.SYNC_EVENT, this::handleSyncEvent);

		// Handle Data Events
		poller.setTaskHandler(KCTaskType.DATA_EVENT, this::handleDataEvent);
	}    

    protected KCTaskAck handleSyncEvent( KCTenant tenant, KCTask task ) {
        System.out.println("Got a Sync Start/Sync End event: "+task);
        return TaskAck.success();
    }

    protected KCTaskAck handleDataEvent( KCTenant tenant, KCTask task ) {
        System.out.println("Got a Data Event: "+task);
        return TaskAck.success();
    }
    
	public static void main(String[] args) {
		try {
			new MyDriver().parseCommandLine(args).run();
		} catch (Exception ex) {
			System.err.println(ex);
		}
	}    
}
```

# Authentication

Client Kit employs two forms of authentication: API Key and OAuth2.

API Key is required to discover the instances of your Integration that are installed in Kimono. These are referred to as Tenants. The `listTenants` API is called each time the driver iterates through its tenants. By default, the API Key is obtained from the `KIMONO_API_KEY` environment variable. You can change this default behavior by implementing your own configuration class.

Each tenant returned by the `listTenants` API includes OAuth2 credentials to connect to that tenant. Client Kit uses these credentials to obtain a bearer token, which it delegates to a Token Manager to persist. The default Token Manager implementation records the bearer token in memory. Consequently, restarting the driver will request a new bearer token for each tenant. You are encouraged to use a persistent Token Manager such as `RedisTokenManager` or to implement your own Token Manager as discussed later.

# Tenant Discovery

Client Kit handles discovering which tenants to process by calling the `listTenants` API each time it polls tenants for Tasks. There are several ways to scope and filter tenants:

* Scope by Integration name
* Scope by Account ID
* Scope by Tenant ID
* Use a Predicate to filter tenants

## Scope by Integration Name

Client Kit selects only those tenants that have the Integration name you specify in the `newDriverInfo` method. 

```
@Override protected KCDriverInfo newDriverInfo() {
	return new KCDriverInfo("MyDriver");
}
```

## Scope by Account ID

For some types of clients it is useful to iterate all tenants for a specific account. Use the `-account:id` option on the command-line to select only those tenants belonging to the specified account.

```
$ MyDriver -account:0109cd99-4764-43b9-9964-327d6ae5013e
```

## Scope by Tenant ID

During development it is easiest to work with a single tenant. You can specify one or more  `tenant_id`s on the command-line with the `-tenant:id` option, where _id_ is a command-delimited list of tenant IDs. Note this option takes precedence over all other scoping options. For example, you can specify a tenant that doesn't match the Integration name . You can ; when specified this When this option is used the Task Loop will only

```
$ MyDriver -tenant:00300bb7-70ab-43cf-a950-1a216cc4e82a
```

## Using a Predicate to Filter Tenants

You can perform your own filtering of tenants by implementing a Predicate. The Task Loop will only enumerate tasks for tenants that satisfy your filter. The following example obtains a "customer number" from the Account associated with this tenant and returns true if that customer is enabled or not (presumably checking some state in your application).

```
@Override protected void configureTaskHandlers( KCTaskPoller poller ) {

    // Only process tenants if customers that are enabled in my app
    poller.setPredicate(tenant->{
        String myCustomerNumber = tenant.getTenantInfo().getAccount().getUserdata();
        return isCustomerEnabled(myCustomerNumber);
    }
}
```

## KCTenant

The `kimono.client.KCTenant` interface encapsulates a tenant; that is, a single instance of your Integration installed in an Interop Cloud. You can access the underlying Kimono API-provided tenant information by calling `getTenantInfo()`:

```
tenant.getTenantInfo().getAccount().getUserdata();
```

# Task Processing

The primary job of a Driver is to process the Tasks in each Integration tenant's Task Queue. Client Kit takes care of selecting tenants and requesting tasks, while you take care of writing code to respond to each Task. This is accomplished by registering `KCTaskHandler` in the `configureTaskHandlers` method. Task Handlers receive a `KCTenant` and `KCTask` pair and respond with a `KCTaskAck`.

```
@Override
protected void configureTaskHandlers(KCTaskPoller poller) {
    
    // Handle Sync Start/Sync End events
    poller.setTaskHandler(KCTaskType.SYNC_EVENT, this::handleSyncEvent);
}

protected KCTaskAck handleSyncEvent( KCTenant tenant, KCTask task ) {
    
    ...
    return TaskAck.success();
}
```

## Tasks

The `kimono.client.tasks.KCTask` interface encapsulates a task.

```
protected KCTaskAck handleDataEvent( KCTenant tenant, KCTask task ) {

    // Task ID
    UUID id = task.getId();

    // Task Type and Action
    KCTaskType type = task.getType();
    KCTaskAction action = task.getAction();

    // Topic, current attributes of this data object, and prior values of changed attributes
    KCTopic topic = task.getTopic();
    JSONObject attrs = task.getAttributes();
    JSONObject diffs = task.getChanges();

    // Other info
    KCTaskOrigin origin = task.getOrigin();
    String schema = task.getSchemaVersion();
    String groupId = task.getGroupId();

    return TaskAck.success();
}
```

## Task Acknowledgement

When a Task is delivered to a client app for processing it remains on the Task Queue until it is acknowledged with a Success or Error status. A Task acknowledged with a Retry status will be retried. 

Scalable client implementations should offload tasks from Kimono to other infrastructure that is more suitable for asynchronous workload processing. Tasks should be acknowledged as quickly as possible and should not be processed synchronously on the Task Loop. When Client Kit is configured to use the Managed Tasks API, a task that is not acknowledged in a timely fashion will time out and cause it to be redelivered by Kimono. 

### KCTaskAck

The `kimono.client.tasks.KCTaskAck` interface encapsulates a tack ack. Use the concrete `kimono.client.impl.tasks.TaskAck` implementation to return a Success, Error, or Retry acknowledgement.

```
protected KCTaskAck handleSyncEvent( KCTenant tenant, KCTask task ) {
    
    try {
        // Handle this task...

        // Success!        
        return TaskAck.success();
    } catch( Exception ex ) {

        // Error
        return TaskAck.error("Failed to process", ex);
    }
}
```

#### Success

A simple success ack:

```
return TaskAck.success();
```

A success ack with optional message:

```
return TaskAck.success("John Doe added with ID 8858");
```

#### Error

An error acknowledgement must include a message, which is displayed beneath the task on the Synchronization tab in Dashboard.

```
return TaskAck.error("Invalid email address");
```

#### Retry

A retry acknowledgement must include a message, which is displayed beneath the task on the Synchronization tab in Dashboard.

```
return TaskAck.retry("My App is in temporarily unavailable for maintenance");
```


