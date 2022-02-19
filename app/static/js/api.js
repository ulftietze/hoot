class Api
{
    /**
     * @param {LoginDTO} component
     * @param {function}       promiseOnSuccess
     * @param {function}       promiseOnError
     */
    static login(component, promiseOnSuccess, promiseOnError)
    {
        // TODO: Set Cookie for Auth
        fetch('api/V1/login', {
            method: 'POST',
            body: component,
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json; charset=UTF-8'
            }
        }).then(promiseOnSuccess).catch(promiseOnError);
    }

    /**
     * @param {RegisterDTO} component
     * @param {function}    promiseOnSuccess
     * @param {function}    promiseOnError
     */
    static register(component, promiseOnSuccess, promiseOnError)
    {
        // TODO: Set Cookie for Auth
        fetch('api/V1/login', {
            method: 'POST',
            body: component,
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json; charset=UTF-8'
            }
        }).then(promiseOnSuccess).catch(promiseOnError);
    }

    /**
     * @param {int}      userId
     * @param {function} promiseOnSuccess
     * @param {function} promiseOnError
     */
    static getUser(userId, promiseOnSuccess, promiseOnError)
    {
        // TODO: Set Cookie for Auth
        // TODO: Check for easier query params
        fetch('/api/V1/user?id=' + userId, {
            method: 'GET',
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json; charset=UTF-8'
            }
        }).then(promiseOnSuccess).catch(promiseOnError);
    }

    /**
     * @param {function} promiseOnSuccess
     * @param {function} promiseOnError
     */
    static getUserMe(promiseOnSuccess, promiseOnError)
    {
        // TODO: Set Cookie for Auth
        fetch('/api/V1/user/me', {
            method: 'GET',
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json; charset=UTF-8'
            }
        }).then(promiseOnSuccess).catch(promiseOnError);
    }

    /**
     * @param {UserDTO}  component
     * @param {function} promiseOnSuccess
     * @param {function} promiseOnError
     */
    static putUserMe(component, promiseOnSuccess, promiseOnError)
    {
        // TODO: Set Cookie for Auth
        fetch('/api/V1/user/me', {
            method: 'PUT',
            body: component,
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json; charset=UTF-8'
            }
        }).then(promiseOnSuccess).catch(promiseOnError);
    }

    /**
     * @param {int}      lastPostId
     * @param {int}      quantity
     * @param {function} promiseOnSuccess
     * @param {function} promiseOnError
     */
    static getHootTimelineGlobal(lastPostId, quantity, promiseOnSuccess, promiseOnError)
    {
        fetch('/api/V1/hoot/timeline/global?lastPostId=' + lastPostId + "&quantity=" + quantity, {
            method: 'GET',
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json; charset=UTF-8'
            }
        }).then(promiseOnSuccess).catch(promiseOnError);
    }

    /**
     * @param {int}      lastPostId
     * @param {int}      quantity
     * @param {function} promiseOnSuccess
     * @param {function} promiseOnError
     */
    static getHootTimelineMine(lastPostId, quantity, promiseOnSuccess, promiseOnError)
    {
        // TODO: Set Cookie for Auth
        fetch('/api/V1/hoot/timeline/mine?lastPostId=' + lastPostId + "&quantity=" + quantity, {
            method: 'GET',
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json; charset=UTF-8'
            }
        }).then(promiseOnSuccess).catch(promiseOnError);
    }

    /**
     * @param {int}      lastPostId
     * @param {int}      quantity
     * @param {String}   tags
     * @param {int|null} userId
     * @param {function} promiseOnSuccess
     * @param {function} promiseOnError
     */
    static getHootSearch(lastPostId, quantity, tags , userId,  promiseOnSuccess, promiseOnError)
    {
        let url = '/api/V1/hoot/Search?lastPostId=' + lastPostId + "&quantity=" + quantity;

        if (tags !== "") {
            url += "&tags=" + tags;
        }

        if (userId !== null) {
            url += "&userId" + userId;
        }

        // TODO: Set Cookie for Auth
        fetch( url, {
            method: 'GET',
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json; charset=UTF-8'
            }
        }).then(promiseOnSuccess).catch(promiseOnError);
    }

    /**
     * @param {PostDTO}  component
     * @param {function} promiseOnSuccess
     * @param {function} promiseOnError
     */
    static postHootPost(component,  promiseOnSuccess, promiseOnError)
    {
        // TODO: Set Cookie for Auth
        fetch('/api/V1/hoot/post', {
            method: 'POST',
            body: component,
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json; charset=UTF-8'
            }
        }).then(promiseOnSuccess).catch(promiseOnError);
    }

    /**
     * @param {CommentDTO}  component
     * @param {function} promiseOnSuccess
     * @param {function} promiseOnError
     */
    static postHootComment(component,  promiseOnSuccess, promiseOnError)
    {
        // TODO: Set Cookie for Auth
        fetch('/api/V1/hoot/comment', {
            method: 'POST',
            body: component,
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json; charset=UTF-8'
            }
        }).then(promiseOnSuccess).catch(promiseOnError);
    }

    /**
     * @param {ImageDTO}  component
     * @param {function} promiseOnSuccess
     * @param {function} promiseOnError
     */
    static postHootImage(component,  promiseOnSuccess, promiseOnError)
    {
        // TODO: Set Cookie for Auth
        fetch('/api/V1/hoot/image', {
            method: 'POST',
            body: component,
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json; charset=UTF-8'
            }
        }).then(promiseOnSuccess).catch(promiseOnError);
    }
}