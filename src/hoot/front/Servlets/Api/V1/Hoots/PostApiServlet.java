package hoot.front.Servlets.Api.V1.Hoots;

import com.fasterxml.jackson.databind.JsonNode;
import hoot.front.Servlets.Api.V1.Authentication.LoginApiServlet;
import hoot.model.entities.Post;
import hoot.model.entities.User;
import hoot.model.query.api.IsValidUserSession;
import hoot.model.repositories.HootRepository;
import hoot.system.Annotation.AuthenticationRequired;
import hoot.system.Exception.CouldNotSaveException;
import hoot.system.Exception.EntityNotFoundException;
import hoot.system.ObjectManager.ObjectManager;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@AuthenticationRequired
@WebServlet("/api/V1/hoot/post")
public class PostApiServlet extends AbstractHootApiServlet
{
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
    {
        HootRepository repository = (HootRepository) ObjectManager.get(HootRepository.class);

        try {
            Post entity = this.map(request);
            repository.save(entity);

            this.sendResponse(response, HttpServletResponse.SC_OK, this.serialize(repository.getById(entity.id)));
        } catch (CouldNotSaveException | EntityNotFoundException e) {
            int httpStatus = HttpServletResponse.SC_NOT_ACCEPTABLE;
            this.sendResponse(response, httpStatus, this.serialize(e.getMessage()));
        }
    }

    private Post map(HttpServletRequest request) throws EntityNotFoundException, IOException
    {
        JsonNode node = (JsonNode) this.deserializeJsonRequestBody(request, JsonNode.class);

        Post entity = (Post) ObjectManager.create(Post.class);
        entity.id            = node.get("id") != null ? node.get("id").asInt() : null;
        entity.content       = node.get("content").asText();
        entity.onlyFollower  = node.get("onlyFollower").asBoolean();
        entity.tags          = this.parseTagsFromContent(entity.content);
        entity.tags.hoot     = entity;
        entity.mentions      = this.parseMentionsFromContent(entity.content);
        entity.mentions.hoot = entity;

        User user = (User) ObjectManager.create(User.class);
        user.id     = (Integer) request.getSession(true).getAttribute(IsValidUserSession.SESSION_USER_IDENTIFIER);
        entity.user = user;

        return entity;
    }
}
