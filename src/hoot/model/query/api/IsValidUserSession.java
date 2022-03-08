package hoot.model.query.api;

import javax.servlet.http.HttpSession;

public class IsValidUserSession
{
    public static String SESSION_USER_IDENTIFIER = "userId";

    public boolean execute(HttpSession session)
    {
        return session != null && session.getAttribute(SESSION_USER_IDENTIFIER) != null;
    }
}
