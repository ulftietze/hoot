class Api
{
    /**
     * @param {Login} component
     * @param {function} promiseOnSuccess
     * @param {function} promiseOnError
     */
    static login(component, promiseOnSuccess, promiseOnError)
    {
        // TODO: Set Cookie for Auth
        fetch(Config.getApiUrl() + 'login', {
            method     : 'POST',
            credentials: 'include',
            body       : JSON.stringify(component), headers: {
                'Accept': 'application/json', 'Content-Type': 'application/json; charset=UTF-8',
            },
        }).then(response => response.json()).then(promiseOnSuccess).catch(promiseOnError);
    }

    /**
     * @param {function} promiseOnSuccess
     * @param {function} promiseOnError
     */
    static logout(promiseOnSuccess, promiseOnError)
    {
        fetch(Config.getApiUrl() + 'logout', {
            method     : 'POST',
            credentials: 'include',
            headers    : {
                'Accept': 'application/json', 'Content-Type': 'application/json; charset=UTF-8',
            },
        }).then(response => response.json()).then(promiseOnSuccess).catch(promiseOnError);
    }

    /**
     * @param {Register} component
     * @param {function}    promiseOnSuccess
     * @param {function}    promiseOnError
     */
    static register(component, promiseOnSuccess, promiseOnError)
    {
        // TODO: Set Cookie for Auth
        fetch(Config.getApiUrl() + 'register', {
            method     : 'POST',
            credentials: 'include',
            body       : JSON.stringify(component), headers: {
                'Accept': 'application/json', 'Content-Type': 'application/json; charset=UTF-8',
            },
        }).then(response => response.json()).then(promiseOnSuccess).catch(promiseOnError);
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
        fetch(Config.getApiUrl() + 'user?id=' + userId, {
            method     : 'GET',
            credentials: 'include',
            headers    : {
                'Accept': 'application/json', 'Content-Type': 'application/json; charset=UTF-8',
            },
        })
            .then(response => response.json().then(response => Object.assign(new User(), response)))
            .then(promiseOnSuccess)
            .catch(promiseOnError);
    }

    /**
     * @param {function} promiseOnSuccess
     * @param {function} promiseOnError
     */
    static getUserMe(promiseOnSuccess, promiseOnError)
    {
        // TODO: Set Cookie for Auth
        fetch(Config.getApiUrl() + 'user/me', {
            method     : 'GET',
            credentials: 'include',
            headers    : {
                'Accept': 'application/json', 'Content-Type': 'application/json; charset=UTF-8',
            },
        })
            .then(response => response.json().then(response => Object.assign(new User(), response)))
            .then(promiseOnSuccess)
            .catch(promiseOnError);
    }

    /**
     * @param {int}      userIdToCheck
     * @param {function} promiseOnSuccess
     * @param {function} promiseOnError
     */
    static getMeFollowsUser(userIdToCheck, promiseOnSuccess, promiseOnError)
    {
        // TODO: Set Cookie for Auth
        fetch(Config.getApiUrl() + 'user/me/follows?userId=' + userIdToCheck, {
            method     : 'GET',
            credentials: 'include',
            headers    : {
                'Accept': 'application/json', 'Content-Type': 'application/json; charset=UTF-8',
            },
        }).then(response => response.json()).then(promiseOnSuccess).catch(promiseOnError);
    }

    /**
     * @param {User}  component
     * @param {function} promiseOnSuccess
     * @param {function} promiseOnError
     */
    static putUserMe(component, promiseOnSuccess, promiseOnError)
    {
        // TODO: Set Cookie for Auth
        fetch(Config.getApiUrl() + 'user/me', {
            method     : 'PUT',
            credentials: 'include',
            body       : JSON.stringify(component), headers: {
                'Accept': 'application/json', 'Content-Type': 'application/json; charset=UTF-8',
            },
        })
            .then(response => response.json().then(response => Object.assign(new User(), response)))
            .then(promiseOnSuccess)
            .catch(promiseOnError);
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
            method     : 'POST',
            credentials: 'include',
            body       : userIdToFollow, headers: {
                'Accept': 'application/json', 'Content-Type': 'application/json; charset=UTF-8',
            },
        }).then(response => response.json()).then(promiseOnSuccess).catch(promiseOnError);
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
            method     : 'POST',
            credentials: 'include',
            body       : userIdToUnfollow, headers: {
                'Accept': 'application/json', 'Content-Type': 'application/json; charset=UTF-8',
            },
        }).then(response => response.json()).then(promiseOnSuccess).catch(promiseOnError);
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
        })
            .then(response => response.json().then(response => Object.assign(new Followers(), response)))
            .then(promiseOnSuccess)
            .catch(promiseOnError);
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
        })
            .then(response => response.json().then(response => Object.assign(new Follows(), response)))
            .then(promiseOnSuccess)
            .catch(promiseOnError);
    }

    /**
     * @param {int|null} lastPostId
     * @param {int}      quantity
     * @param {String}   tags
     * @param {function} promiseOnSuccess
     * @param {function} promiseOnError
     */
    static getHootTimelineGlobal(lastPostId, quantity, tags, promiseOnSuccess, promiseOnError)
    {
        let url = Config.getApiUrl() + 'hoot/timeline/global?quantity=' + quantity;
        url += lastPostId ? '&lastPostId=' + lastPostId : '';
        url += tags ? '&tags=' + tags : '';

        fetch(url, {
            method: 'GET', headers: {
                'Accept': 'application/json', 'Content-Type': 'application/json; charset=UTF-8',
            },
        })// TODO: Convert hoots into correct types
            .then(response => response.json().then(response => Object.assign(new Hoots(), response)))
            .then(promiseOnSuccess)
            .catch(promiseOnError);
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
        let url = Config.getApiUrl() + 'hoot/timeline/mine?quantity=' + quantity;
        url += lastPostId ? '&lastPostId=' + lastPostId : '';
        url += tags ? '&tags=' + tags : '';

        fetch(url, {
            method: 'GET', headers: {
                'Accept': 'application/json', 'Content-Type': 'application/json; charset=UTF-8',
            },
        })// TODO: Convert hoots into correct types
            .then(response => response.json().then(response => Object.assign(new Hoots(), response)))
            .then(promiseOnSuccess)
            .catch(promiseOnError);
    }

    /**
     * @param {int|null}    lastPostId
     * @param {int}         quantity
     * @param {String|null} tags
     * @param {int|null}    userId
     * @param {function}    promiseOnSuccess
     * @param {function}    promiseOnError
     */
    static getHootSearch(lastPostId, quantity, tags, userId, promiseOnSuccess, promiseOnError)
    {
        let url = Config.getApiUrl() + 'hoot/search?quantity=' + quantity;

        url += lastPostId ? '&lastPostId=' + lastPostId : '';
        url += tags ? '&tags=' + tags : '';
        url += userId ? '&userId=' + userId : '';

        // TODO: Set Cookie for Auth
        fetch(url, {
            method: 'GET', headers: {
                'Accept': 'application/json', 'Content-Type': 'application/json; charset=UTF-8',
            },
        })// TODO: Convert hoots into correct types
            .then(response => response.json().then(response => Object.assign(new Hoots(), response)))
            .then(promiseOnSuccess)
            .catch(promiseOnError);
    }

    /**
     * @param {int}      hootId
     * @param {function} promiseOnSuccess
     * @param {function} promiseOnError
     */
    static deleteHoot(hootId, promiseOnSuccess, promiseOnError)
    {
        // TODO: Set Cookie for Auth
        fetch(Config.getApiUrl() + 'hoot?hootId=' + hootId, {
            method: 'DELETE', headers: {
                'Accept': 'application/json', 'Content-Type': 'application/json; charset=UTF-8',
            },
        }).then(response => response.json()).then(promiseOnSuccess).catch(promiseOnError);
    }

    /**
     * @param {HootPostSave} component
     * @param {function}     promiseOnSuccess
     * @param {function}     promiseOnError
     */
    static postHootPost(component, promiseOnSuccess, promiseOnError)
    {
        // TODO: Set Cookie for Auth
        fetch(Config.getApiUrl() + 'hoot/post', {
            method: 'POST', body: JSON.stringify(component), headers: {
                'Accept': 'application/json', 'Content-Type': 'application/json; charset=UTF-8',
            },
        })
            .then(response => response.json().then(response => Object.assign(new Post(), response)))
            .then(promiseOnSuccess)
            .catch(promiseOnError);
    }

    /**
     * @param {HootCommentSave} component
     * @param {function}        promiseOnSuccess
     * @param {function}        promiseOnError
     */
    static postHootComment(component, promiseOnSuccess, promiseOnError)
    {
        // TODO: Set Cookie for Auth
        fetch(Config.getApiUrl() + 'hoot/comment', {
            method: 'POST', body: JSON.stringify(component), headers: {
                'Accept': 'application/json', 'Content-Type': 'application/json; charset=UTF-8',
            },
        })
            .then(response => response.json().then(response => Object.assign(new Comment(), response)))
            .then(promiseOnSuccess)
            .catch(promiseOnError);
    }

    /**
     * @param {HootImageSave} component
     * @param {function}      promiseOnSuccess
     * @param {function}      promiseOnError
     */
    static postHootImage(component, promiseOnSuccess, promiseOnError)
    {
        // TODO: Set Cookie for Auth
        fetch(Config.getApiUrl() + 'hoot/image', {
            method: 'POST', body: JSON.stringify(component), headers: {
                'Accept': 'application/json', 'Content-Type': 'application/json; charset=UTF-8',
            },
        })
            .then(response => response.json().then(response => Object.assign(new Image(), response)))
            .then(promiseOnSuccess)
            .catch(promiseOnError);
    }

    /**
     * @param {ReactionMeSend} component
     * @param {function}       promiseOnSuccess
     * @param {function}       promiseOnError
     */
    static postHootReaction(component, promiseOnSuccess, promiseOnError)
    {
        // TODO: Set Cookie for Auth
        fetch(Config.getApiUrl() + 'hoot/me/reaction', {
            method: 'POST', body: JSON.stringify(component), headers: {
                'Accept': 'application/json', 'Content-Type': 'application/json; charset=UTF-8',
            },
        }).then(response => response.json()).then(promiseOnSuccess).catch(promiseOnError);
    }

    /**
     * @param {ReactionMeSend} component
     * @param {function}       promiseOnSuccess
     * @param {function}       promiseOnError
     */
    static deleteHootReaction(component, promiseOnSuccess, promiseOnError)
    {
        // TODO: Set Cookie for Auth
        fetch(Config.getApiUrl() + 'hoot/me/reaction', {
            method: 'DELETE', body: JSON.stringify(component), headers: {
                'Accept': 'application/json', 'Content-Type': 'application/json; charset=UTF-8',
            },
        }).then(response => response.json().then(response => response)).then(promiseOnSuccess).catch(promiseOnError);
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
        })
            .then(response => response.json().then(response => Object.assign(new GraphList(), response)))
            .then(promiseOnSuccess)
            .catch(promiseOnError);
    }
}