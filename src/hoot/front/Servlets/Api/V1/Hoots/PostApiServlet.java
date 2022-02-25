package hoot.front.Servlets.Api.V1.Hoots;

import hoot.front.Servlets.Api.V1.AbstractApiServlet;
import hoot.front.api.dto.hoot.PostDTO;
import hoot.model.entities.Post;
import hoot.model.mapper.dtoToEntity.hoot.PostDtoToPostMapper;
import hoot.model.repositories.HootRepository;
import hoot.system.Annotation.AuthenticationRequired;
import hoot.system.Exception.CouldNotSaveException;
import hoot.system.ObjectManager.ObjectManager;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@AuthenticationRequired
@WebServlet("/api/V1/hoot/post")
public class PostApiServlet extends AbstractApiServlet
{
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
    {
        PostDTO dto = (PostDTO) this.deserializeJsonRequestBody(request, PostDTO.class);

        HootRepository      repository = (HootRepository) ObjectManager.get(HootRepository.class);
        PostDtoToPostMapper mapper     = (PostDtoToPostMapper) ObjectManager.get(PostDtoToPostMapper.class);

        try {
            Post entity = (Post) mapper.map(dto);
            repository.save(entity);

            this.sendResponse(response, HttpServletResponse.SC_OK, this.serializeJsonResponseBody("created"));
        } catch (CouldNotSaveException e) {
            int httpStatus = HttpServletResponse.SC_NOT_ACCEPTABLE;
            this.sendResponse(response, httpStatus, this.serializeJsonResponseBody(e.getMessage()));
        }
    }
}
