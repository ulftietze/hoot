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
            Menu.rebuildMenu();
            if (response === 'true') {
                UtilComponent.createToast('Tschüss');
                window.hootObjects.router.setRoute(window.hootObjects.router.getDefaultRoute());
            } else {
                /** TODO: Show error message */
                UtilComponent.createToast('Das hat nicht geklappt.');
                console.log(response);
            }
        }, er => {
            /** TODO: Show error message */
            console.log(er);
            UserData.setUser(null);
            UtilComponent.createToast('Das hat nicht geklappt.');
        });
    }
}