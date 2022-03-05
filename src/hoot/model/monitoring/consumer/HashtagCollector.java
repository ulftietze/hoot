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
import java.util.concurrent.atomic.AtomicInteger;

public class HashtagCollector extends Thread implements CollectorInterface, ConsumerInterface
{
    public final static  String COLLECTOR_NAME               = "Popular Tags";
    private final static int    AMOUNT_OF_POPULAR_TAGS       = 10;
    private final static long   PERIOD_POPULAR_TAGS_IN_HOURS = 24;

    private final QueueManager queueManager;

    private final NavigableMap<Instant, ArrayList<Tag>> tagsInPeriod;

    private final NavigableMap<Tag, AtomicInteger> tagUsageAmount;

    private final PriorityQueue<TagAmountPair> mostUsedTags;

    private final LoggerInterface logger;

    public HashtagCollector()
    {
        this.queueManager   = (QueueManager) ObjectManager.get(QueueManager.class);
        this.tagsInPeriod   = Collections.synchronizedNavigableMap(new TreeMap<>());
        this.tagUsageAmount = Collections.synchronizedNavigableMap(new TreeMap<>());
        this.mostUsedTags   = new PriorityQueue<>(); // Ignore Synchronized, we only save in one process
        this.logger         = (LoggerInterface) ObjectManager.get(LoggerInterface.class);
    }

    @Override
    public void run()
    {
        while (true) {
            HootTags hootTags = (HootTags) this.queueManager.take(TagsPublisher.QUEUE_ID);
            Instant  now      = Instant.now();

            hootTags.tags.forEach(tag -> {
                this.tagsInPeriod.computeIfAbsent(now, k -> new ArrayList<>());
                this.tagsInPeriod.get(now).add(tag);

                this.tagUsageAmount.computeIfAbsent(tag, k -> new AtomicInteger());
                int           amount               = this.tagUsageAmount.get(tag).incrementAndGet();
                //TagAmountPair tagInList            = this.mostUsedTags.stream().filter(pair -> pair.tag == tag);
                //boolean       tagNotInMostUsedList = this.mostUsedTags.stream().noneMatch(pair -> pair.tag == tag);
                //int           mostUsedSize         = this.mostUsedTags.size();
                //TagAmountPair lowestOfMostUsed     = this.mostUsedTags.peek();

                //if (mostUsedSize < AMOUNT_OF_POPULAR_TAGS) {

                //} else if (!tagNotInMostUsedList && lowestOfMostUsed.quantity > amount) {

                //}
            });
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
        Instant ofHours = Instant.now().minus(Duration.ofHours(PERIOD_POPULAR_TAGS_IN_HOURS));

        Map<Instant, ArrayList<Tag>> outdatedTagsPublished = this.tagsInPeriod.headMap(ofHours, true);
        outdatedTagsPublished.forEach((t, tags) -> tags.forEach(tag -> this.tagUsageAmount.get(tag).decrementAndGet()));
        outdatedTagsPublished.clear();

        Integer amountOfTags = AMOUNT_OF_POPULAR_TAGS;

        return new CollectorResult()
        {{
            //put("popularTags", tagUsageAmount.headMap());
        }};
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
