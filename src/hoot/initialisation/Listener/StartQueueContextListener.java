package hoot.initialisation.Listener;

import hoot.model.consumer.CountLoginsCollectorConsumer;
import hoot.system.Monitoring.Monitor;
import hoot.system.ObjectManager.ObjectManager;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class StartQueueContextListener implements ServletContextListener
{
    public void contextInitialized(ServletContextEvent contextEvent)
    {
        CountLoginsCollectorConsumer loginsCollector = this.createLoginsCollector();

        Monitor monitor = (Monitor) ObjectManager.create(Monitor.class);
        monitor.addCollector(loginsCollector);
        monitor.start();
    }

    private CountLoginsCollectorConsumer createLoginsCollector()
    {
        return (CountLoginsCollectorConsumer) ObjectManager.create(CountLoginsCollectorConsumer.class);
    }
}
