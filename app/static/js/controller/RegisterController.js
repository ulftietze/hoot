class RegisterController extends BaseController
{
    match(routeTarget)
    {
        return routeTarget === Config.routeMapping.logout.target;
    }

    execute()
    {
        if (UserData.isLoggedIn()) {
            window.hootObjects.router.setRoute(window.hootObjects.router.getDefaultRoute());
        }

        let documentElement            = document.getElementById('content-container');
        documentElement.style.maxWidth = '520px';

        let form = RegisterFormComponent.createForm(event => {
            let register      = new Register();
            register.username = document.getElementById('username').value;
            register.password = document.getElementById('password').value;

            Api.register(register, this.#register, error => UtilComponent.createToast('Das hat leider nicht geklappt'));
        });
        form.classList.add('container', 'text-white');

        documentElement.appendChild(form);

        console.log(self);
    }

    #register(response)
    {
        if (response === 'Registered') {
            UtilComponent.createToast('Das hat geklappt.');
            window.hootObjects.router.setRoute(Config.routeMapping.login.target);
        } else {
            UtilComponent.createToast('Etwas ist schiefgelaufen.');
        }
    }
}