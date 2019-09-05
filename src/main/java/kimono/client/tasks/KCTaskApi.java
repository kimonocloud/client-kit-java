package kimono.client.tasks;

import kimono.client.KCSupplier;

public interface KCTaskApi extends KCSupplier<KCTask> {

	void ackTask( KCTask task, KCTaskAck ack );
}
