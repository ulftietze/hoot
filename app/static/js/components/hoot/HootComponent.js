class HootComponent
{
    static HOOT_INPUT_FIELD = 'input-create-hoot';

    static drawCreateHoot()
    {
        let card         = document.createElement('div');
        card.id          = 'create-hoot';
        card.style.width = '90%';
        card.classList.add('card', 'mb-3', 'bg-secondary', 'text-white', 'border-secondary');

        let rowGutter = document.createElement('div');
        rowGutter.classList.add('row', 'no-gutters');

        let cardHead = document.createElement('div');
        cardHead.classList.add('col-2', 'm-2', 'pt-2');
        let image             = document.createElement('img');
        image.src             = UserData.getUser()?.imageUrl ?? Config.getDefaultProfileImage();
        image.alt             = 'Profile image';
        image.style.maxWidth  = '50px';
        image.style.maxHeight = '50px';
        image.classList.add('rounded-circle');
        cardHead.appendChild(image);

        let cardBody = document.createElement('div');
        cardBody.classList.add('col-8');
        let cardBodyContent = document.createElement('div');
        cardBodyContent.classList.add('card-body');
        let input = document.createElement('input');
        input.id  = this.HOOT_INPUT_FIELD;
        input.classList.add('form-control', 'input-new-post', 'text-white');
        input.placeholder = 'Teile dich mit...';
        input.type        = 'textarea';

        let sendButton  = document.createElement('button');
        sendButton.id   = 'create-hoot-button';
        sendButton.type = 'button';
        sendButton.classList.add('btn', 'btn-primary', 'text-white');
        sendButton.addEventListener('click', this.#createPost.bind(this));
        let sendIcon = UtilComponent.createBootstrapSvg('#send', 20, 20, 'bi', 'me-2');
        sendButton.appendChild(sendIcon);

        cardBodyContent.appendChild(input);
        cardBodyContent.appendChild(sendButton);
        cardBody.appendChild(cardBodyContent);

        rowGutter.appendChild(cardHead);
        rowGutter.appendChild(cardBody);

        card.appendChild(rowGutter);

        return card;
    }

    static #createPost()
    {
        let inputField = document.getElementById(this.HOOT_INPUT_FIELD);

        if (!inputField.value) {
            inputField.classList.add('is-invalid');
            return;
        } else {
            inputField.classList.remove('is-invalid');
        }

        let hootPostSave          = new HootPostSave();
        hootPostSave.content      = inputField.value;
        hootPostSave.onlyFollower = false;

        Api.postHootPost(hootPostSave, post => {
            document.getElementById(this.HOOT_INPUT_FIELD).value = null;

            let contentContainer = document.getElementById('content-container');
            let drawnHoot        = this.drawHoot(post);
            contentContainer.insertBefore(drawnHoot, contentContainer.childNodes.item(1));
        }, er => console.log(er));
    }

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
        rowGutter.classList.add('row', 'no-gutters', 'g-0');

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