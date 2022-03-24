class Api
{
    /**
     * @param {LoginDTO} component
     * @param {function} promiseOnSuccess
     * @param {function} promiseOnError
     */
    static login(component, promiseOnSuccess, promiseOnError)
    {
        // TODO: Set Cookie for Auth
        fetch(Config.getApiUrl() + 'login', {
            method: 'POST', body: component, headers: {
                'Accept': 'application/json', 'Content-Type': 'application/json; charset=UTF-8',
            },
        }).then(promiseOnSuccess).catch(promiseOnError);
    }

    /**
     * @param {function} promiseOnSuccess
     * @param {function} promiseOnError
     */
    static logout(promiseOnSuccess, promiseOnError)
    {
        fetch(Config.getApiUrl() + 'logout', {
            method: 'POST', headers: {
                'Accept': 'application/json', 'Content-Type': 'application/json; charset=UTF-8',
            },
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
        fetch(Config.getApiUrl() + 'register', {
            method: 'POST', body: component, headers: {
                'Accept': 'application/json', 'Content-Type': 'application/json; charset=UTF-8',
            },
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
            method: 'GET', headers: {
                'Accept': 'application/json', 'Content-Type': 'application/json; charset=UTF-8',
            },
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
            method: 'GET', headers: {
                'Accept': 'application/json', 'Content-Type': 'application/json; charset=UTF-8',
            },
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
            method: 'PUT', body: component, headers: {
                'Accept': 'application/json', 'Content-Type': 'application/json; charset=UTF-8',
            },
        }).then(promiseOnSuccess).catch(promiseOnError);
    }

    /**
     * @param {int}      userIdToFollow
     * @param {function} promiseOnSuccess
     * @param {function} promiseOnError
     */
    static followUser(userIdToFollow, promiseOnSuccess, promiseOnError)
    {
        // TODO: Set Cookie for Auth
        fetch(Config.getApiUrl() + 'user/me/follow', {
            method: 'POST', body: userIdToFollow, headers: {
                'Accept': 'application/json', 'Content-Type': 'application/json; charset=UTF-8',
            },
        }).then(promiseOnSuccess).catch(promiseOnError);
    }

    /**
     * @param {int}      userIdToUnfollow
     * @param {function} promiseOnSuccess
     * @param {function} promiseOnError
     */
    static unfollowUser(userIdToUnfollow, promiseOnSuccess, promiseOnError)
    {
        // TODO: Set Cookie for Auth
        fetch(Config.getApiUrl() + 'user/me/unfollow', {
            method: 'POST', body: userIdToUnfollow, headers: {
                'Accept': 'application/json', 'Content-Type': 'application/json; charset=UTF-8',
            },
        }).then(promiseOnSuccess).catch(promiseOnError);
    }

    /**
     * @param {int}      userId
     * @param {int}      lastUserId
     * @param {int}      quantity
     * @param {function} promiseOnSuccess
     * @param {function} promiseOnError
     */
    static getUserFollower(userId, lastUserId, quantity, promiseOnSuccess, promiseOnError)
    {
        let url = Config.getApiUrl() + 'user/follower?userId=' + userId + '&quantity=' + quantity ? quantity : '50';
        url += lastUserId ? '&lastUserId=' + lastUserId : '';

        fetch(url, {
            method: 'GET', headers: {
                'Accept': 'application/json', 'Content-Type': 'application/json; charset=UTF-8',
            },
        }).then(promiseOnSuccess).catch(promiseOnError);
    }

    /**
     * @param {int}      userId
     * @param {int}      lastUserId
     * @param {int}      quantity
     * @param {function} promiseOnSuccess
     * @param {function} promiseOnError
     */
    static getUserFollows(userId, lastUserId, quantity, promiseOnSuccess, promiseOnError)
    {
        let url = Config.getApiUrl() + 'user/follows?userId=' + userId + '&quantity=' + quantity ? quantity : '50';
        url += lastUserId ? '&lastUserId=' + lastUserId : '';

        fetch(url, {
            method: 'GET', headers: {
                'Accept': 'application/json', 'Content-Type': 'application/json; charset=UTF-8',
            },
        }).then(promiseOnSuccess).catch(promiseOnError);
    }

    /**
     * @param {int}      lastPostId
     * @param {int}      quantity
     * @param {String}   tags
     * @param {function} promiseOnSuccess
     * @param {function} promiseOnError
     */
    static getHootTimelineGlobal(lastPostId, quantity, tags, promiseOnSuccess, promiseOnError)
    {
        let url = Config.getApiUrl() + 'hoot/timeline/global?lastPostId=' + lastPostId + '&quantity=' + quantity;
        url += tags ? '&tags=' + tags : '';

        fetch(url, {
            method: 'GET', headers: {
                'Accept': 'application/json', 'Content-Type': 'application/json; charset=UTF-8',
            },
        }).then(promiseOnSuccess).catch(promiseOnError);
    }

    /**
     * @param {int}      lastPostId
     * @param {int}      quantity
     * @param {String}   tags
     * @param {function} promiseOnSuccess
     * @param {function} promiseOnError
     */
    static getHootTimelineMine(lastPostId, quantity, tags, promiseOnSuccess, promiseOnError)
    {
        let url = Config.getApiUrl() + 'hoot/timeline/mine?lastPostId=' + lastPostId + '&quantity=' + quantity;
        url += tags ? '&tags=' + tags : '';

        // TODO: Set Cookie for Auth
        fetch(url, {
            method: 'GET', headers: {
                'Accept': 'application/json', 'Content-Type': 'application/json; charset=UTF-8',
            },
        }).then(promiseOnSuccess).catch(promiseOnError);
    }

    /**
     * @param {int}         lastPostId
     * @param {int}         quantity
     * @param {String|null} tags
     * @param {int|null}    userId
     * @param {function}    promiseOnSuccess
     * @param {function}    promiseOnError
     */
    static getHootSearch(lastPostId, quantity, tags, userId, promiseOnSuccess, promiseOnError)
    {
        let url = Config.getApiUrl() + 'hoot/Search?lastPostId=' + lastPostId + '&quantity=' + quantity;

        url += tags ? '&tags=' + tags : '';
        url += userId ? '&userId' + userId : '';

        // TODO: Set Cookie for Auth
        fetch(url, {
            method: 'GET', headers: {
                'Accept': 'application/json', 'Content-Type': 'application/json; charset=UTF-8',
            },
        }).then(promiseOnSuccess).catch(promiseOnError);
    }

    /**
     * @param {PostDTO}  component
     * @param {function} promiseOnSuccess
     * @param {function} promiseOnError
     */
    static postHootPost(component, promiseOnSuccess, promiseOnError)
    {
        // TODO: Set Cookie for Auth
        fetch(Config.getApiUrl() + 'hoot/post', {
            method: 'POST', body: component, headers: {
                'Accept': 'application/json', 'Content-Type': 'application/json; charset=UTF-8',
            },
        }).then(promiseOnSuccess).catch(promiseOnError);
    }

    /**
     * @param {CommentDTO} component
     * @param {function}   promiseOnSuccess
     * @param {function}   promiseOnError
     */
    static postHootComment(component, promiseOnSuccess, promiseOnError)
    {
        // TODO: Set Cookie for Auth
        fetch(Config.getApiUrl() + 'hoot/comment', {
            method: 'POST', body: component, headers: {
                'Accept': 'application/json', 'Content-Type': 'application/json; charset=UTF-8',
            },
        }).then(promiseOnSuccess).catch(promiseOnError);
    }

    /**
     * @param {ImageDTO} component
     * @param {function} promiseOnSuccess
     * @param {function} promiseOnError
     */
    static postHootImage(component, promiseOnSuccess, promiseOnError)
    {
        // TODO: Set Cookie for Auth
        fetch(Config.getApiUrl() + 'hoot/image', {
            method: 'POST', body: component, headers: {
                'Accept': 'application/json', 'Content-Type': 'application/json; charset=UTF-8',
            },
        }).then(response => response.json().then(promiseOnSuccess)).catch(promiseOnError);
    }

    /**
     * @param {ReactionDTO} component
     * @param {function}    promiseOnSuccess
     * @param {function}    promiseOnError
     */
    static postHootReaction(component, promiseOnSuccess, promiseOnError)
    {
        // TODO: Set Cookie for Auth
        fetch(Config.getApiUrl() + 'hoot/me/reaction', {
            method: 'POST', body: component, headers: {
                'Accept': 'application/json', 'Content-Type': 'application/json; charset=UTF-8',
            },
        }).then(response => response.json().then(promiseOnSuccess)).catch(promiseOnError);
    }

    /**
     * @param {ReactionDTO} component
     * @param {function}    promiseOnSuccess
     * @param {function}    promiseOnError
     */
    static deleteHootReaction(component, promiseOnSuccess, promiseOnError)
    {
        // TODO: Set Cookie for Auth
        fetch(Config.getApiUrl() + 'hoot/me/reaction', {
            method: 'DELETE', body: component, headers: {
                'Accept': 'application/json', 'Content-Type': 'application/json; charset=UTF-8',
            },
        }).then(response => response.json().then(promiseOnSuccess)).catch(promiseOnError);
    }

    /**
     * @param {function} promiseOnSuccess
     * @param {function} promiseOnError
     */
    static getSystemMonitorGraphs(promiseOnSuccess, promiseOnError)
    {
        // TODO: Set Cookie for Auth
        fetch(Config.getApiUrl() + 'system/monitor/diagrams', {
            method: 'GET', headers: {
                'Accept': 'application/json', 'Content-Type': 'application/json; charset=UTF-8',
            },
        }).then(response => response.json().then(promiseOnSuccess)).catch(promiseOnError);
    }
}