package hoot.system.Monitoring;

import hoot.system.Exception.CollectorException;

public interface CollectorInterface
{
    public String getCollectorName();

    public Object collect() throws CollectorException;
}
