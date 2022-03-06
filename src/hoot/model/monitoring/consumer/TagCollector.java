package hoot.model.monitoring.consumer;

import hoot.model.entities.HootTags;
import hoot.model.entities.Tag;
import hoot.model.queue.publisher.TagsPublisher;
import hoot.system.Exception.CollectorException;
import hoot.system.Logger.LoggerInterface;
import hoot.system.Monitoring.CollectorInterface;
import hoot.system.Monitoring.CollectorResult;
import hoot.system.ObjectManager.ObjectManager;
import hoot.system.Queue.ConsumerInterface;
import hoot.system.Queue.QueueManager;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class TagCollector extends Thread implements CollectorInterface, ConsumerInterface
{
    public final static  String COLLECTOR_NAME               = "Popular Tags";
    private final static int    AMOUNT_OF_POPULAR_TAGS       = 15;
    private final static long   PERIOD_POPULAR_TAGS_IN_HOURS = 24;

    private final QueueManager queueManager;

    private final NavigableMap<Instant, ArrayList<Tag>> tagsInPeriod;

    private final NavigableMap<Tag, AtomicInteger> tagUsageAmount;

    private final PriorityBlockingQueue<TagAmountPair> mostUsedTags;

    private final LoggerInterface logger;

    private boolean running = true;

    public TagCollector()
    {
        this.queueManager   = (QueueManager) ObjectManager.get(QueueManager.class);
        this.tagsInPeriod   = Collections.synchronizedNavigableMap(new TreeMap<>());
        this.tagUsageAmount = Collections.synchronizedNavigableMap(new TreeMap<>());
        this.mostUsedTags   = new PriorityBlockingQueue<>();
        this.logger         = (LoggerInterface) ObjectManager.get(LoggerInterface.class);
    }

    @Override
    public void run()
    {
        while (this.running) {
            HootTags hootTags = (HootTags) this.queueManager.take(TagsPublisher.QUEUE_ID);
            Instant  now      = Instant.now();

            if (hootTags == null) {
                continue;
            }

            hootTags.tags.forEach(tag -> this.addTagToDataStorage(tag, now));
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
        Instant ofHours = Instant.now().minus(Duration.ofSeconds(10));

        Map<Instant, ArrayList<Tag>> outdatedTagsPublished = this.tagsInPeriod.headMap(ofHours, true);
        outdatedTagsPublished.forEach((t, tags) -> tags.forEach(tag -> {
            this.tagUsageAmount.get(tag).decrementAndGet();
            this.mostUsedTags.remove(this.getIfTagIsInMostUsed(tag));
        }));
        outdatedTagsPublished.clear();

        return new CollectorResult()
        {{
            put("popularTags", mostUsedTags);
        }};
    }

    @Override
    public void stopRun()
    {
        this.running = false;
    }

    private void addTagToDataStorage(Tag tag, Instant now)
    {
        this.tagsInPeriod.computeIfAbsent(now, k -> new ArrayList<>());
        this.tagsInPeriod.get(now).add(tag);
        this.tagUsageAmount.computeIfAbsent(tag, k -> new AtomicInteger());

        int           mostUsedSize = this.mostUsedTags.size();
        int           quantity     = this.tagUsageAmount.get(tag).incrementAndGet();
        TagAmountPair tagInList    = this.getIfTagIsInMostUsed(tag);

        if (tagInList != null) {
            this.mostUsedTags.remove(tagInList);
            tagInList.quantity++;
            this.mostUsedTags.add(tagInList);
        } else if (mostUsedSize < AMOUNT_OF_POPULAR_TAGS) {
            this.mostUsedTags.add(new TagAmountPair(quantity, tag)); // TODO: ObjectManager Create with params
        } else if (this.mostUsedTags.peek().quantity <= quantity) {
            this.mostUsedTags.peek().tag = tag;
            this.mostUsedTags.peek().quantity = quantity;
            this.mostUsedTags.add(new TagAmountPair(quantity, tag));
        }

//        this.logger.log("-------AFTER----------");
//        this.mostUsedTags.forEach(p -> this.logger.log(p.tag.tag + ": " + p.quantity));
//        this.logger.log("-----------------");
    }

    private TagAmountPair getIfTagIsInMostUsed(Tag tag)
    {
        TagAmountPair tagInList = null;

        for (TagAmountPair pair : this.mostUsedTags) {
            if (Objects.equals(pair.tag.tag, tag.tag)) {
                tagInList = pair;
                break;
            }
        }

        return tagInList;
    }

    private class TagAmountPair implements Comparable<TagAmountPair>
    {

        Integer quantity;
        Tag     tag;

        public TagAmountPair(Integer quantity, Tag tag)
        {
            this.quantity = quantity;
            this.tag      = tag;
        }

        @Override
        public int compareTo(TagAmountPair tagAmountPair)
        {
            return quantity.compareTo(tagAmountPair.quantity);
        }
    }
}
