package hoot.front.Servlets.Api.V1.Hoots;

import hoot.front.Servlets.Api.V1.AbstractApiServlet;
import hoot.front.api.dto.hoot.CommentDTO;
import hoot.model.entities.Comment;
import hoot.model.mapper.dtoToEntity.hoot.CommentDtoToCommentMapper;
import hoot.model.repositories.HootRepository;
import hoot.system.Annotation.AuthenticationRequired;
import hoot.system.Exception.CouldNotMapException;
import hoot.system.Exception.CouldNotSaveException;
import hoot.system.ObjectManager.ObjectManager;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@AuthenticationRequired
@WebServlet("/api/V1/hoot/comment")
public class CommentApiServlet extends AbstractApiServlet
{
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException
    {
        CommentDTO dto = (CommentDTO) this.deserializeJsonRequestBody(request, CommentDTO.class);

        HootRepository            repository = (HootRepository) ObjectManager.get(HootRepository.class);
        CommentDtoToCommentMapper mapper     = this.getCommentDtoToCommentMapper();

        try {
            Comment comment = (Comment) mapper.map(dto);
            repository.save(comment);

            this.sendResponse(response, HttpServletResponse.SC_OK, this.serializeJsonResponseBody("created"));
        } catch (CouldNotSaveException | CouldNotMapException e) {
            int httpStatus = HttpServletResponse.SC_NOT_ACCEPTABLE;
            this.sendResponse(response, httpStatus, this.serializeJsonResponseBody(e.getMessage()));
        }
    }

    private CommentDtoToCommentMapper getCommentDtoToCommentMapper()
    {
        return (CommentDtoToCommentMapper) ObjectManager.get(CommentDtoToCommentMapper.class);
    }
}
