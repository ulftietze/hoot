package hoot.front.Servlets.Api.V1;

import hoot.system.Filesystem.MediaFileHandler;
import hoot.system.Logger.LoggerInterface;
import hoot.system.ObjectManager.ObjectManager;
import hoot.system.Serializer.RequestSerializer;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public abstract class AbstractApiServlet extends HttpServlet
{
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
        RequestSerializer requestSerializer = (RequestSerializer) ObjectManager.get(RequestSerializer.class);
        return requestSerializer.deserializeJsonRequestBody(request, targetDTO);
    }

    /**
     * @param toSerialize The DTO to Serialize
     * @return the serialized json string
     */
    protected String serialize(Object toSerialize)
    {
        RequestSerializer requestSerializer = (RequestSerializer) ObjectManager.get(RequestSerializer.class);
        return requestSerializer.serialize(toSerialize);
    }

    protected void saveImage(String relativeFilename, String base64E)
    {
        if (relativeFilename == null || relativeFilename.equals("") || base64E == null || base64E.equals("")) {
            return;
        }
        String imageName;
        String relativePath;
        LoggerInterface l = (LoggerInterface) ObjectManager.get(LoggerInterface.class);
        if (relativeFilename.contains("/")) {
            imageName    = relativeFilename.substring(0, relativeFilename.length() -1).substring(relativeFilename.lastIndexOf("/") + 1);
            relativePath = relativeFilename.substring(0, relativeFilename.lastIndexOf("/")).substring(1);
        } else {
            imageName = relativeFilename.substring(0, relativeFilename.length() - 1 ).substring(1);
            relativePath = null;
        }

        MediaFileHandler mediaFileHandler = (MediaFileHandler) ObjectManager.get(MediaFileHandler.class);
        mediaFileHandler.saveMedia(imageName, relativePath, base64E);
    }
}
