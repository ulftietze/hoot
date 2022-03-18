package hoot.app.Servlets.Api.V1;

import hoot.system.Filesystem.MediaFileHandler;
import hoot.system.objects.ObjectManager;
import hoot.system.Serializer.Serializer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public abstract class AbstractApiServlet extends HttpServlet
{
    private MediaFileHandler mediaFileHandler;

    @Override
    public void init() throws ServletException
    {
        super.init();

        this.mediaFileHandler = (MediaFileHandler) ObjectManager.get(MediaFileHandler.class);
    }

    /**
     * TODO: Documentation
     *
     * @param response
     * @param status
     * @param jsonBodyResponse
     * @throws IOException
     */
    protected void sendResponse(HttpServletResponse response, int status, String jsonBodyResponse) throws IOException
    {
        response.setStatus(status);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        PrintWriter out = response.getWriter();
        out.println(jsonBodyResponse);
    }

    /**
     * @param request   The incoming http servlet request
     * @param targetDTO The Output DTO from which the input body json is serialized
     * @return Object
     * @throws IOException Thrown while reading input stream
     */
    protected Object deserializeJsonRequestBody(HttpServletRequest request, Class<?> targetDTO) throws IOException
    {
        Serializer serializer = (Serializer) ObjectManager.get(Serializer.class);
        return serializer.deserializeJsonRequestBody(request, targetDTO);
    }

    /**
     * @param toSerialize The DTO to Serialize
     * @return the serialized json string
     */
    protected String serialize(Object toSerialize)
    {
        Serializer serializer = (Serializer) ObjectManager.get(Serializer.class);
        return serializer.serialize(toSerialize);
    }

    protected void saveImage(String filepath, String base64E)
    {
        if (filepath == null || filepath.equals("") || base64E == null || base64E.equals("")) {
            return;
        }

        String relativePath;
        String imageName;

        if (filepath.contains("/")) {
            relativePath = filepath.substring(1, filepath.lastIndexOf("/"));
            imageName    = filepath.substring(0, filepath.length() - 1).substring(filepath.lastIndexOf("/") + 1);
        } else {
            relativePath = null;
            imageName    = filepath.substring(0, filepath.length() - 1).substring(1);
        }

        this.mediaFileHandler.saveBase64Image(imageName, relativePath, base64E);
    }
}

