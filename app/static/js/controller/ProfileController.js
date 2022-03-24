class ProfileController extends BaseController
{
    match(routeTarget)
    {
        return routeTarget === Config.routeMapping.profile.target;
    }

    execute()
    {
        console.log(self);
    }
}