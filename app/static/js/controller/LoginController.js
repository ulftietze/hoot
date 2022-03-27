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

            Api.login(login, this.#login, error => console.log(error));
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
                window.hootObjects.router.setRoute(window.hootObjects.router.getDefaultRoute());
            }, er => {});
        } else {
            console.log(response);
        }
    }
}