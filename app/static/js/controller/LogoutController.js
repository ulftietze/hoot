class LogoutController extends BaseController
{
    match(routeTarget)
    {
        return routeTarget === Config.routeMapping.logout.target;
    }

    execute()
    {
        Api.logout(response => {
            if (response === 'true') {
                UserData.setUser(null);
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