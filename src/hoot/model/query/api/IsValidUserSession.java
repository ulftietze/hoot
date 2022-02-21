package hoot.model.query.api;

import javax.servlet.http.HttpSession;

public class IsValidUserSession
{
    public boolean execute(HttpSession session)
    {
        return session != null && session.getAttribute("user-id") != null;
    }
}
