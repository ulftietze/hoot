package hoot.front.Servlets.Api.V1.User;

import hoot.front.Servlets.Api.V1.AbstractApiServlet;
import hoot.front.api.dto.user.UserDTO;
import hoot.model.entities.User;
import hoot.model.mapper.dtoToEntity.UserDtoToUserMapper;
import hoot.model.mapper.entityToDto.UserToUserDtoMapper;
import hoot.model.repositories.UserRepository;
import hoot.system.Annotation.AuthenticationRequired;
import hoot.system.Exception.CouldNotSaveException;
import hoot.system.Exception.EntityNotFoundException;
import hoot.system.ObjectManager.ObjectManager;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@AuthenticationRequired
@WebServlet({"/api/V1/user", "/api/V1/user/me"})
public class UserApiServlet extends AbstractApiServlet
{
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
    {
        String requestedId = request.getParameter("id");

        if (requestedId == null || requestedId.equals("")) {
            int httpStatus = HttpServletResponse.SC_BAD_REQUEST;
            this.sendResponse(response, httpStatus, this.serializeJsonResponseBody("No ID given."));
            return;
        }

        UserRepository      repository = (UserRepository) ObjectManager.get(UserRepository.class);
        UserToUserDtoMapper mapper     = (UserToUserDtoMapper) ObjectManager.get(UserToUserDtoMapper.class);

        try {
            User    entity = repository.getById(Integer.parseInt(requestedId));
            UserDTO dto    = mapper.map(entity);

            this.sendResponse(response, HttpServletResponse.SC_OK, this.serializeJsonResponseBody(dto));
        } catch (EntityNotFoundException e) {
            int httpStatus = HttpServletResponse.SC_NOT_FOUND;
            this.sendResponse(response, httpStatus, this.serializeJsonResponseBody(e.getMessage()));
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
    {
        UserDTO dto = (UserDTO) this.deserializeJsonRequestBody(request, UserDTO.class);

        UserRepository      repository = (UserRepository) ObjectManager.get(UserRepository.class);
        UserDtoToUserMapper mapper     = (UserDtoToUserMapper) ObjectManager.get(UserDtoToUserMapper.class);

        try {
            User entity = mapper.map(dto);
            repository.save(entity);

            this.sendResponse(response, HttpServletResponse.SC_OK, this.serializeJsonResponseBody("saved"));
        } catch (CouldNotSaveException e) {
            int httpStatus = HttpServletResponse.SC_NOT_ACCEPTABLE;
            this.sendResponse(response, httpStatus, this.serializeJsonResponseBody(e.getMessage()));
        }
    }

    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
    {
        UserDTO dto = (UserDTO) this.deserializeJsonRequestBody(request, UserDTO.class);

        UserRepository      repository = (UserRepository) ObjectManager.get(UserRepository.class);
        UserDtoToUserMapper mapper     = (UserDtoToUserMapper) ObjectManager.get(UserDtoToUserMapper.class);

        try {
            User entity = mapper.map(dto);
            repository.save(entity);

            this.sendResponse(response, HttpServletResponse.SC_OK, this.serializeJsonResponseBody("updated"));
        } catch (CouldNotSaveException e) {
            int httpStatus = HttpServletResponse.SC_NOT_ACCEPTABLE;
            this.sendResponse(response, httpStatus, this.serializeJsonResponseBody(e.getMessage()));
        }
    }
}
