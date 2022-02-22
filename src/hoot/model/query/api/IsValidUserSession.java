package hoot.model.query.api;

import hoot.front.Servlets.Api.V1.Authentication.LoginApiServlet;

import javax.servlet.http.HttpSession;

public class IsValidUserSession
{
    public boolean execute(HttpSession session)
    {
        return session != null && session.getAttribute(LoginApiServlet.SESSION_USER_IDENTIFIER) != null;
    }
}
