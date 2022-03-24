class HomeController extends BaseController
{
    match(routeTarget)
    {
        return routeTarget === Config.routeMapping.home.target;
    }

    execute()
    {
        console.log(self);
    }
}