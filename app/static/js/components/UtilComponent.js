class UtilComponent
{
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
}