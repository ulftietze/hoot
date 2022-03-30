package hoot.app.Servlets.Api.V1.User;

import hoot.app.Servlets.Api.V1.AbstractApiServlet;
import hoot.model.query.api.IsValidUserSession;
import hoot.model.repositories.FollowerRepository;
import hoot.model.search.UserFollowsUserSearchCriteria;
import hoot.system.Exception.EntityNotFoundException;
import hoot.system.Security.AuthenticationRequired;
import hoot.system.objects.ObjectManager;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@AuthenticationRequired
@WebServlet({"/api/V1/user/me/follows"})
public class MeFollowsApiServlet extends AbstractApiServlet
{
    private FollowerRepository repository;

    @Override
    public void init() throws ServletException
    {
        super.init();

        this.repository = (FollowerRepository) ObjectManager.get(FollowerRepository.class);
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
    {
        Integer currentUserId = (Integer) request
                .getSession(true)
                .getAttribute(IsValidUserSession.SESSION_USER_IDENTIFIER);
        Integer userIdToCheck = Integer.valueOf(request.getParameter("userId"));

        UserFollowsUserSearchCriteria searchCriteria = this.createUserFollowsUserSearchCriteria();
        searchCriteria.currentUserId = currentUserId;
        searchCriteria.userIdToCheck = userIdToCheck;

        try {
            boolean follows = this.repository.getList(searchCriteria).size() > 0;
            this.sendResponse(response, HttpServletResponse.SC_OK, this.serialize(follows));
        } catch (EntityNotFoundException e) {
            this.sendResponse(response, HttpServletResponse.SC_CONFLICT, this.serialize(false));
        }
    }

    private UserFollowsUserSearchCriteria createUserFollowsUserSearchCriteria()
    {
        return (UserFollowsUserSearchCriteria) ObjectManager.create(UserFollowsUserSearchCriteria.class);
    }
}
