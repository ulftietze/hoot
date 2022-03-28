class LogoutController extends BaseController
{
    match(routeTarget)
    {
        return routeTarget === Config.routeMapping.logout.target;
    }

    execute()
    {
        Api.logout(response => {
            UserData.setUser(null);
            if (response === 'true') {
                Menu.rebuildMenu();
            } else {
                /** TODO: Show error message */
                console.log(response);
            }
        }, er => {
            /** TODO: Show error message */
            console.log(er);
        });
    }
}