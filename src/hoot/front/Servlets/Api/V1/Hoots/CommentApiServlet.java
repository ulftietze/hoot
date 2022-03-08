package hoot.front.Servlets.Api.V1.Hoots;

import com.fasterxml.jackson.databind.JsonNode;
import hoot.front.Servlets.Api.V1.Authentication.LoginApiServlet;
import hoot.model.entities.Comment;
import hoot.model.entities.Hoot;
import hoot.model.entities.HootType;
import hoot.model.entities.User;
import hoot.model.query.api.IsValidUserSession;
import hoot.model.repositories.HootRepository;
import hoot.system.Annotation.AuthenticationRequired;
import hoot.system.Exception.CouldNotSaveException;
import hoot.system.Exception.EntityNotFoundException;
import hoot.system.Exception.NotAllowedException;
import hoot.system.ObjectManager.ObjectManager;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@AuthenticationRequired
@WebServlet("/api/V1/hoot/comment")
public class CommentApiServlet extends AbstractHootApiServlet
{
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException
    {
        HootRepository repository = (HootRepository) ObjectManager.get(HootRepository.class);

        try {
            Comment entity = this.map(request);
            repository.save(entity);

            this.sendResponse(response, HttpServletResponse.SC_OK, this.serialize(repository.getById(entity.id)));
        } catch (CouldNotSaveException | EntityNotFoundException | NotAllowedException e) {
            int httpStatus = HttpServletResponse.SC_NOT_ACCEPTABLE;
            this.sendResponse(response, httpStatus, this.serialize(e.getMessage()));
        }
    }

    private Comment map(HttpServletRequest request) throws IOException, NotAllowedException, EntityNotFoundException
    {
        JsonNode node = (JsonNode) this.deserializeJsonRequestBody(request, JsonNode.class);

        Comment entity = (Comment) ObjectManager.create(Comment.class);
        entity.id            = node.get("id") != null ? node.get("id").asInt() : null;
        entity.content       = node.get("content").asText();
        entity.parent        = this.loadParent(node);
        entity.tags          = this.parseTagsFromContent(entity.content);
        entity.tags.hoot     = entity;
        entity.mentions      = this.parseMentionsFromContent(entity.content);
        entity.mentions.hoot = entity;

        User user = (User) ObjectManager.create(User.class);
        user.id     = (Integer) request.getSession(true).getAttribute(IsValidUserSession.SESSION_USER_IDENTIFIER);
        entity.user = user;

        return entity;
    }

    private Hoot loadParent(JsonNode node) throws EntityNotFoundException, NotAllowedException
    {
        HootRepository repository = (HootRepository) ObjectManager.get(HootRepository.class);
        Hoot           parent     = repository.getById(node.get("parentHootId").asInt());

        if (parent.hootType == HootType.Comment) {
            throw new NotAllowedException("Parent Type " + parent.hootType + " is not allowed.");
        }

        return parent;
    }
}
