package hoot.front.Servlets.Api.V1.Hoots.Timeline;

import hoot.front.Servlets.Api.V1.AbstractApiServlet;
import hoot.model.entities.Hoot;
import hoot.model.repositories.HootRepository;
import hoot.model.search.hoot.TimelineSearchCriteria;
import hoot.system.Exception.EntityNotFoundException;
import hoot.system.ObjectManager.ObjectManager;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

@WebServlet("/api/V1/hoot/timeline/global")
public class TimelineGlobalApiServlet extends AbstractApiServlet
{
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
    {
        HootRepository         repository     = (HootRepository) ObjectManager.get(HootRepository.class);
        TimelineSearchCriteria searchCriteria = this.createSearchCriteriaFromRequest(request);

        try {
            ArrayList<Hoot> hoots = repository.getList(searchCriteria);
            this.sendResponse(response, HttpServletResponse.SC_OK, this.serialize(hoots));
        } catch (EntityNotFoundException e) {
            int httpStatus = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
            this.sendResponse(response, httpStatus, this.serialize(e.getMessage()));
        }
    }

    private TimelineSearchCriteria createSearchCriteriaFromRequest(HttpServletRequest request)
    {
        String  lastPost = request.getParameter("lastPostId");
        String  quantity = request.getParameter("quantity");
        String  tags     = request.getParameter("tags");

        TimelineSearchCriteria searchCriteria = this.createSearchCriteriaClass();

        if (tags != null && !tags.equals("")) {
            searchCriteria.tags.addAll(Arrays.asList(tags.split(",")));
        }

        searchCriteria.lastPostId      = lastPost != null && !lastPost.equals("") ? Integer.valueOf(lastPost) : null;
        searchCriteria.defaultPageSize   = quantity != null && !quantity.equals("") ? Integer.valueOf(quantity) : null;


        return searchCriteria;
    }

    private TimelineSearchCriteria createSearchCriteriaClass()
    {
        return (TimelineSearchCriteria) ObjectManager.create(TimelineSearchCriteria.class);
    }
}
