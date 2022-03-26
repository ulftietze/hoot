class HootComponent
{
    /**
     * Create hoot element
     *
     * @param {Image|Post|Comment} hoot
     * @returns {HTMLDivElement}
     */
    static drawHoot(hoot)
    {
        let hootElement = document.createElement('div');
        hootElement.id = 'hoot-' + hoot.id;
        hootElement.setAttribute('data-hoot-id', hoot.id.toString());
        hootElement.innerHTML = '<p>' + hoot.content + '</p>';
        hootElement.classList.add('row');

        return hootElement;
    }
}