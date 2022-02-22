package hoot.front.Servlets;

import hoot.model.entities.User;
import hoot.model.repositories.UserRepository;
import hoot.system.Annotation.AuthenticationRequired;
import hoot.system.Exception.ConnectionFailedException;
import hoot.system.Exception.EntityNotFoundException;
import hoot.system.ObjectManager.ObjectManager;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import javax.xml.crypto.Data;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@AuthenticationRequired(authenticationRequired = false)
@WebServlet("/test")
public class TestServlet extends HttpServlet
{
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException
    {
        PrintWriter out = response.getWriter();
        response.setContentType("text/html");

        response.setContentType("text/html");
        out.println("<!doctype html><html>");
        out.println("<head> <meta charset='utf-8'>");
        out.println("<title>JDBC Test</title> </head>");
        out.println("<body>");

        User user = null;

        try {
            DataSource ds = (DataSource) ObjectManager.get(DataSource.class);
            Connection connection = ds.getConnection();

            PreparedStatement pss = connection.prepareStatement("select id, username, imagePath from User where id = ?");
            pss.setInt(1, 1);
            ResultSet rs = pss.executeQuery();

            rs.next(); // will throw SQLException if result set is empty
            if (!rs.isLast()) {
                throw new EntityNotFoundException("User");
            }

            user.id = rs.getObject()



        } catch (EntityNotFoundException | ConnectionFailedException | SQLException e) {
            e.printStackTrace(out);
        }

        if (user != null) {
            out.println("ID: " + user.id + "<br>" + "UserName: " + user.username + "<br>");
        } else {
            out.println("DB connection failed or User not found.");
        }

        out.println("</body>");
        out.println("</html>");
    }
}
