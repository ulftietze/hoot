package hoot.model.query.api;

import hoot.front.api.dto.authentication.LoginDTO;

public class IsValidLogin
{
    public boolean execute(LoginDTO login)
    {
        // TODO: Get Database Connection from Pool
        // TODO: Hash Password
        // TODO: Load User by Credentials
        // TODO: validate and return if user exists
        return true;
    }
}
