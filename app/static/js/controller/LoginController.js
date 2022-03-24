class LoginController extends BaseController
{
    match(routeTarget)
    {
        return routeTarget === Config.routeMapping.login.target;
    }

    execute()
    {
        console.log(self);
    }
}