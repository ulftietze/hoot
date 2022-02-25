package hoot.front.Servlets.Api.V1.Hoots;

import hoot.front.Servlets.Api.V1.AbstractApiServlet;
import hoot.front.api.dto.hoot.ImageDTO;
import hoot.model.entities.Image;
import hoot.model.mapper.dtoToEntity.hoot.ImageDtoToImageMapper;
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
@WebServlet("/api/V1/hoot/image")
public class ImageApiServlet extends AbstractApiServlet
{
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
    {
        ImageDTO dto = (ImageDTO) this.deserializeJsonRequestBody(request, ImageDTO.class);

        HootRepository        repository = (HootRepository) ObjectManager.get(HootRepository.class);
        ImageDtoToImageMapper mapper     = (ImageDtoToImageMapper) ObjectManager.get(ImageDtoToImageMapper.class);

        try {
            Image imageEntity = (Image) mapper.map(dto);
            repository.save(imageEntity);

            this.sendResponse(response, HttpServletResponse.SC_OK, this.serializeJsonResponseBody("created"));
        } catch (CouldNotSaveException e) {
            int httpStatus = HttpServletResponse.SC_NOT_ACCEPTABLE;
            this.sendResponse(response, httpStatus, this.serializeJsonResponseBody(e.getMessage()));
        }
    }
}
