class ProfileController extends BaseController
{
    match(routeTarget)
    {
        return routeTarget === Config.routeMapping.profile.target;
    }

    execute()
    {
        if (!UserData.isLoggedIn()) {
            return;
        }

        let component                  = ProfileComponent.drawProfileHead(UserData.getUser());
        let documentElement            = document.getElementById('content-container');
        documentElement.style.maxWidth = '720px';
        documentElement.classList.add('container');

        documentElement.appendChild(component);
    }
}