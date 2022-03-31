package hoot.app.Servlets.Api.V1.Hoots;

import hoot.model.entities.Hoot;
import hoot.model.query.api.IsValidUserSession;
import hoot.model.repositories.HootRepository;
import hoot.system.Exception.CouldNotDeleteException;
import hoot.system.Exception.EntityNotFoundException;
import hoot.system.Exception.NotAllowedException;
import hoot.system.Security.AuthenticationRequired;
import hoot.system.objects.ObjectManager;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

@AuthenticationRequired
@WebServlet("/api/V1/hoot")
public class DeleteHootApiServlet extends AbstractHootApiServlet
{
    private HootRepository repository;

    @Override
    public void init() throws ServletException
    {
        super.init();

        this.repository = (HootRepository) ObjectManager.get(HootRepository.class);
    }

    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException
    {
        String hootId = request.getParameter("hootId");

        try {
            if (hootId == null || hootId.equals("")) {
                throw new CouldNotDeleteException("no id given");
            }
            int  idToDelete = Integer.parseInt(hootId);
            Hoot entity     = this.repository.getById(idToDelete);

            Integer currentUserId = (Integer) request
                    .getSession(true)
                    .getAttribute(IsValidUserSession.SESSION_USER_IDENTIFIER);

            if (!Objects.equals(entity.user.id, currentUserId)) {
                throw new NotAllowedException("User is not allowed to delete this hoot");
            }

            this.repository.delete(entity);

            this.sendResponse(response, HttpServletResponse.SC_OK, this.serialize("true"));
        } catch (CouldNotDeleteException | EntityNotFoundException | NotAllowedException e) {
            int httpStatus = HttpServletResponse.SC_NOT_ACCEPTABLE;
            this.sendResponse(response, httpStatus, this.serialize(e.getMessage()));
        }
    }
}
