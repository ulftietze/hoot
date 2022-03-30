class UtilComponent
{
    static TOAST_CONTAINER_ID = 'toast-container';

    /**
     * @param {String} message
     */
    static createToast(message)
    {
        let toastContainer = document.getElementById(UtilComponent.TOAST_CONTAINER_ID);

        if (!toastContainer) {
            toastContainer    = document.createElement('div');
            toastContainer.id = UtilComponent.TOAST_CONTAINER_ID;
            toastContainer.classList.add('toast-container', 'position-fixed', 'top-0', 'end-0', 'p-3');
            document.body.appendChild(toastContainer);
        }

        let toast = document.createElement('div');
        toast.classList.add('toast', 'align-items-center', 'text-white', 'bg-primary', 'border-0', 'show');
        toast.setAttribute('role', 'alert');
        toast.setAttribute('aria-live', 'assertive');
        toast.setAttribute('aria-atomic', 'true');
        let toastInnerFlex = document.createElement('div');
        toastInnerFlex.classList.add('d-flex');
        let toastBody       = document.createElement('div');
        toastBody.classList.add('toast-body');
        toastBody.innerText = message;
        let toastClose      = document.createElement('button');
        toastClose.type     = 'button';
        toastClose.classList.add('btn-close', 'btn-close-white', 'me-2', 'm-auto');
        toastClose.setAttribute('data-bs-dismiss', 'toast');
        toastClose.setAttribute('aria-label', 'Close');

        toastInnerFlex.appendChild(toastBody);
        toastInnerFlex.appendChild(toastClose);
        toast.appendChild(toastInnerFlex);

        toastContainer.prepend(toast);
    }

    static createBootstrapSvg(identifier, width, height, ...cssClass)
    {
        let iconSvg = document.createElementNS('http://www.w3.org/2000/svg', 'svg');
        iconSvg.classList.add(...cssClass);
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

    static createSvg(url, width, height, ...cssClass)
    {
        let icon  = document.createElement('object');
        icon.data = url;
        icon.type = 'image/svg+xml';
        icon.classList.add(...cssClass);
        icon.width  = width;
        icon.height = height;

        return icon;

        let iconSvg = document.createElementNS('http://www.w3.org/2000/svg', 'svg');
        iconSvg.classList.add(...cssClass);
        iconSvg.setAttribute('width', width);
        iconSvg.setAttribute('height', height);

        let svgUse = document.createElementNS('http://www.w3.org/2000/svg', 'use');
        svgUse.setAttributeNS(
            'http://www.w3.org/1999/xlink',
            'xlink:href',
            url,
        );

        iconSvg.appendChild(svgUse);

        return iconSvg;
    }
}