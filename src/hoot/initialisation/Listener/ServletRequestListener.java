package hoot.initialisation.Listener;

import hoot.model.queue.publisher.HttpRequestDurationPublisher;
import hoot.system.Logger.LoggerInterface;
import hoot.system.ObjectManager.ObjectManager;
import hoot.system.util.Pair;

import javax.servlet.ServletRequestEvent;
import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpServletRequest;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@WebListener
public class ServletRequestListener implements javax.servlet.ServletRequestListener
{
    private final Map<String, Instant> requestDurations;

    public ServletRequestListener()
    {
        this.requestDurations = new HashMap<>();
    }

    public void requestInitialized(ServletRequestEvent event)
    {
        String uuid = String.valueOf(UUID.randomUUID());
        event.getServletRequest().setAttribute("uuid", uuid);
        this.requestDurations.put(uuid, Instant.now());
    }

    public void requestDestroyed(ServletRequestEvent event)
    {
        String uuid = (String) event.getServletRequest().getAttribute("uuid");
        var logger = (LoggerInterface) ObjectManager.get(LoggerInterface.class);

        if (uuid == null || uuid.equals("") || this.requestDurations.get(uuid) == null) {
            logger.log("Request has no UUID. This is not supposed to happen.");
            return;
        }

        String   method   = ((HttpServletRequest) event.getServletRequest()).getMethod();
        Duration duration = Duration.between(Instant.now(), this.requestDurations.get(uuid));

        //logger.log("Method: " + method + " Duration: " + duration.toMillis());
        this.getDurationPublisher().publish(new Pair<>(method, duration));
    }

    private HttpRequestDurationPublisher getDurationPublisher()
    {
        return (HttpRequestDurationPublisher) ObjectManager.get(HttpRequestDurationPublisher.class);
    }
}
