package hoot.model.consumer;

import hoot.system.Exception.CollectorException;
import hoot.system.Monitoring.CollectorInterface;
import hoot.system.Monitoring.QueueManager;
import hoot.system.ObjectManager.ObjectManager;

public class CountLoginsCollectorConsumer implements CollectorInterface
{
    private final QueueManager queueManager;

    private Integer loginsPerCollect;

    public CountLoginsCollectorConsumer()
    {
        this.queueManager = (QueueManager) ObjectManager.get(QueueManager.class);
    }

    @Override
    public void run()
    {
        while (true) {
            // TODO: RESET
            this.queueManager.take("logins");
            this.loginsPerCollect++;
        }
    }

    @Override
    public String getCollectorName()
    {
        return "CountLogins";
    }

    @Override
    public Object collect() throws CollectorException
    {
        Integer c = this.loginsPerCollect;
        this.loginsPerCollect = 0;

        return c;
    }
}
