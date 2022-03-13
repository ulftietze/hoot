package hoot.app.Servlets.Api.V1.Hoots.Timeline;

import hoot.app.Servlets.Api.V1.AbstractApiServlet;
import hoot.model.entities.Hoot;
import hoot.model.query.api.IsValidUserSession;
import hoot.model.repositories.HootRepository;
import hoot.model.search.hoot.TimelineMineSearchCriteria;
import hoot.system.Annotation.AuthenticationRequired;
import hoot.system.Exception.EntityNotFoundException;
import hoot.system.ObjectManager.ObjectManager;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

@AuthenticationRequired
@WebServlet("/api/V1/hoot/timeline/mine")
public class TimelineMineApiServlet extends AbstractApiServlet
{
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
    {
        HootRepository             repository     = (HootRepository) ObjectManager.get(HootRepository.class);
        TimelineMineSearchCriteria searchCriteria = this.createSearchCriteriaFromRequest(request);

        try {
            ArrayList<Hoot> hoots = repository.getList(searchCriteria);
            this.sendResponse(response, HttpServletResponse.SC_OK, this.serialize(hoots));
        } catch (EntityNotFoundException e) {
            int httpStatus = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
            this.sendResponse(response, httpStatus, this.serialize(e.getMessage()));
        }
    }

    private TimelineMineSearchCriteria createSearchCriteriaFromRequest(HttpServletRequest request)
    {
        String  lastPost = request.getParameter("lastPostId");
        String  quantity = request.getParameter("quantity");
        String  tags     = request.getParameter("tags");
        Integer userId   = (Integer) request.getSession(true).getAttribute(IsValidUserSession.SESSION_USER_IDENTIFIER);

        TimelineMineSearchCriteria searchCriteria = this.createSearchCriteriaClass();

        if (tags != null && !tags.equals("")) {
            searchCriteria.tags.addAll(Arrays.asList(tags.split(",")));
        }

        searchCriteria.lastPostId      = lastPost != null && !lastPost.equals("") ? Integer.valueOf(lastPost) : null;
        searchCriteria.defaultPageSize   = quantity != null && !quantity.equals("") ? Integer.valueOf(quantity) : null;
        searchCriteria.timelineForUserId = userId;

        return searchCriteria;
    }

    private TimelineMineSearchCriteria createSearchCriteriaClass()
    {
        return (TimelineMineSearchCriteria) ObjectManager.create(TimelineMineSearchCriteria.class);
    }
}
