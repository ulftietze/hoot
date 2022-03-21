package hoot.model.monitoring;

import hoot.model.entities.Tag;
import hoot.model.repositories.TagRepository;
import hoot.model.search.hoot.TagsPostedInIntervalSearchCriteria;
import hoot.system.Exception.CollectorException;
import hoot.system.Exception.EntityNotFoundException;
import hoot.system.Logger.LoggerInterface;
import hoot.system.Monitoring.CollectorInterface;
import hoot.system.Monitoring.CollectorResult;
import hoot.system.objects.Inject;
import hoot.system.objects.ObjectManager;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class TagCollector extends Thread implements CollectorInterface
{
    public final static String COLLECTOR_NAME                = "Popular Tags";
    public final        int    INTERVAL_LOAD_TAGS_IN_MINUTES = 10;

    @Inject private TagRepository   tagRepository;
    @Inject private LoggerInterface logger;

    private ArrayList<Tag> mostUsedTags;

    private boolean running = true;

    public TagCollector()
    {
        this.mostUsedTags = new ArrayList<>();
    }

    @Override
    public void run()
    {
        while (this.running) {
            try {
                TagsPostedInIntervalSearchCriteria searchCriteria = this.getSearchCriteria();
                this.mostUsedTags = this.tagRepository.getList(searchCriteria);
            } catch (EntityNotFoundException e) {
                this.logger.logException("Something went wrong: " + e.getMessage(), e);
            }

            try {
                TimeUnit.MINUTES.sleep(INTERVAL_LOAD_TAGS_IN_MINUTES);
            } catch (InterruptedException ignore) {
            }
        }
    }

    @Override
    public String getCollectorName()
    {
        return COLLECTOR_NAME;
    }

    @Override
    public CollectorResult collect() throws CollectorException
    {
        return new CollectorResult()
        {{
            put("popularTags", mostUsedTags);
        }};
    }

    public void stopRun()
    {
        this.running = false;
    }

    private TagsPostedInIntervalSearchCriteria getSearchCriteria()
    {
        return (TagsPostedInIntervalSearchCriteria) ObjectManager.get(TagsPostedInIntervalSearchCriteria.class);
    }
}
