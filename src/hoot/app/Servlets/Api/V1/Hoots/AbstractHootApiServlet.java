package hoot.app.Servlets.Api.V1.Hoots;

import hoot.app.Servlets.Api.V1.AbstractApiServlet;
import hoot.model.entities.HootMentions;
import hoot.model.entities.HootTags;
import hoot.model.entities.Mention;
import hoot.model.entities.Tag;
import hoot.model.repositories.UserRepository;
import hoot.system.Exception.EntityNotFoundException;
import hoot.system.objects.ObjectManager;

import java.util.ArrayList;
import java.util.StringTokenizer;

public abstract class AbstractHootApiServlet extends AbstractApiServlet
{
    protected HootTags parseTagsFromContent(String content)
    {
        HootTags        hootTags  = (HootTags) ObjectManager.create(HootTags.class);
        ArrayList<Tag>  tags      = new ArrayList<>();
        StringTokenizer tokenizer = new StringTokenizer(content);

        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();
            if (token.startsWith("#")) {
                Tag tag = (Tag) ObjectManager.create(Tag.class);
                tag.tag = token.substring(token.indexOf("#") + 1);
                tags.add(tag);
            }
        }

        hootTags.tags = tags;

        return hootTags;
    }

    protected HootMentions parseMentionsFromContent(String content) throws EntityNotFoundException
    {
        UserRepository repository = (UserRepository) ObjectManager.get(UserRepository.class);

        HootMentions       hootMentions = (HootMentions) ObjectManager.create(HootMentions.class);
        ArrayList<Mention> mentions     = new ArrayList<>();
        StringTokenizer    tokenizer    = new StringTokenizer(content);

        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();
            if (token.startsWith("@")) {
                Mention mention = (Mention) ObjectManager.create(Mention.class);
                mention.mentioned = repository.getByUsername(token.substring(token.indexOf("@") + 1));
                mentions.add(mention);
            }
        }

        hootMentions.mentions = mentions;

        return hootMentions;
    }
}
