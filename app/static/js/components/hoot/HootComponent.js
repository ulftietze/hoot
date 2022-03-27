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
        let hootElement         = document.createElement('div');
        hootElement.id          = 'hoot-' + hoot.id;
        hootElement.style.width = '90%';
        hootElement.setAttribute('data-hoot-id', hoot.id.toString());
        hootElement.classList.add('card', 'mb-3', 'bg-secondary', 'text-white', 'border-secondary');

        let rowGutter = document.createElement('div');
        rowGutter.classList.add('row', 'no-gutters');

        let cardHead = document.createElement('div');
        cardHead.classList.add('col-2', 'm-2', 'pt-2');
        let image             = document.createElement('img');
        image.src             = hoot.user.imageUrl ?? Config.getDefaultProfileImage();
        image.alt             = 'Profile image';
        image.style.maxWidth  = '50px';
        image.style.maxHeight = '50px';
        image.classList.add('rounded-circle');
        cardHead.appendChild(image);

        let cardBody = document.createElement('div');
        cardBody.classList.add('col-8');
        let cardBodyContent = document.createElement('div');
        cardBodyContent.classList.add('card-body');
        let userHeadline = document.createElement('p');
        userHeadline.classList.add('text-monospace', 'text-muted');
        userHeadline.textContent = '@' + hoot.user.username;
        let content              = document.createElement('p');
        content.classList.add('card-text');
        content.textContent = hoot.content;

        cardBodyContent.appendChild(userHeadline);
        cardBodyContent.appendChild(content);
        cardBody.appendChild(cardBodyContent);

        rowGutter.appendChild(cardHead);
        rowGutter.appendChild(cardBody);

        hootElement.appendChild(rowGutter);

        return hootElement;
    }

}