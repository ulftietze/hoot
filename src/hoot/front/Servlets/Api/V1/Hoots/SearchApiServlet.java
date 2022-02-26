package hoot.front.Servlets.Api.V1.Hoots;

import hoot.front.Servlets.Api.V1.AbstractApiServlet;
import hoot.front.api.dto.hoot.HootDTO;
import hoot.front.api.dto.hoot.HootsDTO;
import hoot.model.entities.Hoot;
import hoot.model.mapper.entityToDto.HootToHootDtoMapper;
import hoot.model.repositories.HootRepository;
import hoot.model.search.HootSearchCriteria;
import hoot.system.Annotation.AuthenticationRequired;
import hoot.system.Exception.CouldNotMapException;
import hoot.system.Exception.EntityNotFoundException;
import hoot.system.ObjectManager.ObjectManager;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

@AuthenticationRequired
@WebServlet("/api/V1/hoot/search")
public class SearchApiServlet extends AbstractApiServlet
{
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
    {
        HootRepository     repository     = (HootRepository) ObjectManager.get(HootRepository.class);
        HootSearchCriteria searchCriteria = this.createSearchCriteriaFromRequest(request);

        try {
            ArrayList<Hoot> hoots    = repository.getList(searchCriteria);
            HootsDTO        hootsDTO = new HootsDTO();

            for (Hoot hoot : hoots) {
                HootDTO dto = this.getHootToHootDtoMapper().map(hoot);
                hootsDTO.addHoot(dto);
            }

            this.sendResponse(response, HttpServletResponse.SC_OK, this.serializeJsonResponseBody(hootsDTO));
        } catch (EntityNotFoundException | CouldNotMapException e) {
            int httpStatus = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
            this.sendResponse(response, httpStatus, this.serializeJsonResponseBody(e.getMessage()));
        }
    }

    private HootSearchCriteria createSearchCriteriaFromRequest(HttpServletRequest request)
    {
        String lastPost = request.getParameter("lastPostId");
        String quantity = request.getParameter("quantity");
        String tags     = request.getParameter("tags");
        String userId   = request.getParameter("userId");

        HootSearchCriteria searchCriteria = (HootSearchCriteria) ObjectManager.create(HootSearchCriteria.class);

        searchCriteria.lastPostId      = lastPost != null && !lastPost.equals("") ? Integer.valueOf(lastPost) : null;
        searchCriteria.defaultPageSize = quantity != null && !quantity.equals("") ? Integer.valueOf(quantity) : null;
        searchCriteria.tags            = tags != null && !tags.equals("") ? tags : null;
        searchCriteria.userId          = userId != null && !userId.equals("") ? Integer.valueOf(userId) : null;

        return searchCriteria;
    }

    private HootToHootDtoMapper getHootToHootDtoMapper()
    {
        return (HootToHootDtoMapper) ObjectManager.get(HootToHootDtoMapper.class);
    }
}
