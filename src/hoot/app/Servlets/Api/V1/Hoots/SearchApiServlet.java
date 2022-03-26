package hoot.app.Servlets.Api.V1.Hoots;

import hoot.app.Servlets.Api.V1.AbstractApiServlet;
import hoot.model.entities.Hoot;
import hoot.model.repositories.HootRepository;
import hoot.model.search.hoot.HootSearchCriteria;
import hoot.system.Exception.EntityNotFoundException;
import hoot.system.objects.ObjectManager;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

//@AuthenticationRequired
@WebServlet("/api/V1/hoot/search")
public class SearchApiServlet extends AbstractApiServlet
{
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
    {
        HootRepository     repository     = (HootRepository) ObjectManager.get(HootRepository.class);
        HootSearchCriteria searchCriteria = this.createSearchCriteriaFromRequest(request);

        try {
            HashMap<String, ArrayList<Hoot>> hoots = new HashMap<>();
            hoots.put("hoots", repository.getList(searchCriteria));
            this.sendResponse(response, HttpServletResponse.SC_OK, this.serialize(hoots));
        } catch (EntityNotFoundException e) {
            int httpStatus = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
            this.sendResponse(response, httpStatus, this.serialize(e.getMessage()));
        }
    }

    private HootSearchCriteria createSearchCriteriaFromRequest(HttpServletRequest request)
    {
        String lastPost = request.getParameter("lastPostId");
        String quantity = request.getParameter("quantity");
        String tags     = request.getParameter("tags");
        String userId   = request.getParameter("userId");

        HootSearchCriteria searchCriteria = (HootSearchCriteria) ObjectManager.create(HootSearchCriteria.class);

        if (tags != null && !tags.equals("")) {
            searchCriteria.tags.addAll(Arrays.asList(tags.split(",")));
        }

        searchCriteria.lastPostId      = lastPost != null && !lastPost.equals("") ? Integer.valueOf(lastPost) : null;
        searchCriteria.defaultPageSize = quantity != null && !quantity.equals("") ? Integer.valueOf(quantity) : null;
        searchCriteria.userId          = userId != null && !userId.equals("") ? Integer.valueOf(userId) : null;

        return searchCriteria;
    }
}
