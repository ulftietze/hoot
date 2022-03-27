class LogoutController extends BaseController
{
    match(routeTarget)
    {
        return routeTarget === Config.routeMapping.logout.target;
    }

    execute()
    {
        console.log(self);
    }
}