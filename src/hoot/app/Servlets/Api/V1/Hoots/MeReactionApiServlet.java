package hoot.app.Servlets.Api.V1.Hoots;

import com.fasterxml.jackson.databind.JsonNode;
import hoot.app.Servlets.Api.V1.AbstractApiServlet;
import hoot.model.entities.*;
import hoot.model.query.api.IsValidUserSession;
import hoot.model.repositories.ReactionRepository;
import hoot.system.Annotation.AuthenticationRequired;
import hoot.system.Exception.CouldNotDeleteException;
import hoot.system.Exception.CouldNotSaveException;
import hoot.system.ObjectManager.ObjectManager;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@AuthenticationRequired
@WebServlet({"/api/V1/hoot/me/reaction"})
public class MeReactionApiServlet extends AbstractApiServlet
{
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
    {
        ReactionRepository repository = (ReactionRepository) ObjectManager.get(ReactionRepository.class);

        try {
            Reaction reaction = this.mapReactionFromRequest(request);
            repository.save(reaction);
            this.sendResponse(response, HttpServletResponse.SC_OK, this.serialize(true));
        } catch (CouldNotSaveException e) {
            this.sendResponse(response, HttpServletResponse.SC_CONFLICT, this.serialize(false));
        } catch (Exception e) {
            this.sendResponse(response, HttpServletResponse.SC_CONFLICT, this.serialize(e.getMessage()));
        }
    }

    public void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
    {
        ReactionRepository repository = (ReactionRepository) ObjectManager.get(ReactionRepository.class);

        try {
            Reaction reaction = this.mapReactionFromRequest(request);
            repository.delete(reaction);
            this.sendResponse(response, HttpServletResponse.SC_OK, this.serialize(true));
        } catch (CouldNotDeleteException e) {
            this.sendResponse(response, HttpServletResponse.SC_CONFLICT, this.serialize(false));
        } catch (Exception e) {
            this.sendResponse(response, HttpServletResponse.SC_CONFLICT, this.serialize(e.getMessage()));
        }
    }

    private Reaction mapReactionFromRequest(HttpServletRequest request) throws IOException, IllegalArgumentException
    {
        JsonNode reactionBody = (JsonNode) this.deserializeJsonRequestBody(request, JsonNode.class);
        Reaction reaction     = (Reaction) ObjectManager.create(Reaction.class);

        reaction.user    = (User) ObjectManager.create(User.class);
        reaction.hoot    = this.getHootByType(reactionBody.get("hootType").asText());
        reaction.user.id = (Integer) request.getSession(true).getAttribute(IsValidUserSession.SESSION_USER_IDENTIFIER);
        reaction.hoot.id = reactionBody.get("hootId").asInt();

        if (reactionBody.get("interaction") != null) {
            reaction.interaction = Interaction.valueOf(reactionBody.get("interaction").asText());
        }

        return reaction;
    }

    private Hoot getHootByType(String type)
    {
        switch (HootType.valueOf(type)) {
            case Post:
                return (Post) ObjectManager.create(Post.class);
            case Image:
                return (Image) ObjectManager.create(Image.class);
            case Comment:
                return (Comment) ObjectManager.create(Comment.class);
        }

        throw new RuntimeException("Could not detect hootType: " + type);
    }
}
