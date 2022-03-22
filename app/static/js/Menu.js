class Menu
{
    static menuItems = [
        {target: '#', text: 'Heimat', icon: '#house-door'},
        {target: '#explore', text: 'Entdecken', icon: '#binoculars'},
        {target: '#search', text: 'Suche', icon: '#search'},
        {target: '#profile', text: 'Profil', icon: '#person-fill'},
        {target: '#monitor', text: 'Monitor', icon: '#graph-down'},
        {target: '#swagger', text: 'OpenApi3.0', icon: '#braces'},
    ];

    static menuItemsLoggedOut = [
        {target: '#explore', text: 'Entdecken', icon: '#binoculars'},
        {target: '#monitor', text: 'Monitor', icon: '#graph-down'},
        {target: '#swagger', text: 'OpenApi3.0', icon: '#braces'},
    ];

    static buildMenu()
    {
        let main = document.createElement('main');

        this.#buildDesktop(main);
        this.#buildMobile(main);

        document.body.prepend(main);
    }

    static #buildDesktop(mainElement)
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
        );
        wrapper.style.width = '280px';

        wrapper.appendChild(this.#getIconDesktop());
        wrapper.appendChild(document.createElement('hr'));

        let ul = document.createElement('ul');
        ul.classList.add('nav', 'nav-pills', 'flex-column', 'mb-auto', 'text-white');

        this.menuItems.forEach(menuItem => {
            let menuLi = document.createElement('li');
            menuLi.classList.add('nav-item');

            let menuLiLink = document.createElement('a');
            menuLiLink.href = menuItem.target;
            menuLiLink.classList.add('nav-link', 'text-white');
            menuLiLink.setAttribute('aria-current', 'page');
            menuLiLink.appendChild(this.#createBootstrapSvg(menuItem.icon, 18, 18));

            let menuText = document.createElement('span');
            menuText.innerText = menuItem.text;

            menuLiLink.appendChild(menuText);
            ul.appendChild(menuLiLink);
        });

        wrapper.appendChild(ul);

        mainElement.appendChild(wrapper);
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

        iconLink.appendChild(this.#createBootstrapSvg('#bootstrap', 40, 32));
        iconLink.appendChild(spanHeading);

        return iconLink;
    }

    static #buildMobile(mainElement)
    {

    }

    static #createBootstrapSvg(identifier, width, height)
    {
        let iconSvg = document.createElementNS('http://www.w3.org/2000/svg', 'svg');
        iconSvg.classList.add('bi', 'me-2');
        iconSvg.setAttribute('width', width);
        iconSvg.setAttribute('height', height);

        let svgUse = document.createElementNS('http://www.w3.org/2000/svg', 'use');
        svgUse.setAttributeNS(
            'http://www.w3.org/1999/xlink',
            'xlink:href',
            'static/icons/bootstrap-icons.svg' + identifier,
        );

        iconSvg.appendChild(svgUse);

        return iconSvg;
    }
}