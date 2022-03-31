class LoginController extends BaseController
{
    match(routeTarget)
    {
        return routeTarget === Config.routeMapping.login.target;
    }

    execute()
    {
        if (UserData.isLoggedIn()) {
            window.hootObjects.router.setRoute(window.hootObjects.router.getDefaultRoute());
        }

        let documentElement            = document.getElementById('content-container');
        documentElement.style.maxWidth = '520px';

        let form = LoginFormComponent.createForm(event => {
            let login      = new Login();
            login.username = document.getElementById('username').value;
            login.password = document.getElementById('password').value;

            Api.login(login, this.#login, error => UtilComponent.createToast('Das hat leider nicht geklappt'));
        });
        form.classList.add('container', 'text-white');

        documentElement.appendChild(form);

        console.log(self);
    }

    #login(response)
    {
        if (response === 'success') {
            console.log('Logged in');
            Api.getUserMe(user => {
                UserData.setUser(user);
                Menu.rebuildMenu();
                UtilComponent.createToast('Moin');
                window.hootObjects.router.setRoute(window.hootObjects.router.getDefaultRoute());
            }, er => UtilComponent.createToast('Etwas ist ziemlich schiefgelaufen.'));
        } else {
            UtilComponent.createToast('Falscher Login');
        }
    }
}