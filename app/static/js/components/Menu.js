class Menu
{
    static menuItems = [
        {target: '#route=' + Config.routeMapping.home.target, text: 'Heimat', icon: '#house-door'},
        {target: '#route=' + Config.routeMapping.explore.target, text: 'Entdecken', icon: '#binoculars'},
        {target: '#route=' + Config.routeMapping.search.target, text: 'Suche', icon: '#search'},
        {target: '#route=' + Config.routeMapping.profile.target + '&userId=currentUser', text: 'Profil', icon: '#person-fill'},
        {target: '#route=' + Config.routeMapping.monitor.target, text: 'Monitor', icon: '#graph-down'},
        {target: '#route=' + Config.routeMapping.swagger.target, text: 'OpenApi3.0', icon: '#braces'},
    ];

    static menuItemsLoggedOut = [
        {target: '#route=' + Config.routeMapping.explore.target, text: 'Entdecken', icon: '#binoculars'},
        {target: '#route=' + Config.routeMapping.monitor.target, text: 'Monitor', icon: '#graph-down'},
        {target: '#route=' + Config.routeMapping.swagger.target, text: 'OpenApi3.0', icon: '#braces'},
        {target: '#route=' + Config.routeMapping.login.target, text: 'Login', icon: '#door-open-fill'},
        {target: '#route=' + Config.routeMapping.register.target, text: 'Register', icon: '#ui-checks'},
    ];

    static buildMenu()
    {
        this.#buildDesktop(document.body, UserData.isLoggedIn() ? this.menuItems : this.menuItemsLoggedOut);
        this.#buildMobile(document.body, UserData.isLoggedIn() ? this.menuItems : this.menuItemsLoggedOut);
    }

    static rebuildMenu()
    {
        let menuItems = document.getElementsByClassName('main-menu');

        while (menuItems.length > 0) {
            menuItems[0].parentNode.removeChild(menuItems[0]);
        }

        this.buildMenu();
    }

    static #buildDesktop(mainElement, menuItems)
    {
        let wrapper = document.createElement('div');
        wrapper.classList.add(
            'd-flex',
            'flex-column',
            'flex-shrink-0',
            'p-3',
            'text-white',
            'bg-dark',
            'd-none',
            'd-lg-block',
            'main-menu',
            'position-fixed',
        );
        wrapper.style.width = '280px';

        wrapper.appendChild(this.#getIconDesktop());
        wrapper.appendChild(document.createElement('hr'));
        wrapper.appendChild(this.#buildMenuItemsDesktop(menuItems));
        wrapper.appendChild(document.createElement('hr'));
        wrapper.appendChild(this.#buildMenuFooterDesktop());

        mainElement.prepend(wrapper);
    }

    static #buildMenuItemsDesktop(menuItems)
    {
        let ul = document.createElement('ul');
        ul.classList.add('nav', 'nav-pills', 'flex-column', 'mb-auto', 'text-white');

        menuItems.forEach(menuItem => {
            let menuLi = document.createElement('li');
            menuLi.classList.add('nav-item');

            let menuLiLink  = document.createElement('a');
            menuLiLink.href = menuItem.target;
            menuLiLink.classList.add('nav-link', 'text-white');
            menuLiLink.setAttribute('aria-current', 'page');
            menuLiLink.appendChild(UtilComponent.createBootstrapSvg(menuItem.icon, 24, 24, 'bi', 'me-2'));

            let menuText       = document.createElement('span');
            menuText.innerText = menuItem.text;

            menuLiLink.appendChild(menuText);
            ul.appendChild(menuLiLink);
        });

        return ul;
    }

    static #getIconDesktop()
    {
        let iconLink  = document.createElement('a');
        iconLink.href = '#';
        iconLink.classList.add(
            'd-flex',
            'align-items-center',
            'mb-3',
            'mb-md-0',
            'me-md-auto',
            'text-primary',
            'text-decoration-none',
        );

        let spanHeading = document.createElement('span');
        spanHeading.classList.add('fs-1', 'font-heading');
        spanHeading.innerText = 'hoot';

        //iconLink.appendChild(UtilComponent.createBootstrapSvg('#bootstrap', 40, 32, 'bi', 'me-2'));
        iconLink.appendChild(UtilComponent.createSvg('static/image/moin.svg', 40, 50, 'bi', 'me-2'));
        iconLink.appendChild(spanHeading);

        return iconLink;
    }

    static #buildMenuFooterDesktop()
    {
        let dropdownDiv = document.createElement('div');

        if (!UserData.isLoggedIn()) {
            return dropdownDiv;
        }

        dropdownDiv.classList.add('dropdown');

        let dropdownLink = document.createElement('a');
        dropdownLink.classList.add(
            'd-flex',
            'align-items-center',
            'text-white',
            'text-decoration-none',
            'dropdown-toggle',
        );
        dropdownLink.id = 'dropdown-menu-profile';
        dropdownLink.setAttribute('data-bs-toggle', 'dropdown');
        dropdownLink.setAttribute('aria-expanded', 'false');

        let profileImage = document.createElement('img');
        profileImage.classList.add('rounded-circle', 'me-2');
        profileImage.src    = UserData.getProfileImage();
        profileImage.alt    = '';
        profileImage.width  = 32;
        profileImage.height = 32;

        let strong       = document.createElement('strong');
        strong.innerText = UserData.getUser()?.username;

        dropdownLink.appendChild(profileImage);
        dropdownLink.appendChild(strong);
        dropdownDiv.appendChild(dropdownLink);

        let dropdownList = document.createElement('ul');
        dropdownList.setAttribute('aria-labelledby', 'dropdown-menu-profile');
        dropdownList.classList.add('dropdown-menu', 'dropdown-menu-dark', 'text-small', 'shadow');

        dropdownList.appendChild(this.#createListLink(
            'drp-ite',
            'dropdown-item',
            '#route=' + Config.routeMapping.settings.target,
            'Einstellungen',
        ));
        dropdownList.appendChild(this.#createListLink(
            'drp-ite',
            'dropdown-item',
            '#route=' + Config.routeMapping.logout.target,
            'Logout',
        ));

        dropdownDiv.appendChild(dropdownList);

        return dropdownDiv;
    }

    static #buildMobile(mainElement, menuItems)
    {
        let wrapper = document.createElement('div');
        wrapper.classList.add(
            'd-flex',
            'flex-column',
            'flex-shrink-0',
            'text-white',
            'bg-dark',
            'd-lg-none',
            'main-menu',
            'position-fixed',
        );
        wrapper.style.width = '4.5rem';

        wrapper.appendChild(this.#getIconMobile());
        wrapper.appendChild(document.createElement('hr'));
        wrapper.appendChild(this.#buildMenuItemsMobile(menuItems));
        wrapper.appendChild(this.#buildMenuFooterMobile());

        mainElement.prepend(wrapper);
    }

    static #buildMenuItemsMobile(menuItems)
    {
        let ul = document.createElement('ul');
        ul.classList.add('nav', 'nav-pills', 'nav-flush', 'flex-column', 'mb-auto', 'text-center');

        menuItems.forEach(menuItem => {
            let menuLi = document.createElement('li');
            menuLi.classList.add('nav-item');

            let menuLiLink   = document.createElement('a');
            menuLiLink.href  = menuItem.target;
            menuLiLink.title = menuItem.text;
            menuLiLink.classList.add('nav-link', 'py-3', 'text-white');
            menuLiLink.setAttribute('data-bs-toggle', 'tooltip');
            menuLiLink.setAttribute('data-bs-placement', 'right');
            menuLiLink.appendChild(UtilComponent.createBootstrapSvg(menuItem.icon, 24, 24, 'bi', 'me-2'));
            ul.appendChild(menuLiLink);
        });

        return ul;
    }

    static #getIconMobile()
    {
        let iconLink  = document.createElement('a');
        iconLink.href = '#';
        iconLink.classList.add('d-block', 'p-3', 'link-light', 'text-decoration-none', 'text-primary');

        let spanHidden = document.createElement('span');
        spanHidden.classList.add('visually-hidden');
        spanHidden.innerText = 'Icon-only';

        //iconLink.appendChild(UtilComponent.createBootstrapSvg('#bootstrap', 40, 32, 'bi'));
        iconLink.appendChild(UtilComponent.createSvg('static/image/moin.svg', 40, 50, 'bi', 'me-2'));
        iconLink.appendChild(spanHidden);

        return iconLink;
    }

    static #buildMenuFooterMobile()
    {
        let dropdownDiv = document.createElement('div');

        if (!UserData.isLoggedIn()) {
            return dropdownDiv;
        }

        dropdownDiv.classList.add('dropdown', 'border-top');

        let dropdownLink  = document.createElement('a');
        dropdownLink.href = '#';
        dropdownLink.classList.add(
            'd-flex',
            'align-items-center',
            'justify-content-center',
            'p-3',
            'link-dark',
            'text-decoration-none',
            'dropdown-toggle',
        );
        dropdownLink.id = 'dropdown-menu-profile-mobile';
        dropdownLink.setAttribute('data-bs-toggle', 'dropdown');
        dropdownLink.setAttribute('aria-expanded', 'false');

        let profileImage = document.createElement('img');
        profileImage.classList.add('rounded-circle');
        profileImage.src    = UserData.getProfileImage();
        profileImage.alt    = UserData.getUser()?.username;
        profileImage.width  = 32;
        profileImage.height = 32;

        dropdownLink.appendChild(profileImage);
        dropdownDiv.appendChild(dropdownLink);

        let dropdownList = document.createElement('ul');
        dropdownList.setAttribute('aria-labelledby', 'dropdown-menu-profile-mobile');
        dropdownList.classList.add('dropdown-menu', 'text-small', 'shadow');
        dropdownList.appendChild(this.#createListLink(
            'drp-ite',
            'dropdown-item',
            '#route=' + Config.routeMapping.settings.target,
            'Einstellungen',
        ));
        dropdownList.appendChild(this.#createListLink(
            'drp-ite',
            'dropdown-item',
            '#route=' + Config.routeMapping.logout.target,
            'Logout',
        ));

        dropdownDiv.appendChild(dropdownList);

        return dropdownDiv;
    }

    static #createListLink(listClass, linkClass, linkHref, linkText)
    {
        let listItem = document.createElement('li');
        listItem.classList.add(listClass);

        let link = document.createElement('a');
        link.classList.add(linkClass);
        link.href      = linkHref;
        link.innerText = linkText;

        listItem.appendChild(link);

        return listItem;
    }
}