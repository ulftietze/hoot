package hoot.app.Servlets.Api.V1.User;

import hoot.app.Servlets.Api.V1.AbstractApiServlet;
import hoot.model.entities.Follower;
import hoot.model.repositories.FollowerRepository;
import hoot.model.search.FollowsSearchCriteria;
import hoot.system.Security.AuthenticationRequired;
import hoot.system.Exception.EntityNotFoundException;
import hoot.system.objects.ObjectManager;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

@AuthenticationRequired
@WebServlet({"/api/V1/user/follows"})
public class UserFollowsApiServlet extends AbstractApiServlet
{
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
    {
        FollowerRepository    repository     = (FollowerRepository) ObjectManager.get(FollowerRepository.class);
        FollowsSearchCriteria searchCriteria = this.createFollowsSearchCriteriaFromRequest(request);

        try {
            ArrayList<Follower> followers = repository.getList(searchCriteria);
            this.sendResponse(response, HttpServletResponse.SC_OK, this.serialize(followers));
        } catch (EntityNotFoundException e) {
            this.sendResponse(response, HttpServletResponse.SC_CONFLICT, this.serialize(false));
        }
    }

    private FollowsSearchCriteria createFollowsSearchCriteriaFromRequest(HttpServletRequest request)
    {
        String userId   = request.getParameter("userId");
        String lastUser = request.getParameter("lastUserId");
        String quantity = request.getParameter("quantity");

        FollowsSearchCriteria searchCriteria = this.createFollowsSearchCriteria();

        searchCriteria.lastUserId      = lastUser != null && !lastUser.equals("") ? Integer.valueOf(lastUser) : null;
        searchCriteria.defaultPageSize = quantity != null && !quantity.equals("") ? Integer.valueOf(quantity) : null;
        searchCriteria.userId          = userId != null && !userId.equals("") ? Integer.valueOf(userId) : null;

        return searchCriteria;
    }

    private FollowsSearchCriteria createFollowsSearchCriteria()
    {
        return (FollowsSearchCriteria) ObjectManager.get(FollowsSearchCriteria.class);
    }
}
