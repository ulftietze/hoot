class BaseController
{
    match(routeTarget)
    {
        return false;
    }

    execute()
    {
        throw new ControllerException('Base Controller should not be called directly');
    }
}