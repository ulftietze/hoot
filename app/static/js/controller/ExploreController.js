class ExploreController extends BaseController
{
    match(routeTarget)
    {
        return routeTarget === Config.routeMapping.explore.target;
    }

    execute()
    {
        console.log(self);
    }
}