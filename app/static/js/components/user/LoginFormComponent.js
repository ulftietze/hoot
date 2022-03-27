class LoginFormComponent
{
    /**
     * @param {function} onClickFunction
     * @returns {HTMLFormElement}
     */
    static createForm(onClickFunction)
    {
        let form = document.createElement('form');

        let divUsername = this.#createInputWithLabel('mb-3', 'username', 'Username', 'text');
        let divPassword = this.#createInputWithLabel('mb-3', 'password', 'Passwort', 'password');
        let buttonLogin = this.#createButton('submit', 'Login', onClickFunction, 'btn', 'btn-primary');

        form.append(divUsername);
        form.append(divPassword);
        form.append(buttonLogin);

        return form;
    }

    static #createButton(buttonType, buttonText, onClickFunction, ...buttonClasses)
    {
        let button  = document.createElement('button');
        button.type = buttonType;
        button.classList.add(...buttonClasses);
        button.textContent = buttonText;

        if (onClickFunction) {
            button.addEventListener('click', event => {
                event.preventDefault();
                onClickFunction(event);
            });
        }

        return button;
    }

    static #createInputWithLabel(wrapperClass, identifier, labelText, inputType)
    {
        let wrapper = document.createElement('div');
        wrapper.classList.add(wrapperClass);

        let label       = document.createElement('label');
        label.htmlFor   = identifier;
        label.innerText = labelText;
        label.classList.add('form-label');

        let input  = document.createElement('input');
        input.id   = identifier;
        input.type = inputType;
        input.classList.add('form-control');

        wrapper.append(label);
        wrapper.append(input);

        return wrapper;
    }
}