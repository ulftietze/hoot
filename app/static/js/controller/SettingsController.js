class SettingsController extends BaseController
{
    match(routeTarget)
    {
        return routeTarget === Config.routeMapping.settings.target;
    }

    execute()
    {
        console.log(self);
    }
}