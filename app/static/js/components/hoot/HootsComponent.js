class HootsComponent
{
    /**
     * Prepend hoots to a given element
     *
     * @param {Hoots} hoots
     * @param {Element} element
     */
    static prependHootsToElement(hoots, element)
    {
        element.classList.add('container');
        hoots.hoots.forEach(hoot => {
            let el = document.querySelector('#' + element.id + ' > div[data-hoot-id="' + hoot.id + '"]');

            if (!el) {
                element.prepend(HootComponent.drawHoot(hoot));
            } else {
                el.replaceWith(HootComponent.drawHoot(hoot))
            }
        });
    }
}