package hoot.front.Servlets.Api.V1.Hoots;

import hoot.front.Servlets.Api.V1.AbstractApiServlet;
import hoot.model.entities.Post;
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
        HootRepository repository = (HootRepository) ObjectManager.get(HootRepository.class);
        Post           entity     = (Post) this.deserializeJsonRequestBody(request, Post.class);

        try {
            repository.save(entity);

            this.sendResponse(response, HttpServletResponse.SC_OK, this.serialize("created"));
        } catch (CouldNotSaveException e) {
            int httpStatus = HttpServletResponse.SC_NOT_ACCEPTABLE;
            this.sendResponse(response, httpStatus, this.serialize(e.getMessage()));
        }
    }
}
