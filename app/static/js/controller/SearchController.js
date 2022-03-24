class SearchController extends BaseController
{
    match(routeTarget)
    {
        return routeTarget === Config.routeMapping.search.target;
    }

    execute()
    {
        console.log(self);
    }
}